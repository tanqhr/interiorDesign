package com.taico.interiorDesign.web;

import com.taico.interiorDesign.model.dto.ProjectDetailsDTO;
import com.taico.interiorDesign.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final ProjectService projectService;

    @GetMapping("/{id:\\d+}")
    public String paymentPage(
            @PathVariable Long id,
            Model model
    ) {

        ProjectDetailsDTO project =
                projectService.getProjectById(id);

        model.addAttribute(
                "project",
                project
        );

        return "payment";
    }


    @PostMapping("/{id}")
    public String pay(
            @PathVariable Long id,
            @RequestParam String paymentMethod
    ) {

        projectService.payProject(
                id,
                paymentMethod
        );

        return "redirect:/payment/success";
    }


    @GetMapping("/success")
    public String paymentSuccess() {

        return "payment-success";
    }

}
