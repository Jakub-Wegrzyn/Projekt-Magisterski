package com.projekt.magisterski.backend.energy.web;

import com.projekt.magisterski.backend.energy.web.dto.KontaktDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/kontakt")
public class KontaktController {

    @Autowired
    private JavaMailSender emailSender;

    @ModelAttribute("contact")
    public KontaktDto kontaktDto() {
        return new KontaktDto();
    }

    @GetMapping
    public String kontakt() {
        return "kontakt";
    }

    @PostMapping
    public String kontaktPost(@ModelAttribute("contact") KontaktDto kontaktDto) {
        boolean validation_email = sendEmail(kontaktDto);
        if (validation_email) {
            return "redirect:/kontakt?success";
        } else {
            return "redirect:/kontakt?failure";
        }
    }

    private boolean sendEmail(KontaktDto kontaktDto) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("energy.praca.magisterska@gmail.com", kontaktDto.getEmail());
            message.setSubject(kontaktDto.getSubject() + " - " + kontaktDto.getFullname());
            message.setText(kontaktDto.getMessage());
            emailSender.send(message);
            return true;
        } catch (MailAuthenticationException exc) {
            return false;
        }
    }
}
