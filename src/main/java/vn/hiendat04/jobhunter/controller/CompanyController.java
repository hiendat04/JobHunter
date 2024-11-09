package vn.hiendat04.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hiendat04.jobhunter.domain.Company;
import vn.hiendat04.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hiendat04.jobhunter.service.CompanyService;
import vn.hiendat04.jobhunter.util.annotation.ApiMessage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company) {
        // TODO: process POST request
        Company newCompany = new Company();
        newCompany.setName(company.getName());
        newCompany.setDescription(company.getDescription());
        newCompany.setAddress(company.getAddress());
        newCompany.setLogo(company.getLogo());

        this.companyService.createCompany(newCompany);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
    }

    @GetMapping("/companies")
    @ApiMessage("fetch all companies")
    public ResponseEntity<ResultPaginationDTO> fetchAllCompanies(
            @Filter Specification<Company> specification,
            Pageable pageable) {

        ResultPaginationDTO companies = this.companyService.fetchAllCompanies(specification, pageable);
        return ResponseEntity.ok().body(companies);
    }

    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company company) {
        // TODO: process PUT request
        Company updatedCompany = this.companyService.updateCompany(company);
        return ResponseEntity.ok().body(updatedCompany);
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") long id) {
        this.companyService.deleteCompany(id);
        return ResponseEntity.ok().body(null);
    }

}
