package com.taico.interiorDesign.web;

import com.taico.interiorDesign.model.entity.UserEntity;
import com.taico.interiorDesign.service.ProjectService;
import com.taico.interiorDesign.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;



@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ProjectService projectService;

    public AdminController(UserService userService, ProjectService projectService) {
        this.userService = userService;
        this.projectService = projectService;
    }

    @GetMapping
    public String dashboard() {
        return "admin/dashboard";
    }

    @PostMapping("/projects/{id}/design")
    public String uploadDesign(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {

        projectService.uploadDesignFile(
                id,
                file,
                authentication
        );

        return "redirect:/admin/projects/" + id;
    }


//    @PostMapping("/projects/{id}/delete")
//    public String deleteProject(
//            @PathVariable Long id) {
//
//        projectService.deleteUnpaidProject(id);
//
//        return "redirect:/admin/projects";
//    }

    @PostMapping("/projects/{id}/delete")
    public String deleteProject(@PathVariable Long id) {

        System.out.println("DELETE PROJECT ID = " + id);

        projectService.deleteUnpaidProject(id);

        System.out.println("PROJECT DELETED = " + id);

        return "redirect:/admin/projects";
    }

    @PostMapping("/users/{id}/deactivate")
    public String deactivateUser(
            @PathVariable Long id) {

        userService.deactivateUser(id);

        return "redirect:/admin/users";
    }

    @GetMapping("/users")
    public String users(Model model) {

        model.addAttribute(
                "users",
                userService.findAll()
        );

        return "admin/users";
    }



    @PostMapping("/users/{id}/activate")
    public String activateUser(
            @PathVariable Long id) {

        userService.activateUser(id);

        return "redirect:/admin/users";
    }


}