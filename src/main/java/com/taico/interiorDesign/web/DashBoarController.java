package com.taico.interiorDesign.web;

import com.taico.interiorDesign.model.entity.ProjectEntity;
import com.taico.interiorDesign.security.CurrentUser;
import com.taico.interiorDesign.service.ProjectService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashBoarController {
private final ProjectService projectService;

    public DashBoarController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();

        List<ProjectEntity> projects =
                projectService.findByAuthor(currentUser.getId());

        model.addAttribute("projects", projects);
        model.addAttribute("firstName", currentUser.getFirstName());

        return "home";
    }
}
