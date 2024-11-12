package vn.hiendat04.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hiendat04.jobhunter.domain.Company;
import vn.hiendat04.jobhunter.domain.User;
import vn.hiendat04.jobhunter.domain.response.ResultPaginationDTO;
import vn.hiendat04.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserService userService;

    public CompanyService(CompanyRepository companyRepository, UserService userService) {
        this.companyRepository = companyRepository;
        this.userService = userService;
    }

    public Company createCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO fetchAllCompanies(Specification<Company> specification, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(specification, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageCompany.getNumber() + 1);
        meta.setPageSize(pageCompany.getSize());
        meta.setPages(pageCompany.getTotalPages());
        meta.setTotal(pageCompany.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(pageCompany.getContent());
        return resultPaginationDTO;
    }

    public Company fetchCompanyById(long id) {
        return this.companyRepository.findById(id).isPresent()
                ? this.companyRepository.findById(id).get()
                : null;
    }

    public Company updateCompany(Company company) {
        Company updatedCompany = this.fetchCompanyById(company.getId());

        updatedCompany.setName(company.getName());
        updatedCompany.setDescription(company.getDescription());
        updatedCompany.setAddress(company.getAddress());
        updatedCompany.setLogo(company.getLogo());

        return this.createCompany(updatedCompany);

    }

    public void deleteCompany(long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if (companyOptional.isPresent()) {
            Company com = companyOptional.get();

            // Fetch all users belong to the company
            List<User> users = this.userService.fetchUsersByCompany(com.getId());
            this.userService.deleteAll(users);
        }
        this.companyRepository.deleteById(id);
    }
}