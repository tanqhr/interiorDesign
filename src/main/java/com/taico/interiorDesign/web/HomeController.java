package com.taico.interiorDesign.web;


import com.taico.interiorDesign.model.entity.ProjectEntity;
import com.taico.interiorDesign.model.entity.UserEntity;
import com.taico.interiorDesign.repositories.ProjectRepository;
import com.taico.interiorDesign.repositories.UserRepository;
import com.taico.interiorDesign.security.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller

public class HomeController {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public HomeController(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String index() {

        return "index";
    }

//@GetMapping("/home")
//public String home(Model model,
//                   @AuthenticationPrincipal CurrentUser currentUser) {
//
//
//    model.addAttribute("firstName",
//            currentUser.getFirstName());


    //  return "home";
//}

    @GetMapping("/home")
    public String home(Model model,
                       @AuthenticationPrincipal CurrentUser currentUser) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        UserEntity user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ProjectEntity> projects =
                projectRepository.findByAuthor(user);

        model.addAttribute("projects", projects);
        model.addAttribute("firstName", user.getFirstName());

        return "home";
    }

}