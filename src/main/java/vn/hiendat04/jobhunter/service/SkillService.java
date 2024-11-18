package vn.hiendat04.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hiendat04.jobhunter.domain.Skill;
import vn.hiendat04.jobhunter.domain.response.ResultPaginationDTO;
import vn.hiendat04.jobhunter.repository.SkillRepository;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill fetchSkillById(long id) {
        Skill skill = this.skillRepository.findById(id).isPresent()
                ? this.skillRepository.findById(id).get()
                : null;
        return skill;
    }

    public Skill createSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public boolean checkSkillExists(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Skill updateSkill(Skill skill) {
        Skill currentSkill = this.fetchSkillById(skill.getId());

        if (currentSkill != null) {
            currentSkill.setName(skill.getName());
            currentSkill = this.skillRepository.save(currentSkill);
        }
        return currentSkill;

    }

    public ResultPaginationDTO fetchAllSkills(Specification<Skill> specification, Pageable pageable) {
        Page<Skill> pageSkill = this.skillRepository.findAll(specification, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageSkill.getNumber() + 1);
        meta.setPageSize(pageSkill.getSize());
        meta.setPages(pageSkill.getTotalPages());
        meta.setTotal(pageSkill.getTotalElements());

        res.setMeta(meta);
        res.setResult(pageSkill.getContent());

        return res;
    }

    public void deleteSkill(long id) {
        // Delete job (inside job_skill table)
        Skill currentSkill = this.fetchSkillById(id);
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));

        // Delete skill (inside subscriber_skill table);
        currentSkill.getSubscribers().forEach(subscriber -> subscriber.getSkills().remove(currentSkill));

        // Then delete skill
        this.skillRepository.delete(currentSkill);
    }
}
