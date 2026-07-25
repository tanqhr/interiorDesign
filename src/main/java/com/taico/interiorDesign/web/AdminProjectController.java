package com.taico.interiorDesign.web;

import com.taico.interiorDesign.enums.ProjectStatus;
import com.taico.interiorDesign.model.dto.ProjectUpdateDTO;
import com.taico.interiorDesign.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/admin/projects")
public class AdminProjectController {


    private final ProjectService projectService;


    public AdminProjectController(ProjectService projectService) {

        this.projectService = projectService;
    }



    @GetMapping
    public String allProjects(Model model) {


        model.addAttribute(
                "projects",
                projectService.findAll()
        );


        return "admin/projects";
    }




    @GetMapping("/{id}")
    public String projectDetails(@PathVariable Long id,
                                 Model model) {

        model.addAttribute(
                "project",
                projectService.getProjectDetails(id)
        );

        model.addAttribute(
                "statuses",
                ProjectStatus.values()
        );

        return "admin/project-details";
    }

    @PostMapping("/{id}")
    public String updateProject(
            @PathVariable Long id,
            ProjectUpdateDTO dto) {

        projectService.updateProject(id, dto);

        return "redirect:/admin/projects/" + id;
    }

}