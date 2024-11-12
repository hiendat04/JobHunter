package vn.hiendat04.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hiendat04.jobhunter.domain.Job;
import vn.hiendat04.jobhunter.domain.Skill;
import vn.hiendat04.jobhunter.domain.response.ResJobCreateDTO;
import vn.hiendat04.jobhunter.domain.response.ResJobUpdateDTO;
import vn.hiendat04.jobhunter.domain.response.ResultPaginationDTO;
import vn.hiendat04.jobhunter.repository.JobRepository;
import vn.hiendat04.jobhunter.repository.SkillRepository;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;

    public JobService(
            JobRepository jobRepository,
            SkillRepository skillRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
    }

    public Job createJob(Job job) {
        List<Long> skillId = new ArrayList<>();
        for (Skill skill : job.getSkills()) {
            skillId.add(skill.getId());
        }

        List<Skill> skills = this.skillRepository.findAllById(skillId);
        job.setSkills(skills);
        job = this.jobRepository.save(job);
        return job;
    }

    public ResJobCreateDTO convertResJobCreateDTO(Job job) {
        ResJobCreateDTO res = new ResJobCreateDTO();
        List<String> skills = new ArrayList<>();
        for (Skill skill : job.getSkills()) {
            skills.add(skill.getName());
        }

        res.setActive(job.isActive());
        res.setCreatedAt(job.getCreatedAt());
        res.setCreatedBy(job.getCreatedBy());
        res.setEndDate(job.getEndDate());
        res.setId(job.getId());
        res.setLevel(job.getLevel());
        res.setLocation(job.getLocation());
        res.setName(job.getName());
        res.setQuantity(job.getQuantity());
        res.setSalary(job.getSalary());
        res.setStartDate(job.getStartDate());
        res.setSkills(skills);

        return res;
    }

    public boolean checkExistJob(long id) {
        return this.jobRepository.existsById(id);
    }

    public Job updateJob(Job job) {
        Job currentJob = this.jobRepository.findById(job.getId()).get();

        if (currentJob != null) {
            currentJob.setActive(job.isActive());
            currentJob.setCompany(job.getCompany());
            currentJob.setDescription(job.getDescription());
            currentJob.setEndDate(job.getEndDate());
            currentJob.setStartDate(job.getStartDate());
            currentJob.setLevel(job.getLevel());
            currentJob.setLocation(job.getLocation());
            currentJob.setQuantity(job.getQuantity());
            currentJob.setName(job.getName());

            currentJob = this.jobRepository.save(currentJob);
        }
        return currentJob;
    }

    public ResJobUpdateDTO convertResJobUpdateDTO(Job job) {
        ResJobUpdateDTO res = new ResJobUpdateDTO();
        List<String> skills = new ArrayList<>();
        for (Skill skill : job.getSkills()) {
            skills.add(skill.getName());
        }

        res.setActive(job.isActive());
        res.setUpdatedAt(job.getUpdatedAt());
        res.setUpdatedBy(job.getUpdatedBy());
        res.setEndDate(job.getEndDate());
        res.setId(job.getId());
        res.setLevel(job.getLevel());
        res.setLocation(job.getLocation());
        res.setName(job.getName());
        res.setQuantity(job.getQuantity());
        res.setSalary(job.getSalary());
        res.setStartDate(job.getStartDate());
        res.setSkills(skills);

        return res;
    }

    public Optional<Job> fetchJobById(long id) {
        return this.jobRepository.findById(id);
    }

    public void deleteJob(long id) {
        this.jobRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAllJobs(Specification<Job> specification, Pageable pageable) {
        Page<Job> pageJob = this.jobRepository.findAll(specification, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageJob.getNumber() + 1);
        meta.setPageSize(pageJob.getSize());
        meta.setPages(pageJob.getTotalPages());
        meta.setTotal(pageJob.getNumberOfElements());

        res.setMeta(meta);
        res.setResult(pageJob.getContent());

        return res;

    }
}
