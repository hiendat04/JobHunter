package vn.hiendat04.jobhunter.service;

import vn.hiendat04.jobhunter.domain.response.resume.ResResumeCreateDTO;
import vn.hiendat04.jobhunter.domain.response.resume.ResResumeFetchDTO;
import vn.hiendat04.jobhunter.domain.response.ResultPaginationDTO;
import vn.hiendat04.jobhunter.repository.ResumeRepository;
import vn.hiendat04.jobhunter.util.SecurityUtil;
import vn.hiendat04.jobhunter.domain.Resume;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

import vn.hiendat04.jobhunter.domain.response.resume.ResResumeUpdateDTO;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final FilterParser filterParser;
    private FilterSpecificationConverter filterSpecificationConverter;

    public ResumeService(ResumeRepository resumeRepository, FilterParser filterParser,
            FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeRepository = resumeRepository;
        this.filterParser = filterParser;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    public Resume createResume(Resume resume) {
        return this.resumeRepository.save(resume);
    }

    public ResResumeCreateDTO convertResResumeCreateDTO(Resume resume) {
        ResResumeCreateDTO res = new ResResumeCreateDTO();
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        res.setId(resume.getId());

        return res;
    }

    public boolean checkResumeExists(long id) {
        return this.resumeRepository.existsById(id);
    }

    public Resume updateResume(Resume resume) {
        Optional<Resume> resumeOptional = this.resumeRepository.findById(resume.getId());
        Resume currentResume = resumeOptional.isPresent() ? resumeOptional.get() : null;
        if (currentResume != null) {
            currentResume.setStatus(resume.getStatus());
            currentResume = this.resumeRepository.save(currentResume);
        }
        return currentResume;
    }

    public ResResumeUpdateDTO convertResResumeUpdateDTO(Resume resume) {
        ResResumeUpdateDTO res = new ResResumeUpdateDTO();
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());

        return res;
    }

    public void deleteResume(long id) {
        this.resumeRepository.deleteById(id);
    }

    public Optional<Resume> fetchResumeById(long id) {
        return this.resumeRepository.findById(id);
    }

    public ResResumeFetchDTO convertResResumeFetchDTO(Resume resume) {
        ResResumeFetchDTO res = new ResResumeFetchDTO();
        ResResumeFetchDTO.UserResume user = new ResResumeFetchDTO.UserResume();
        ResResumeFetchDTO.JobResume job = new ResResumeFetchDTO.JobResume();

        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        res.setEmail(resume.getEmail());
        res.setId(resume.getId());
        res.setStatus(resume.getStatus());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());
        res.setUrl(resume.getUrl());

        if (resume.getJob() != null) {
            res.setCompanyName(resume.getJob().getCompany().getName());
        }

        user.setId(resume.getUser().getId());
        user.setName(resume.getUser().getName());
        res.setUser(user);

        job.setId(resume.getJob().getId());
        job.setName(resume.getJob().getName());
        res.setJob(job);

        return res;

    }

    public ResultPaginationDTO fetchAllResumes(Specification<Resume> specification, Pageable pageable) {
        Page<Resume> pageResume = this.resumeRepository.findAll(specification, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        List<ResResumeFetchDTO> result = new ArrayList<>();

        meta.setPage(pageResume.getNumber() + 1);
        meta.setPageSize(pageResume.getSize());
        meta.setPages(pageResume.getTotalPages());
        meta.setTotal(pageResume.getNumberOfElements());
        res.setMeta(meta);

        for (Resume resume : pageResume.getContent()) {
            ResResumeFetchDTO item = this.convertResResumeFetchDTO(resume);
            result.add(item);
        }

        res.setResult(result);

        return res;
    }

    public ResultPaginationDTO fetchResumeByUser(Pageable pageable) {
        // Query builder
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        FilterNode node = this.filterParser.parse("email='" + email + "'");
        FilterSpecification<Resume> spec = this.filterSpecificationConverter.convert(node);
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);

        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageResume.getNumber() + 1);
        meta.setPageSize(pageResume.getSize());
        meta.setPages(pageResume.getTotalPages());
        meta.setTotal(pageResume.getNumberOfElements());

        res.setMeta(meta);
        res.setResult(pageResume.getContent());

        return res;

    }
}
