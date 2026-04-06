package com.nissens.imaging.controller;

import com.nissens.imaging.entity.GeneratedImage;
import com.nissens.imaging.entity.GenerationStatus;
import com.nissens.imaging.entity.ProductCategory;
import com.nissens.imaging.entity.ProductProject;
import com.nissens.imaging.entity.ReferenceImage;
import com.nissens.imaging.entity.StylePreset;
import com.nissens.imaging.repository.GeneratedImageRepository;
import com.nissens.imaging.repository.ProductProjectRepository;
import com.nissens.imaging.repository.ReferenceImageRepository;
import com.nissens.imaging.service.DemoGenerationService;
import com.nissens.imaging.service.FileStorageService;
import com.nissens.imaging.service.PromptBuilderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProductProjectRepository projectRepository;
    private final ReferenceImageRepository referenceImageRepository;
    private final GeneratedImageRepository generatedImageRepository;
    private final FileStorageService fileStorageService;
    private final PromptBuilderService promptBuilderService;
    private final DemoGenerationService demoGenerationService;

    public ProjectController(ProductProjectRepository projectRepository,
                             ReferenceImageRepository referenceImageRepository,
                             GeneratedImageRepository generatedImageRepository,
                             FileStorageService fileStorageService,
                             PromptBuilderService promptBuilderService,
                             DemoGenerationService demoGenerationService) {
        this.projectRepository = projectRepository;
        this.referenceImageRepository = referenceImageRepository;
        this.generatedImageRepository = generatedImageRepository;
        this.fileStorageService = fileStorageService;
        this.promptBuilderService = promptBuilderService;
        this.demoGenerationService = demoGenerationService;
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
        model.addAttribute("generatedImages", generatedImageRepository.findByProject(project));
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

    @PostMapping("/{id}/generate")
    public String generateImages(@PathVariable Long id) {
        ProductProject project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        List<ReferenceImage> refs = referenceImageRepository.findByProject(project);
        String prompt = promptBuilderService.buildPrompt(project, refs);

        String demoOutputPath = null;
        if (!refs.isEmpty()) {
            demoOutputPath = demoGenerationService.createDemoGeneratedImage(refs.get(0).getFilePath(), id);
        }

        GeneratedImage generatedImage = new GeneratedImage();
        generatedImage.setProject(project);
        generatedImage.setPromptUsed(prompt);
        generatedImage.setStylePreset(project.getStylePreset());
        generatedImage.setStatus(demoOutputPath != null ? GenerationStatus.COMPLETED : GenerationStatus.FAILED);
        generatedImage.setProviderRequestId("demo-request-" + System.currentTimeMillis());
        generatedImage.setFilePath(demoOutputPath);

        generatedImageRepository.save(generatedImage);

        return "redirect:/projects/" + id;
    }
}