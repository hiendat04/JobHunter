package vn.hiendat04.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hiendat04.jobhunter.domain.Skill;
import vn.hiendat04.jobhunter.domain.response.ResultPaginationDTO;
import vn.hiendat04.jobhunter.service.SkillService;
import vn.hiendat04.jobhunter.util.annotation.ApiMessage;
import vn.hiendat04.jobhunter.util.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("Create new skill successfully!")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        // Check if skill is existing
        boolean existingSkill = this.skillService.checkSkillExists(skill.getName());
        if (existingSkill && skill.getName() != null) {
            throw new IdInvalidException("Skill " + skill.getName() + " is existing!");
        }
        Skill newSkill = this.skillService.createSkill(skill);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSkill);
    }

    @PutMapping("/skills")
    @ApiMessage("Update skill successfully!")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        // Check if id exists
        if (this.skillService.fetchSkillById(skill.getId()) == null) {
            throw new IdInvalidException("Skill id = " + skill.getId() + " does not exists");
        }

        // Check if skill is existing
        boolean existingSkill = this.skillService.checkSkillExists(skill.getName());
        if (existingSkill) {
            throw new IdInvalidException("Skill " + skill.getName() + " is existing!");
        }
        Skill updatedSkill = this.skillService.updateSkill(skill);
        return ResponseEntity.ok().body(updatedSkill);
    }

    @GetMapping("/skills")
    @ApiMessage("Fetch all skills successfully!")
    public ResponseEntity<ResultPaginationDTO> getAllSkills(
            @Filter Specification<Skill> specification,
            Pageable pageable) {
        ResultPaginationDTO res = this.skillService.fetchAllSkills(specification, pageable);
        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete skill successfully")
    public ResponseEntity<Void> deleteSkill(@PathVariable long id) throws IdInvalidException {
        // Check if id exists
        if (this.skillService.fetchSkillById(id) == null) {
            throw new IdInvalidException("Skill id = " + id + " does not exists");
        }

        // Delete skill
        this.skillService.deleteSkill(id);
        return ResponseEntity.ok(null);
    }
}
