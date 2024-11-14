package vn.hiendat04.jobhunter.service;

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
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public PermissionService(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    public boolean checkPermissionUnique(Permission permission) {
        boolean isApiPathExist = this.permissionRepository.existsByApiPath(permission.getApiPath());
        boolean isMethodExist = this.permissionRepository.existsByMethod(permission.getMethod());
        boolean isModuleExist = this.permissionRepository.existsByModule(permission.getModule());

        if (isApiPathExist & isMethodExist & isModuleExist) {
            return true;
        }
        return false;
    }

    public Permission createPermission(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    public boolean checkPermissionExist(long id) {
        return this.permissionRepository.existsById(id);
    }

    public Permission updatePermission(Permission permission) {
        Optional<Permission> permissionOptional = this.permissionRepository.findById(permission.getId());
        Permission currentPermission = permissionOptional.isPresent() ? permissionOptional.get() : null;
        if (currentPermission != null) {
            currentPermission.setName(permission.getName());
            currentPermission.setApiPath(permission.getApiPath());
            currentPermission.setMethod(permission.getMethod());
            currentPermission.setModule(permission.getModule());

            currentPermission = this.permissionRepository.save(currentPermission);
            return currentPermission;
        }
        return null;
    }

    public ResultPaginationDTO fetchAllPermissions(Specification<Permission> specification, Pageable pageable) {
        Page<Permission> pagePermission = this.permissionRepository.findAll(specification, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pagePermission.getNumber() + 1);
        meta.setPageSize(pagePermission.getSize());
        meta.setPages(pagePermission.getTotalPages());
        meta.setTotal(pagePermission.getNumberOfElements());
        res.setMeta(meta);

        res.setResult(pagePermission.getContent());

        return res;
    }

    public void deletePermission(long id) {
        // Delete permission in permission_role table
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        Permission currentPermission = permissionOptional.isPresent() ? permissionOptional.get() : null;
        currentPermission.getRoles().forEach(role -> role.getPermissions().remove(currentPermission));

        // Delete permission
        this.permissionRepository.deleteById(id);
    }

}
