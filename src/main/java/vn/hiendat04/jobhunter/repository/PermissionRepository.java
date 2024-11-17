package vn.hiendat04.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.hiendat04.jobhunter.domain.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission>{
    boolean existsByApiPath(String apiPath);
    boolean existsByMethod(String method);
    boolean existsByModule(String module);
    boolean existsByName(String name);
}
