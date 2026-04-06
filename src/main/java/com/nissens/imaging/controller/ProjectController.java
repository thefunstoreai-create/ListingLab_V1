package com.nissens.imaging.controller;

import com.nissens.imaging.entity.GenerateImagesForm;
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
import com.nissens.imaging.service.FileCleanupService;
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
    private final FileCleanupService fileCleanupService;

    public ProjectController(ProductProjectRepository projectRepository,
                             ReferenceImageRepository referenceImageRepository,
                             GeneratedImageRepository generatedImageRepository,
                             FileStorageService fileStorageService,
                             PromptBuilderService promptBuilderService,
                             DemoGenerationService demoGenerationService,
                             FileCleanupService fileCleanupService) {
        this.projectRepository = projectRepository;
        this.referenceImageRepository = referenceImageRepository;
        this.generatedImageRepository = generatedImageRepository;
        this.fileStorageService = fileStorageService;
        this.promptBuilderService = promptBuilderService;
        this.demoGenerationService = demoGenerationService;
        this.fileCleanupService = fileCleanupService;
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
        model.addAttribute("generateForm", defaultGenerateForm(project));
        model.addAttribute("styles", StylePreset.values());
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
    public String generateImages(@PathVariable Long id,
                                 @ModelAttribute("generateForm") GenerateImagesForm generateForm) {
        ProductProject project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        List<ReferenceImage> refs = referenceImageRepository.findByProject(project);

        int imageCount = generateForm.getImageCount() == null ? 1 : generateForm.getImageCount();
        if (imageCount < 1) {
            imageCount = 1;
        }
        if (imageCount > 8) {
            imageCount = 8;
        }

        StylePreset requestedStyle = generateForm.getStylePreset() != null
                ? generateForm.getStylePreset()
                : project.getStylePreset();

        for (int i = 0; i < imageCount; i++) {
            String prompt = promptBuilderService.buildPrompt(project, refs, requestedStyle);

            String demoOutputPath = null;
            if (!refs.isEmpty()) {
                int refIndex = i % refs.size();
                demoOutputPath = demoGenerationService.createDemoGeneratedImage(
                        refs.get(refIndex).getFilePath(),
                        id
                );
            }

            GeneratedImage generatedImage = new GeneratedImage();
            generatedImage.setProject(project);
            generatedImage.setPromptUsed(prompt);
            generatedImage.setStylePreset(requestedStyle);
            generatedImage.setStatus(demoOutputPath != null ? GenerationStatus.COMPLETED : GenerationStatus.FAILED);
            generatedImage.setProviderRequestId("demo-request-" + System.currentTimeMillis() + "-" + i);
            generatedImage.setFilePath(demoOutputPath);

            generatedImageRepository.save(generatedImage);
        }

        return "redirect:/projects/" + id;
    }

    @PostMapping("/{projectId}/reference-images/{imageId}/delete")
    public String deleteReferenceImage(@PathVariable Long projectId,
                                       @PathVariable Long imageId) {
        ProductProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        ReferenceImage image = referenceImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Reference image not found"));

        if (!image.getProject().getId().equals(project.getId())) {
            throw new IllegalArgumentException("Reference image does not belong to project");
        }

        fileCleanupService.deleteIfExists(image.getFilePath());
        referenceImageRepository.delete(image);

        return "redirect:/projects/" + projectId;
    }

    @PostMapping("/{projectId}/generated-images/{imageId}/delete")
    public String deleteGeneratedImage(@PathVariable Long projectId,
                                       @PathVariable Long imageId) {
        ProductProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        GeneratedImage image = generatedImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Generated image not found"));

        if (!image.getProject().getId().equals(project.getId())) {
            throw new IllegalArgumentException("Generated image does not belong to project");
        }

        fileCleanupService.deleteIfExists(image.getFilePath());
        generatedImageRepository.delete(image);

        return "redirect:/projects/" + projectId;
    }

    private GenerateImagesForm defaultGenerateForm(ProductProject project) {
        GenerateImagesForm form = new GenerateImagesForm();
        form.setImageCount(1);
        form.setStylePreset(project.getStylePreset());
        return form;
    }
}