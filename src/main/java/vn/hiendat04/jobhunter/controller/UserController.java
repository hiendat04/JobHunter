package vn.hiendat04.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hiendat04.jobhunter.domain.User;
import vn.hiendat04.jobhunter.domain.Company;
import vn.hiendat04.jobhunter.domain.Role;
import vn.hiendat04.jobhunter.domain.response.ResultPaginationDTO;
import vn.hiendat04.jobhunter.domain.response.user.ResponseUserCreateDTO;
import vn.hiendat04.jobhunter.domain.response.user.ResponseUserDTO;
import vn.hiendat04.jobhunter.domain.response.user.ResponseUserUpdateDTO;
import vn.hiendat04.jobhunter.service.CompanyService;
import vn.hiendat04.jobhunter.service.RoleService;
import vn.hiendat04.jobhunter.service.UserService;
import vn.hiendat04.jobhunter.util.annotation.ApiMessage;
import vn.hiendat04.jobhunter.util.error.IdInvalidException;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1") // versioning API
public class UserController {
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    // @GetMapping("/users/create")
    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResponseUserCreateDTO> createNewUser(@Valid @RequestBody User user)
            throws IdInvalidException {

        // Check if email is existing
        if (this.userService.checkEmailExists(user.getEmail())) {
            throw new IdInvalidException("Email " + user.getEmail() + " is existing, please choose another email!");
        }

        // Check if role is existing
        if (user.getRole() != null) {
            Role role = this.roleService.fetchRoleById(user.getRole().getId());
            user.setRole(role != null ? role : null  );
        }

        // Hash password before create user
        String hashedPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        User newUser = this.userService.createUser(user);

        // Convert to DTO (not to show password)
        ResponseUserCreateDTO responseUserDTO = new ResponseUserCreateDTO();
        responseUserDTO.setAddress(newUser.getAddress());
        responseUserDTO.setAge(newUser.getAge());
        responseUserDTO.setCreatedAt(newUser.getCreatedAt());
        responseUserDTO.setEmail(newUser.getEmail());
        responseUserDTO.setGender(newUser.getGender());
        responseUserDTO.setId(newUser.getId());
        responseUserDTO.setName(newUser.getName());

        // Set company response data
        ResponseUserCreateDTO.CompanyCreate company = new ResponseUserCreateDTO.CompanyCreate();
        if (newUser.getCompany() != null) {
            company.setId(newUser.getCompany().getId());
            company.setName(newUser.getCompany().getName());
        }

        responseUserDTO.setCompany(company);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUserDTO);

    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        if (this.userService.fetchUserById(id).isPresent() == false) {
            throw new IdInvalidException("Id = " + id + " doest not exist!");
        }
        this.userService.deleteUserById(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Fetch a user by id")
    public ResponseEntity<ResponseUserDTO> fetchUserById(@PathVariable("id") long id) throws IdInvalidException {
        if (this.userService.fetchUserById(id).isPresent() == false) {
            throw new IdInvalidException("Id = " + id + " doest not exist!");
        }
        User user = this.userService.fetchUserById(id).get();

        // Convert to DTO (not to show password)
        ResponseUserDTO responseUserDTO = new ResponseUserDTO();
        responseUserDTO.setAddress(user.getAddress());
        responseUserDTO.setAge(user.getAge());
        responseUserDTO.setCreatedAt(user.getCreatedAt());
        responseUserDTO.setEmail(user.getEmail());
        responseUserDTO.setGender(user.getGender());
        responseUserDTO.setId(user.getId());
        responseUserDTO.setName(user.getName());
        responseUserDTO.setUpdatedAt(user.getUpdatedAt());

        // Set company response data
        ResponseUserDTO.CompanyFetch company = new ResponseUserDTO.CompanyFetch();
        if (user.getCompany() != null) {
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
        }

        responseUserDTO.setCompany(company);
        return ResponseEntity.status(HttpStatus.OK).body(responseUserDTO);
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all users")
    public ResponseEntity<ResultPaginationDTO> fetchAllUser(
            @Filter Specification<User> specification,
            Pageable pageable) {

        ResultPaginationDTO users = this.userService.fetchAllUser(specification, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @PutMapping("/users")
    @ApiMessage("Update user")
    public ResponseEntity<ResponseUserUpdateDTO> updateUser(@RequestBody User user) throws IdInvalidException {
        if (this.userService.fetchUserById(user.getId()).isPresent() == false) {
            throw new IdInvalidException("Id = " + user.getId() + " doest not exist!");
        }

        User updatedUser = this.userService.updateUser(user);
        ResponseUserUpdateDTO responseUserUpdateDTO = new ResponseUserUpdateDTO();
        ResponseUserUpdateDTO.CompanyUpdate company = new ResponseUserUpdateDTO.CompanyUpdate();

        // Convert to DTO (not to show email and password)
        responseUserUpdateDTO.setId(updatedUser.getId());
        responseUserUpdateDTO.setAddress(updatedUser.getAddress());
        responseUserUpdateDTO.setAge(updatedUser.getAge());
        responseUserUpdateDTO.setUpdatedAt(updatedUser.getUpdatedAt());
        responseUserUpdateDTO.setGender(updatedUser.getGender());
        responseUserUpdateDTO.setName(updatedUser.getName());

        // Set company response data
        if (user.getCompany() != null) {
            company.setId(updatedUser.getCompany().getId());
            company.setName(updatedUser.getCompany().getName());
        }

        responseUserUpdateDTO.setCompany(company);

        return ResponseEntity.status(HttpStatus.OK).body(responseUserUpdateDTO);
    }

}
