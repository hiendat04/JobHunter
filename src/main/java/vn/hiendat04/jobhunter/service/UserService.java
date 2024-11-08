package vn.hiendat04.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.hiendat04.jobhunter.domain.User;
import vn.hiendat04.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    public List<User> fetchAllUser() {
        return this.userRepository.findAll();
    }

    public User updateUser(User user) {
        User currentUser = this.userRepository.findById(user.getId()).isPresent()
                ? this.userRepository.findById(user.getId()).get()
                : null;

        if (currentUser != null) {
            currentUser.setEmail(user.getEmail());
            currentUser.setName(user.getName());
            currentUser.setPassword(user.getPassword());
            currentUser = this.createUser(currentUser);
        }

        return currentUser;
    }

    public User getUserByUsername(String name) {
        return this.userRepository.findByEmail(name);
    }
}
