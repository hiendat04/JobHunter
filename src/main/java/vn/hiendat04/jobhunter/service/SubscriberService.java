package vn.hiendat04.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import vn.hiendat04.jobhunter.domain.Job;
import vn.hiendat04.jobhunter.domain.Skill;
import vn.hiendat04.jobhunter.domain.Subscriber;
import vn.hiendat04.jobhunter.domain.response.email.ResEmailJob;
import vn.hiendat04.jobhunter.repository.JobRepository;
import vn.hiendat04.jobhunter.repository.SkillRepository;
import vn.hiendat04.jobhunter.repository.SubscriberRepository;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository,
            JobRepository jobRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    public boolean checkEmailExists(String email) {
        return this.subscriberRepository.existsByEmail(email);
    }

    public Subscriber createSubscriber(Subscriber subscriber) {
        // Check skills
        if (subscriber.getSkills() != null) {
            List<Long> skillIds = subscriber.getSkills().stream().map(item -> item.getId())
                    .collect(Collectors.toList());
            List<Skill> skills = this.skillRepository.findAllById(skillIds);
            subscriber.setSkills(skills);
        }

        return this.subscriberRepository.save(subscriber);
    }

    public boolean checkIdExist(long id) {
        return this.subscriberRepository.existsById(id);
    }

    public Subscriber updateSubscriber(Subscriber subscriber) {
        // Get the current subscriber
        Optional<Subscriber> subOptional = this.subscriberRepository.findById(subscriber.getId());
        Subscriber currentSub = subOptional.get();

        if (currentSub != null) {
            // Check and update skills
            List<Long> skillIds = subscriber.getSkills().stream().map(item -> item.getId())
                    .collect(Collectors.toList());
            List<Skill> skills = this.skillRepository.findAllById(skillIds);
            currentSub.setSkills(skills);

            // Update subscriber
            currentSub = this.subscriberRepository.save(currentSub);
        }
        return currentSub;
    }

    public ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();
        List<ResEmailJob.SkillEmail> s = skills.stream().map(skill -> new ResEmailJob.SkillEmail(skill.getName()))
                .collect(Collectors.toList());
        res.setSkills(s);
        return res; 

    }

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {

                        List<ResEmailJob> arr = listJobs.stream().map(
                                job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());

                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                arr);
                    }
                }
            }
        }
    }

    public Subscriber getSubscriberByEmail(String email){
        return this.subscriberRepository.findByEmail(email);
    }
}
