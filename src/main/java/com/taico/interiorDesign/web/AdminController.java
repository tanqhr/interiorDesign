package com.taico.interiorDesign.web;

import com.taico.interiorDesign.enums.ProjectStatus;
import com.taico.interiorDesign.enums.ServiceType;
import com.taico.interiorDesign.model.entity.ProjectEntity;
import com.taico.interiorDesign.model.entity.UserEntity;
import com.taico.interiorDesign.repositories.ProjectRepository;
import com.taico.interiorDesign.service.ProjectService;
import com.taico.interiorDesign.service.ServiceSettingService;
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
import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ProjectService projectService;
    private final ServiceSettingService serviceSettingService;
    private final ProjectRepository projectRepository;

    public AdminController(UserService userService, ProjectService projectService, ServiceSettingService serviceSettingService, ProjectRepository projectRepository) {
        this.userService = userService;
        this.projectService = projectService;
        this.serviceSettingService = serviceSettingService;
        this.projectRepository = projectRepository;
    }

    @GetMapping
    public String dashboard(Model model) {

        List<ProjectEntity> latestProjects = projectRepository.findTop5ByOrderByCreatedAtDesc();
        model.addAttribute("latestProjects", latestProjects);

//        model.addAttribute(
//                "newProjects",
//                projectRepository.countByStatus(ProjectStatus.NEW)
//        );
//
//        model.addAttribute(
//                "waitingPayment",
//                projectRepository.countByStatus(ProjectStatus.PENDING_PAYMENT)
//        );
//
//        model.addAttribute(
//                "inProgress",
//                projectRepository.countByStatus(ProjectStatus.IN_PROGRESS)
//        );
//
//        model.addAttribute(
//                "completed",
//                projectRepository.countByStatus(ProjectStatus.COMPLETED)
//        );

        long newCount =
                projectRepository.countByStatus(ProjectStatus.NEW);

        long paymentCount =
                projectRepository.countByStatus(ProjectStatus.PENDING_PAYMENT);

        long progressCount =
                projectRepository.countByStatus(ProjectStatus.IN_PROGRESS);

        long completedCount =
                projectRepository.countByStatus(ProjectStatus.COMPLETED);

        model.addAttribute("newProjects", newCount);
        model.addAttribute("waitingPayment", paymentCount);
        model.addAttribute("inProgress", progressCount);
        model.addAttribute("completed", completedCount);
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

    @GetMapping("/services")
    public String services(Model model) {

        model.addAttribute(
                "services",
                serviceSettingService.findAll()
        );

        return "admin/services";
    }

    @PostMapping("/services/{serviceType}/activate")
    public String activateService(@PathVariable ServiceType serviceType) {

        serviceSettingService.activate(serviceType);

        return "redirect:/admin/services";
    }

    @PostMapping("/services/{serviceType}/deactivate")
    public String deactivateService(
            @PathVariable ServiceType serviceType) {

        serviceSettingService.deactivate(serviceType);

        return "redirect:/admin/services";
    }
}