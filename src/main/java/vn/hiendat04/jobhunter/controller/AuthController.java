package vn.hiendat04.jobhunter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hiendat04.jobhunter.domain.User;
import vn.hiendat04.jobhunter.domain.dto.LoginDTO;
import vn.hiendat04.jobhunter.domain.dto.ResponseLoginDTO;
import vn.hiendat04.jobhunter.service.UserService;
import vn.hiendat04.jobhunter.util.SecurityUtil;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    public AuthController(
            AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil,
            UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        // Send username and password to Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        // Authenticate User => Have to write loadUserByUsername function (in
        // UserDetailsCustom.java)
        Authentication authentication = this.authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Create a token
        String accessToken = this.securityUtil.createToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication); // Save user information in Spring
                                                                              // Security Context Holder

        ResponseLoginDTO responseLoginDTO = new ResponseLoginDTO();

        User currentUser = this.userService.getUserByUsername(loginDTO.getUsername());

        if (currentUser != null) {
            ResponseLoginDTO.UserLogin user = new ResponseLoginDTO.UserLogin(
                    currentUser.getId(),
                    currentUser.getEmail(),
                    currentUser.getName()   );
            responseLoginDTO.setUser(user);
            responseLoginDTO.setAccessToken(accessToken);
        }
        return ResponseEntity.ok().body(responseLoginDTO);
    }
}
