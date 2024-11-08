package vn.hiendat04.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hiendat04.jobhunter.domain.Company;
import vn.hiendat04.jobhunter.domain.RestResponse;
import vn.hiendat04.jobhunter.service.CompanyService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
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
}
