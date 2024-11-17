package vn.hiendat04.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import io.micrometer.core.ipc.http.HttpSender.Response;
import jakarta.validation.Valid;
import vn.hiendat04.jobhunter.domain.Permission;
import vn.hiendat04.jobhunter.domain.response.ResultPaginationDTO;
import vn.hiendat04.jobhunter.service.PermissionService;
import vn.hiendat04.jobhunter.util.annotation.ApiMessage;
import vn.hiendat04.jobhunter.util.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission)
            throws IdInvalidException {
        // Check if permission is unique
        boolean isPermissionUnique = this.permissionService.checkPermissionUnique(permission);
        if (isPermissionUnique) {
            throw new IdInvalidException("Permission must be unique!");
        }

        // Create new permission
        Permission newPermission = this.permissionService.createPermission(permission);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPermission);
    }

    @PutMapping("/permissions")
    public ResponseEntity<Permission> updatePermission(@RequestBody Permission permission) throws IdInvalidException {

        // Check permission exist
        if (!this.permissionService.checkPermissionExist(permission.getId())) {
            throw new IdInvalidException("Permission does not exist!");
        }

        // Check if permission is unique
        if (this.permissionService.checkPermissionUnique(permission)) {
            // Check name
            if (this.permissionService.isSameName(permission.getName()))
                throw new IdInvalidException("Permission must be unique!");
        }

        Permission updatedPermission = this.permissionService.updatePermission(permission);

        return ResponseEntity.ok().body(updatedPermission);
    }

    @GetMapping("/permissions")
    public ResponseEntity<ResultPaginationDTO> fetchAllPermissions(
            @Filter Specification<Permission> specification,
            Pageable pageable) {
        ResultPaginationDTO res = this.permissionService.fetchAllPermissions(specification, pageable);
        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete permission successfully!")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") long id) throws IdInvalidException {
        // Check if permission exist
        if (!this.permissionService.checkPermissionExist(id)) {
            throw new IdInvalidException("Permission id " + id + " does not exist");
        }
        this.permissionService.deletePermission(id);
        return ResponseEntity.ok(null);
    }
}
