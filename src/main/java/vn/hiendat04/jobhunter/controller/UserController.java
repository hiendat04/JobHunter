package vn.hiendat04.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import vn.hiendat04.jobhunter.domain.User;
import vn.hiendat04.jobhunter.service.UserService;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // @GetMapping("/user/create")
    @PostMapping("/user")
    public String createNewUser(@RequestBody User pUser) {
        this.userService.saveUser(pUser);
        return "create user";
    }

    @DeleteMapping("/user/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        this.userService.deleteUserById(id);
        return "datUser";
    }

    @GetMapping("/user/{id}")
    public User fetchUserById(@PathVariable("id") long id) {
        Optional<User> optUser = this.userService.fetchUserById(id);
        User user = optUser.isPresent() ? optUser.get() : null;
        return user;
    }

    @GetMapping("/user")
    public List<User> fetchAllUser() {
        List<User> users = this.userService.fetchAllUser();
        return users;
    }

    @PutMapping("/user")
    public User updatUser(@RequestBody User user) {
        return this.userService.updateUser(user);
    }

}
