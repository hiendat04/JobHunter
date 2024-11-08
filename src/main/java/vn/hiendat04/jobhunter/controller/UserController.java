package vn.hiendat04.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import vn.hiendat04.jobhunter.domain.User;
import vn.hiendat04.jobhunter.service.UserService;
import vn.hiendat04.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class UserController {
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // @GetMapping("/users/create")
    @PostMapping("/users")
    public ResponseEntity<User> createNewUser(@RequestBody User pUser) {
        String hashedPassword = this.passwordEncoder.encode(pUser.getPassword());
        pUser.setPassword(hashedPassword);
        User user = this.userService.createUser(pUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        this.userService.deleteUserById(id);
        if(id >= 1500){
            throw new IdInvalidException("Toang roi");
        }
        return ResponseEntity.ok("delete success");
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> fetchUserById(@PathVariable("id") long id) {
        Optional<User> optUser = this.userService.fetchUserById(id);
        User user = optUser.isPresent() ? optUser.get() : null;
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> fetchAllUser() {
        List<User> users = this.userService.fetchAllUser();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @PutMapping("/users")
    public ResponseEntity<User> updatUser(@RequestBody User user) {
        User updatedUser = this.userService.updateUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

}
