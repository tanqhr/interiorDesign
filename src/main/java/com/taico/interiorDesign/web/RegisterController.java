package com.taico.interiorDesign.web;

import com.taico.interiorDesign.service.EmailService;
import jakarta.validation.Valid;
import com.taico.interiorDesign.model.dto.UserRegisterDTO;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.taico.interiorDesign.service.UserService;

@Controller
@RequestMapping("/users")
public class RegisterController {

    private final UserService userService;
    private final EmailService emailService;

    public RegisterController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @ModelAttribute("userRegisterDTO")
    public UserRegisterDTO initRegisterDTO() {
        return new UserRegisterDTO();
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("userRegisterDTO") UserRegisterDTO userRegisterDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            redirectAttributes.addFlashAttribute(
                    "userRegisterDTO",
                    userRegisterDTO
            );

            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.userRegisterDTO",
                    bindingResult
            );

            return "redirect:/users/register";
        }

        try {

            // 1. Създаваме потребителя
            userService.register(userRegisterDTO);

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "userRegisterDTO",
                    userRegisterDTO
            );

            redirectAttributes.addFlashAttribute(
                    "registerError",
                    ex.getMessage()
            );

            return "redirect:/users/register";
        }


        try {

            emailService.sendRegistrationEmail(
                    userRegisterDTO.getEmail(),
                    userRegisterDTO.getFirstName()
            );

        } catch (Exception ex) {

            // Не проваляме регистрацията
            System.err.println(
                    "Неуспешно изпращане на регистрационен имейл: "
                            + ex.getMessage()
            );
     }

        // 3. Регистрацията е успешна независимо от имейла
        return "redirect:/users/login";
    }
}