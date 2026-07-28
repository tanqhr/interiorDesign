package com.taico.interiorDesign.web;

import com.taico.interiorDesign.model.entity.UserEntity;
import com.taico.interiorDesign.service.ProjectService;
import com.taico.interiorDesign.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
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

}