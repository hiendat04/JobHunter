package vn.hiendat04.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hiendat04.jobhunter.domain.Subscriber;
import vn.hiendat04.jobhunter.service.SubscriberService;
import vn.hiendat04.jobhunter.service.UserService;
import vn.hiendat04.jobhunter.util.SecurityUtil;
import vn.hiendat04.jobhunter.util.annotation.ApiMessage;
import vn.hiendat04.jobhunter.util.error.IdInvalidException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService, UserService userService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    public ResponseEntity<Subscriber> createSubscriber(@Valid @RequestBody Subscriber subscriber)
            throws IdInvalidException {

        // Check if subscriber is existing
        if (this.subscriberService.checkEmailExists(subscriber.getEmail())) {
            throw new IdInvalidException("Email is existing. Please choose another email!");
        }

        Subscriber newSubscriber = this.subscriberService.createSubscriber(subscriber);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSubscriber);
    }

    @PutMapping("/subscribers")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber subscriber)
            throws IdInvalidException {
        // Check if subscriber exist
        if (!this.subscriberService.checkIdExist(subscriber.getId())) {
            throw new IdInvalidException("Subscriber Id = " + subscriber.getId() + " does not exist!");
        }

        Subscriber updatedSubscriber = this.subscriberService.updateSubscriber(subscriber);

        return ResponseEntity.ok().body(updatedSubscriber);
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("Get subscriber's skills")
    public ResponseEntity<Subscriber> getSubscriberSkills() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : " ";

        return ResponseEntity.ok().body(this.subscriberService.getSubscriberByEmail(email));
    }

}
