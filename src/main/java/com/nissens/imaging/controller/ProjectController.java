package com.nissens.imaging.controller;

import com.nissens.imaging.entity.ProductCategory;
import com.nissens.imaging.entity.ProductProject;
import com.nissens.imaging.entity.ReferenceImage;
import com.nissens.imaging.entity.StylePreset;
import com.nissens.imaging.repository.ProductProjectRepository;
import com.nissens.imaging.repository.ReferenceImageRepository;
import com.nissens.imaging.service.FileStorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProductProjectRepository projectRepository;
    private final ReferenceImageRepository referenceImageRepository;
    private final FileStorageService fileStorageService;

    public ProjectController(ProductProjectRepository projectRepository,
                             ReferenceImageRepository referenceImageRepository,
                             FileStorageService fileStorageService) {
        this.projectRepository = projectRepository;
        this.referenceImageRepository = referenceImageRepository;
        this.fileStorageService = fileStorageService;
    }
    @GetMapping
    public String listProjects(Model model) {
        model.addAttribute("projects", projectRepository.findAll());
        return "project-list";
    }   
    
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("project", new ProductProject());
        model.addAttribute("styles", StylePreset.values());
        model.addAttribute("categories", ProductCategory.values());
        return "project-create";
    }

    @PostMapping
    public String createProject(@ModelAttribute ProductProject project) {
        ProductProject saved = projectRepository.save(project);
        return "redirect:/projects/" + saved.getId();
    }

    @GetMapping("/{id}")
    public String projectDetail(@PathVariable Long id, Model model) {
        ProductProject project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        model.addAttribute("project", project);
        model.addAttribute("referenceImages", referenceImageRepository.findByProject(project));
        return "project-detail";
    }

    @PostMapping("/{id}/upload")
    public String uploadReference(@PathVariable Long id,
                                  @RequestParam("files") MultipartFile[] files) {
        ProductProject project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }

            String storedPath = fileStorageService.store(file, "references/project-" + id);

            ReferenceImage ref = new ReferenceImage();
            ref.setProject(project);
            ref.setOriginalFilename(file.getOriginalFilename());
            ref.setStoredFilename(file.getOriginalFilename());
            ref.setContentType(file.getContentType());
            ref.setFilePath(storedPath);

            referenceImageRepository.save(ref);
        }

        return "redirect:/projects/" + id;
    }
}