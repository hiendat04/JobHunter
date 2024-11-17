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
import vn.hiendat04.jobhunter.util.error.IdInvalidException;

public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    // Configure more for the authorization base on the model (Frontend may not
    // cover all situation):
    // Request => Spring Security => INTERCEPTOR => Controller => Service ....
    // This file is INTERCEPTOR.
    @Override
    @Transactional
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
                    System.out.println(">>> is Allowed: " + isAllowed);
                    if (isAllowed == false) {
                        throw new IdInvalidException("You are not allowed to access this endpoint!");
                    }
                } else {
                    throw new IdInvalidException("You are not allowed to access this endpoint!");
                }
            }
        }
        return true;
    }
}