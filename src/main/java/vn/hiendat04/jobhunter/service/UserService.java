package vn.hiendat04.jobhunter.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hiendat04.jobhunter.domain.User;
import vn.hiendat04.jobhunter.domain.response.ResponseUserDTO;
import vn.hiendat04.jobhunter.domain.response.ResultPaginationDTO;
import vn.hiendat04.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean checkEmailExists(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public User createUser(User user) {
        return this.userRepository.save(user);
    }

    public void deleteUserById(long id) {
        this.userRepository.deleteById(id);
    }

    public Optional<User> fetchUserById(long id) {
        return this.userRepository.findById(id);
    }

    public ResultPaginationDTO fetchAllUser(Specification<User> specification, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(specification, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        ArrayList<ResponseUserDTO> users = new ArrayList<>();

        resultPaginationDTO.setMeta(meta);

        for (User user : pageUser.getContent()) {
            ResponseUserDTO responseUserDTO = new ResponseUserDTO();

            responseUserDTO.setAddress(user.getAddress());
            responseUserDTO.setAge(user.getAge());
            responseUserDTO.setCreatedAt(user.getCreatedAt());
            responseUserDTO.setEmail(user.getEmail());
            responseUserDTO.setGender(user.getGender());
            responseUserDTO.setId(user.getId());
            responseUserDTO.setName(user.getName());
            responseUserDTO.setUpdatedAt(user.getUpdatedAt());

            users.add(responseUserDTO);
        }

        resultPaginationDTO.setResult(users);

        return resultPaginationDTO;
    }

    public User updateUser(User user) {
        User currentUser = this.fetchUserById(user.getId()).get();

        if (currentUser != null) {
            currentUser.setGender(user.getGender());
            currentUser.setAge(user.getAge());
            currentUser.setAddress(user.getAddress());
            currentUser.setName(user.getName());

            currentUser = this.userRepository.save(currentUser);
        }

        return currentUser;
    }

    public User getUserByUsername(String name) {
        return this.userRepository.findByEmail(name);
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.getUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
