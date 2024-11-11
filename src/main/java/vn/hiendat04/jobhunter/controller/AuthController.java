package vn.hiendat04.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.ipc.http.HttpSender.Response;
import jakarta.validation.Valid;
import vn.hiendat04.jobhunter.domain.User;
import vn.hiendat04.jobhunter.domain.dto.LoginDTO;
import vn.hiendat04.jobhunter.domain.dto.ResponseLoginDTO;
import vn.hiendat04.jobhunter.service.UserService;
import vn.hiendat04.jobhunter.util.SecurityUtil;
import vn.hiendat04.jobhunter.util.annotation.ApiMessage;
import vn.hiendat04.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    @Value("${hiendat04.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(
            AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil,
            UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    @ApiMessage("User login")
    public ResponseEntity<ResponseLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        // Send username and password to Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        // Authenticate User => Have to write loadUserByUsername function (in
        // UserDetailsCustom.java)
        Authentication authentication = this.authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Save user information in Security Context Holder for further usage
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseLoginDTO responseLoginDTO = new ResponseLoginDTO();

        User currentUser = this.userService.getUserByUsername(loginDTO.getUsername());

        if (currentUser != null) {
            ResponseLoginDTO.UserLogin user = new ResponseLoginDTO.UserLogin(
                    currentUser.getId(),
                    currentUser.getEmail(),
                    currentUser.getName());
            responseLoginDTO.setUser(user);
        }

        // Create access token
        String accessToken = this.securityUtil.createAccessToken(authentication.getName(), responseLoginDTO.getUser());
        responseLoginDTO.setAccessToken(accessToken);

        // Create refresh token
        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), responseLoginDTO);

        // Update user with refresh token
        this.userService.updateUserToken(refreshToken, loginDTO.getUsername());

        // Set cookie
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/") // to allow all APIs in the project to use cookie
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(responseLoginDTO);
    }

    @GetMapping("/auth/account")
    @ApiMessage("fetch account")
    public ResponseEntity<ResponseLoginDTO.UserLogin> FetchAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        User currentUser = this.userService.getUserByUsername(email);
        ResponseLoginDTO.UserLogin user = new ResponseLoginDTO.UserLogin();

        if (currentUser != null) {
            user.setId(currentUser.getId());
            user.setEmail(email);
            user.setName(currentUser.getName());
        }
        return ResponseEntity.ok().body(user);
    }

    // The refresh token is saved as long as user login
    @GetMapping("/auth/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<ResponseLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "unavailable") String refresh_token // Server get
                                                                                                    // refresh token
                                                                                                    // from cookie
    ) throws IdInvalidException {
        if (refresh_token.equals("unavailable")) {
            throw new IdInvalidException("Refresh token is unavailable in cookie");
        }

        // Server check if token is valid
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);

        // Get the object of the token, in this case, it is the email of the user
        String email = decodedToken.getSubject();

        // Check user by token + email (to make the process more secured)
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if (currentUser == null) {
            throw new IdInvalidException("Refresh token is invalid! ");
        }

        // Issue new token/ set refresh token as cookie
        ResponseLoginDTO responseLoginDTO = new ResponseLoginDTO();
        ResponseLoginDTO.UserLogin user = new ResponseLoginDTO.UserLogin(
                currentUser.getId(),
                currentUser.getEmail(),
                currentUser.getName());
        responseLoginDTO.setUser(user);

        // Create access token
        String accessToken = this.securityUtil.createAccessToken(email, responseLoginDTO.getUser());
        responseLoginDTO.setAccessToken(accessToken);

        // Create refresh token
        String newRefreshToken = this.securityUtil.createRefreshToken(email, responseLoginDTO);

        // Update user with refresh token
        this.userService.updateUserToken(newRefreshToken, email);

        // Set cookie
        ResponseCookie cookie = ResponseCookie.from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/") // to allow all APIs in the project to use cookie
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(responseLoginDTO);

    }

    @PostMapping("/auth/logout")
    @ApiMessage("User logout")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        // Get the email of current user
        String email = SecurityUtil.getCurrentUserLogin().get();

        if(email.equals(null)){
            throw new IdInvalidException("Access token is invalid");
        }

        // Set refresh cookie = null
        this.userService.updateUserToken(null, email);

        // Delete refresh token in cookie
        ResponseCookie deleteCookie = ResponseCookie
                .from("refresh_token", null)
                .maxAge(0)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

}
