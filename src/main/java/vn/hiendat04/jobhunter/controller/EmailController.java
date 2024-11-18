package vn.hiendat04.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;
import vn.hiendat04.jobhunter.service.EmailService;
import vn.hiendat04.jobhunter.service.SubscriberService;
import vn.hiendat04.jobhunter.util.annotation.ApiMessage;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class EmailController {

    private final SubscriberService subscriberService;

    public EmailController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @GetMapping("/email")
    @ApiMessage("Send message")
    // @Scheduled(cron = "*/30 * * * * *")
    // @Transactional 
    public String sendEmail() {
        // this.emailService.sendSimpleEmail();
        // this.emailService.sendEmailSync("hiendat04@gmail.com", "TEST SPRING BOOT",
        // "<h1><b>HELLO</b></h1>", false,
        // true);
        this.subscriberService.sendSubscribersEmailJobs();
        return "Hello World";
    }

}
