package com.taico.interiorDesign.web;


import com.taico.interiorDesign.service.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ContactController {

    private final EmailService emailService;

    public ContactController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }



    @PostMapping("/contact")
    public String sendMessage(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String subject,
            @RequestParam String message
    ) {

        emailService.sendContactMessage(
                name,
                email,
                subject,
                message
        );

        return "redirect:/contact?success";
    }
}
