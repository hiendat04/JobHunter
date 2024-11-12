package vn.hiendat04.jobhunter.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.hiendat04.jobhunter.domain.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long>, JpaSpecificationExecutor<Skill>  {
    boolean existsByName(String name);

    Page<Skill> findAll(Specification<Skill> specification, Pageable pageable);

    List<Skill> findByIdIn(List<Long> id);
}
