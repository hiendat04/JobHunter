package vn.hiendat04.jobhunter.service;

import org.springframework.stereotype.Service;

import vn.hiendat04.jobhunter.domain.Company;
import vn.hiendat04.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository){
        this.companyRepository = companyRepository;
    }

    public Company createCompany(Company company){
       return  this.companyRepository.save(company);
    }
}
