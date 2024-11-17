package vn.hiendat04.jobhunter.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.hiendat04.jobhunter.domain.Permission;
import vn.hiendat04.jobhunter.domain.Role;
import vn.hiendat04.jobhunter.domain.User;
import vn.hiendat04.jobhunter.service.UserService;
import vn.hiendat04.jobhunter.util.SecurityUtil;
import vn.hiendat04.jobhunter.util.error.PermissionException;

public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    // Configure more for the authorization base on the model (Frontend may not
    // cover all situation):
    // Request => Spring Security => INTERCEPTOR => Controller => Service ....
    // This file is INTERCEPTOR.
    @Override
    @Transactional // Because we set up the fetch type in our model is LAZY, which means we just
                   // only can query the data when we "code" not by default. However, to query the
                   // data, we need to go to the Controller to access the Service. But we are
                   // currently in the Interceptor, we cannot use the Service before the
                   // Controller. To solve this, we need to tell the Java Spring to create a
                   // temporary session to allow us to query data we want in the database. When we
                   // finish query, the session will be removed, and then we can go to the
                   // Controller (is we pass the Interceptor here!)
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);

        // Check permission
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        if (email != null && !email.isEmpty()) {
            User user = userService.getUserByUsername(email);
            if (user != null) {
                Role role = user.getRole();
                if (role != null) {
                    List<Permission> permissions = role.getPermissions();
                    // Check if the request of the user (to access the API path and its Method)
                    // match with the permission in the Database
                    boolean isAllowed = permissions.stream()
                            .anyMatch(item -> item.getApiPath().equals(path) && item.getMethod().equals(httpMethod));
                    if (isAllowed == false) {
                        throw new PermissionException("You are not allowed to access this endpoint!");
                    }
                } else {
                    throw new PermissionException("You are not allowed to access this endpoint!");
                }
            }
        }
        return true;
    }
}