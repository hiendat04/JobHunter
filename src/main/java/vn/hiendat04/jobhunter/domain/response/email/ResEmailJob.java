package vn.hiendat04.jobhunter.domain.response.email;

import java.util.List;

public class ResEmailJob {
    private String name;
    private double salary;
    private CompanyEmail company;
    private List<SkillEmail> skills;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public CompanyEmail getCompany() {
        return company;
    }

    public void setCompany(CompanyEmail company) {
        this.company = company;
    }

    public List<SkillEmail> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillEmail> skills) {
        this.skills = skills;
    }

    public static class CompanyEmail {
        private String name;

        public CompanyEmail(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public static class SkillEmail {
        private String name;

        
        public SkillEmail(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

}
