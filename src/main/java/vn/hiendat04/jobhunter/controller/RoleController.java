package vn.hiendat04.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.hiendat04.jobhunter.domain.Role;
import vn.hiendat04.jobhunter.domain.response.ResultPaginationDTO;
import vn.hiendat04.jobhunter.service.RoleService;
import vn.hiendat04.jobhunter.util.annotation.ApiMessage;
import vn.hiendat04.jobhunter.util.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create role successfully!")
    public ResponseEntity<Role> createRole(@RequestBody Role role) throws IdInvalidException {

        // Check role name exist
        if (this.roleService.checkRoleNameExist(role.getName())) {
            throw new IdInvalidException("Role " + role.getName() + " is existing!");
        }

        Role newRole = this.roleService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRole);
    }

    @PutMapping("/roles")
    @ApiMessage("Update role successfully!")
    public ResponseEntity<Role> updateRole(@RequestBody Role role) throws IdInvalidException {

        // Check role exist
        if (!this.roleService.checkRoleIdExist(role.getId())) {
            throw new IdInvalidException("Role id " + role.getId() + " does not exist!");
        }

        // Check role name exist
        // if (this.roleService.checkRoleNameExist(role.getName())) {
        //     throw new IdInvalidException("Role " + role.getName() + " is existing!");
        // }

        Role updatedRole = this.roleService.updateRole(role);
        return ResponseEntity.ok().body(updatedRole);
    }

    @GetMapping("/roles")
    @ApiMessage("Fetch role successfully!")
    public ResponseEntity<ResultPaginationDTO> fetchAllRoles(
            @Filter Specification<Role> specification,
            Pageable pageable) {

        ResultPaginationDTO res = this.roleService.fetchAllRoles(specification, pageable);
        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete role successfully!")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") long id) throws IdInvalidException {
        // Check if role exist
        if (!this.roleService.checkRoleIdExist(id)) {
            throw new IdInvalidException("Role id " + id + " does not exist");
        }
        this.roleService.deleteRole(id);
        return ResponseEntity.ok(null);
    }

}
