package vn.hiendat04.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hiendat04.jobhunter.domain.Permission;
import vn.hiendat04.jobhunter.domain.Role;
import vn.hiendat04.jobhunter.domain.response.ResultPaginationDTO;
import vn.hiendat04.jobhunter.repository.PermissionRepository;
import vn.hiendat04.jobhunter.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean checkRoleNameExist(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role createRole(Role role) {
        // Check permission
        List<Long> permission_id = new ArrayList<>();
        for (Permission p : role.getPermissions()) {
            permission_id.add(p.getId());
        }
        List<Permission> permissions = this.permissionRepository.findAllById(permission_id);
        role.setPermissions(permissions);

        // Create role
        return this.roleRepository.save(role);
    }

    public boolean checkRoleIdExist(long id) {
        return this.roleRepository.existsById(id);
    }

    public Role updateRole(Role role) {
        Optional<Role> roleOptional = this.roleRepository.findById(role.getId());
        Role currentRole = roleOptional.isPresent() ? roleOptional.get() : null;

        if (currentRole != null) {
            List<Long> permission_id = new ArrayList<>();
            for (Permission p : role.getPermissions()) {
                permission_id.add(p.getId());
            }
            List<Permission> permissions = this.permissionRepository.findAllById(permission_id);

            currentRole.setPermissions(permissions);
            currentRole.setName(role.getName());
            currentRole.setDescription(role.getDescription());
            currentRole.setActive(role.isActive());

            // Update role
            currentRole = this.roleRepository.save(currentRole);
            return currentRole;
        }
        return null;
    }

    public ResultPaginationDTO fetchAllRoles(Specification<Role> specification, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(specification, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageRole.getNumber() + 1);
        meta.setPageSize(pageRole.getSize());
        meta.setPages(pageRole.getTotalPages());
        meta.setTotal(pageRole.getNumberOfElements());
        res.setMeta(meta);

        res.setResult(pageRole.getContent());

        return res;
    }

    public void deleteRole(long id) {
        this.roleRepository.deleteById(id);
    }
}
