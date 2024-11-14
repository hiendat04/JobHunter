package vn.hiendat04.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.hiendat04.jobhunter.domain.Job;
import vn.hiendat04.jobhunter.domain.response.ResultPaginationDTO;
import vn.hiendat04.jobhunter.domain.response.job.ResJobCreateDTO;
import vn.hiendat04.jobhunter.domain.response.job.ResJobUpdateDTO;
import vn.hiendat04.jobhunter.service.JobService;
import vn.hiendat04.jobhunter.util.annotation.ApiMessage;
import vn.hiendat04.jobhunter.util.error.IdInvalidException;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create job successfully!")
    public ResponseEntity<ResJobCreateDTO> createJob(@RequestBody Job job) {
        Job newJob = this.jobService.createJob(job);
        ResJobCreateDTO res = this.jobService.convertResJobCreateDTO(newJob);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/jobs")
    public ResponseEntity<ResJobUpdateDTO> updateJob(@RequestBody Job job) throws IdInvalidException {

        // Check if job exists
        boolean jobExists = this.jobService.checkExistJob(job.getId());
        if (!jobExists) {
            throw new IdInvalidException("Job is not found!");
        }

        Job updatedJob = this.jobService.updateJob(job);
        ResJobUpdateDTO res = this.jobService.convertResJobUpdateDTO(updatedJob);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete job successfully!")
    public ResponseEntity<Void> deleteJob(@PathVariable("id") long id) throws IdInvalidException {
        // Check if job exists
        boolean jobExists = this.jobService.checkExistJob(id);
        if (!jobExists) {
            throw new IdInvalidException("Job is not found!");
        }

        this.jobService.deleteJob(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<Job> fetchJob(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> jobOptional = this.jobService.fetchJobById(id);
        Job job = jobOptional.isPresent() ? jobOptional.get() : null;

        if (job == null) {
            throw new IdInvalidException("Job is not found!");
        }

        return ResponseEntity.ok().body(job);
    }

    @GetMapping("/jobs")
    @ApiMessage("Get all jobs successfully")
    public ResponseEntity<ResultPaginationDTO> fetchAllJobs(
            @Filter Specification<Job> specification,
            Pageable pageable) {
        ResultPaginationDTO res = this.jobService.fetchAllJobs(specification, pageable);
        return ResponseEntity.ok().body(res);
    }

}
