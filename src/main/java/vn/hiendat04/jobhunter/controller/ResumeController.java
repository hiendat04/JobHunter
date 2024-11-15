package vn.hiendat04.jobhunter.controller;

import vn.hiendat04.jobhunter.domain.response.resume.ResResumeCreateDTO;
import vn.hiendat04.jobhunter.domain.response.resume.ResResumeUpdateDTO;
import vn.hiendat04.jobhunter.domain.response.resume.ResResumeFetchDTO;
import vn.hiendat04.jobhunter.domain.response.ResultPaginationDTO;
import vn.hiendat04.jobhunter.domain.Resume;
import vn.hiendat04.jobhunter.service.UserService;
import vn.hiendat04.jobhunter.service.JobService;
import vn.hiendat04.jobhunter.service.ResumeService;
import vn.hiendat04.jobhunter.util.error.IdInvalidException;
import vn.hiendat04.jobhunter.util.annotation.ApiMessage;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

import org.hibernate.transform.ResultTransformer;
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
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final UserService userService;
    private final JobService jobService;
    private final ResumeService resumeService;

    public ResumeController(UserService userService, JobService jobService, ResumeService resumeService) {
        this.userService = userService;
        this.jobService = jobService;
        this.resumeService = resumeService;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create resume successfully")
    public ResponseEntity<ResResumeCreateDTO> createResume(@Valid @RequestBody Resume resume)
            throws IdInvalidException {

        // Check if user id or job id exists
        boolean isUserExists = this.userService.checkUserExists(resume.getUser().getId());
        boolean isJobExists = this.jobService.checkExistJob(resume.getJob().getId());
        if (!isUserExists || !isJobExists) {
            throw new IdInvalidException("User Id/ Job Id is not found!");
        }

        Resume newResume = this.resumeService.createResume(resume);
        ResResumeCreateDTO res = this.resumeService.convertResResumeCreateDTO(newResume);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("resumes")
    @ApiMessage("Update resume successfully!")
    public ResponseEntity<ResResumeUpdateDTO> updateResume(@RequestBody Resume resume) throws IdInvalidException {

        // Check if resume exists
        boolean isResumeExist = this.resumeService.checkResumeExists(resume.getId());
        if (!isResumeExist) {
            throw new IdInvalidException("Resume with id = " + resume.getId() + " does not exist!");
        }

        Resume updatedResume = this.resumeService.updateResume(resume);
        ResResumeUpdateDTO res = this.resumeService.convertResResumeUpdateDTO(updatedResume);

        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete resume successfully!")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") long id) throws IdInvalidException {
        // Check if resume exists
        boolean isResumeExist = this.resumeService.checkResumeExists(id);
        if (!isResumeExist) {
            throw new IdInvalidException("Resume with id = " + id + " does not exist!");
        }

        this.resumeService.deleteResume(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Fetch user successfully!")
    public ResponseEntity<ResResumeFetchDTO> fetchUser(@PathVariable("id") long id) throws IdInvalidException {
        // Check if resume exists
        boolean isResumeExist = this.resumeService.checkResumeExists(id);
        if (!isResumeExist) {
            throw new IdInvalidException("Resume with id = " + id + " does not exist!");
        }
        Resume resume = this.resumeService.fetchResumeById(id).get();
        ResResumeFetchDTO res = this.resumeService.convertResResumeFetchDTO(resume);

        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/resumes")
    public ResponseEntity<ResultPaginationDTO> fetchAllResumes(
            @Filter Specification<Resume> specification,
            Pageable pageable) {
        ResultPaginationDTO res = this.resumeService.fetchAllResumes(specification, pageable);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/resumes/by-user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable) {
        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }
    

}
