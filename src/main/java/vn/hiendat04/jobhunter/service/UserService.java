package vn.hiendat04.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hiendat04.jobhunter.domain.User;
import vn.hiendat04.jobhunter.domain.Company;
import vn.hiendat04.jobhunter.domain.Resume;
import vn.hiendat04.jobhunter.domain.Role;
import vn.hiendat04.jobhunter.domain.response.ResultPaginationDTO;
import vn.hiendat04.jobhunter.domain.response.user.ResponseUserDTO;
import vn.hiendat04.jobhunter.repository.UserRepository;
import vn.hiendat04.jobhunter.repository.CompanyRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, CompanyRepository companyRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.roleService = roleService;
    }

    public boolean checkEmailExists(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public boolean checkUserExists(long id) {
        return this.userRepository.existsById(id);
    }

    public User createUser(User user) {

        // Check company
        if (user.getCompany() != null) {
            Optional<Company> companyOptional = this.companyRepository.findById(user.getCompany().getId());
            user.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
        }
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
            ResponseUserDTO.CompanyFetch company = new ResponseUserDTO.CompanyFetch();
            ResponseUserDTO.RoleFetch role = new ResponseUserDTO.RoleFetch();

            responseUserDTO.setAddress(user.getAddress());
            responseUserDTO.setAge(user.getAge());
            responseUserDTO.setCreatedAt(user.getCreatedAt());
            responseUserDTO.setEmail(user.getEmail());
            responseUserDTO.setGender(user.getGender());
            responseUserDTO.setId(user.getId());
            responseUserDTO.setName(user.getName());
            responseUserDTO.setUpdatedAt(user.getUpdatedAt());

            // Check company
            if (user.getCompany() != null) {
                company.setId(user.getCompany().getId());
                company.setName(user.getCompany().getName());
            }

            // Check role
            if (user.getRole() != null) {
                role.setId(user.getRole().getId());
                role.setName(user.getRole().getName());

            }
            responseUserDTO.setRole(role);
            responseUserDTO.setCompany(company);
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

            // Check company
            if (user.getCompany() != null) {
                Optional<Company> companyOptional = this.companyRepository.findById(user.getCompany().getId());
                currentUser.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
            }

            // Check role
            if (user.getRole() != null) {
                Role role = this.roleService.fetchRoleById(user.getRole().getId());
                currentUser.setRole(role != null ? role : null);
            }

            // Update
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

    public List<User> fetchUsersByCompany(long id) {
        return this.userRepository.findByCompanyId(id);
    }

    public void deleteAll(List<User> users) {
        this.userRepository.deleteAll(users);
    }

}
