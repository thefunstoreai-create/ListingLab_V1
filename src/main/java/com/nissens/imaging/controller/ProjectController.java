package com.nissens.imaging.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nissens.imaging.catalog.CategoryConfigService;
import com.nissens.imaging.entity.GenerateImagesForm;
import com.nissens.imaging.entity.GeneratedImage;
import com.nissens.imaging.entity.GenerationStatus;
import com.nissens.imaging.entity.ProductAnalysis;
import com.nissens.imaging.entity.ProductProject;
import com.nissens.imaging.entity.ReferenceImage;
import com.nissens.imaging.entity.StylePreset;
import com.nissens.imaging.repository.GeneratedImageRepository;
import com.nissens.imaging.repository.ProductAnalysisRepository;
import com.nissens.imaging.repository.ProductProjectRepository;
import com.nissens.imaging.repository.ReferenceImageRepository;
import com.nissens.imaging.service.DemoGenerationService;
import com.nissens.imaging.service.FileCleanupService;
import com.nissens.imaging.service.FileStorageService;
import com.nissens.imaging.service.ProductAnalysisService;
import com.nissens.imaging.service.PromptBuilderService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProductProjectRepository productProjectRepository;
    private final ReferenceImageRepository referenceImageRepository;
    private final GeneratedImageRepository generatedImageRepository;
    private final ProductAnalysisRepository productAnalysisRepository;

    private final CategoryConfigService categoryConfigService;
    private final ObjectMapper objectMapper;

    private final FileStorageService fileStorageService;
    private final PromptBuilderService promptBuilderService;
    private final DemoGenerationService demoGenerationService;
    private final FileCleanupService fileCleanupService;
    private final ProductAnalysisService productAnalysisService;

    public ProjectController(ProductProjectRepository productProjectRepository,
                             ReferenceImageRepository referenceImageRepository,
                             GeneratedImageRepository generatedImageRepository,
                             ProductAnalysisRepository productAnalysisRepository,
                             CategoryConfigService categoryConfigService,
                             ObjectMapper objectMapper,
                             FileStorageService fileStorageService,
                             PromptBuilderService promptBuilderService,
                             DemoGenerationService demoGenerationService,
                             FileCleanupService fileCleanupService,
                             ProductAnalysisService productAnalysisService) {
        this.productProjectRepository = productProjectRepository;
        this.referenceImageRepository = referenceImageRepository;
        this.generatedImageRepository = generatedImageRepository;
        this.productAnalysisRepository = productAnalysisRepository;
        this.categoryConfigService = categoryConfigService;
        this.objectMapper = objectMapper;
        this.fileStorageService = fileStorageService;
        this.promptBuilderService = promptBuilderService;
        this.demoGenerationService = demoGenerationService;
        this.fileCleanupService = fileCleanupService;
        this.productAnalysisService = productAnalysisService;
    }

    @GetMapping
    public String listProjects(Model model) {
        List<ProductProject> projects;
        try {
            projects = productProjectRepository.findAllByOrderByCreatedAtDesc();
        } catch (Exception e) {
            projects = productProjectRepository.findAll();
        }

        Map<Long, String> projectThumbnailMap = new HashMap<>();

        for (ProductProject project : projects) {
            List<ReferenceImage> refs = referenceImageRepository.findByProject(project);
            if (!refs.isEmpty()) {
                String webPath = refs.get(0).getWebPath();
                if (webPath != null && !webPath.isBlank()) {
                    projectThumbnailMap.put(project.getId(), webPath);
                }
            }
        }

        model.addAttribute("projects", projects);
        model.addAttribute("projectThumbnailMap", projectThumbnailMap);
        return "project-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        ProductProject project = new ProductProject();

        model.addAttribute("project", project);
        model.addAttribute("styles", StylePreset.values());
        model.addAttribute("catalogCategories", categoryConfigService.getAllCategories());

        return "project-create";
    }

    @PostMapping
    public String createProject(@ModelAttribute("project") ProductProject project,
                                RedirectAttributes redirectAttributes) {
        try {
            project.setStructuredInputsJson(blankToNull(project.getStructuredInputsJson()));
            ProductProject saved = productProjectRepository.save(project);
            redirectAttributes.addFlashAttribute("successMessage", "Project created successfully.");
            return "redirect:/projects/" + saved.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not create project.");
            return "redirect:/projects/new";
        }
    }

    @GetMapping("/{id}")
    public String projectDetail(@PathVariable Long id, Model model) {
        ProductProject project = productProjectRepository.findById(id).orElse(null);

        if (project == null) {
            return "redirect:/projects";
        }

        model.addAttribute("project", project);
        model.addAttribute("referenceImages", referenceImageRepository.findByProject(project));
        model.addAttribute("generatedImages", generatedImageRepository.findByProject(project));
        model.addAttribute("productAnalysis", productAnalysisRepository.findByProject(project).orElse(null));
        model.addAttribute("structuredInputMap", parseJsonMap(project.getStructuredInputsJson()));
        model.addAttribute("generateForm", defaultGenerateForm(project));
        model.addAttribute("styles", StylePreset.values());
        model.addAttribute("catalogCategories", categoryConfigService.getAllCategories());

        return "project-detail";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        ProductProject project = productProjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("project", project);
        model.addAttribute("styles", StylePreset.values());
        model.addAttribute("catalogCategories", categoryConfigService.getAllCategories());

        return "project-create";
    }

    @PostMapping("/{id}")
    public String updateProject(@PathVariable Long id,
                                @ModelAttribute("project") ProductProject formProject,
                                RedirectAttributes redirectAttributes) {
        ProductProject project = productProjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        try {
            project.setProjectName(formProject.getProjectName());
            project.setProductName(formProject.getProductName());
            project.setProductCategory(formProject.getProductCategory());
            project.setStylePreset(formProject.getStylePreset());
            project.setNotes(formProject.getNotes());

            project.setCategory(formProject.getCategory());
            project.setSubcategory(formProject.getSubcategory());
            project.setStructuredInputsJson(blankToNull(formProject.getStructuredInputsJson()));

            productProjectRepository.save(project);

            redirectAttributes.addFlashAttribute("successMessage", "Project updated successfully.");
            return "redirect:/projects/" + project.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not update project.");
            return "redirect:/projects/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/upload")
    public String uploadReference(@PathVariable Long id,
                                  @RequestParam("files") MultipartFile[] files,
                                  RedirectAttributes redirectAttributes) {
        ProductProject project = productProjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found: " + id));

        if (files == null || files.length == 0) {
            redirectAttributes.addFlashAttribute("warningMessage", "No files were selected.");
            return "redirect:/projects/" + id;
        }

        int uploadedCount = 0;

        try {
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) {
                    continue;
                }

                String storedPath = fileStorageService.store(file, "references/project-" + id);
                String storedFilename = Paths.get(storedPath).getFileName().toString();

                ReferenceImage ref = new ReferenceImage();
                ref.setProject(project);
                ref.setOriginalFilename(file.getOriginalFilename());
                ref.setStoredFilename(storedFilename);
                ref.setContentType(file.getContentType());
                ref.setFilePath(storedPath);

                referenceImageRepository.saveAndFlush(ref);
                uploadedCount++;
            }

            if (uploadedCount > 0) {
                redirectAttributes.addFlashAttribute("successMessage", uploadedCount + " image(s) uploaded successfully.");
            } else {
                redirectAttributes.addFlashAttribute("warningMessage", "No valid files were uploaded.");
            }

            return "redirect:/projects/" + id;

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Could not save reference image because the project is out of sync. Please reload the page and try again.");
            return "redirect:/projects/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Image upload failed.");
            return "redirect:/projects/" + id;
        }
    }

    @PostMapping("/{id}/analyze")
    public String analyzeProduct(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        ProductProject project = productProjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        try {
            List<ReferenceImage> refs = referenceImageRepository.findByProject(project);

            productAnalysisRepository.findByProject(project)
                    .ifPresent(productAnalysisRepository::delete);

            ProductAnalysis analysis = productAnalysisService.analyze(project, refs);
            productAnalysisRepository.save(analysis);

            redirectAttributes.addFlashAttribute("successMessage", "Product analysis completed.");
            return "redirect:/projects/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Product analysis failed.");
            return "redirect:/projects/" + id;
        }
    }

    @PostMapping("/{id}/generate")
    public String generateImages(@PathVariable Long id,
                                 @ModelAttribute("generateForm") GenerateImagesForm generateForm,
                                 RedirectAttributes redirectAttributes) {
        ProductProject project = productProjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<ReferenceImage> refs = referenceImageRepository.findByProject(project);
        ProductAnalysis analysis = productAnalysisRepository.findByProject(project).orElse(null);

        int imageCount = generateForm.getImageCount() == null ? 1 : generateForm.getImageCount();
        if (imageCount < 1) {
            imageCount = 1;
        }
        if (imageCount > 8) {
            imageCount = 8;
        }

        StylePreset requestedStyle = generateForm.getStylePreset() != null
                ? generateForm.getStylePreset()
                : parseStylePreset(project.getStylePreset());

        try {
            for (int i = 0; i < imageCount; i++) {
                String prompt;

                boolean hasStructuredInputs =
                        project.getCategory() != null && !project.getCategory().isBlank()
                        && project.getSubcategory() != null && !project.getSubcategory().isBlank()
                        && project.getStructuredInputsJson() != null && !project.getStructuredInputsJson().isBlank();

                if (hasStructuredInputs) {
                    try {
                        Map<String, String> structuredInputs = parseJsonMap(project.getStructuredInputsJson());
                        prompt = promptBuilderService.buildStructuredPrompt(
                                project.getCategory(),
                                project.getSubcategory(),
                                structuredInputs
                        );
                    } catch (IllegalArgumentException e) {
                        redirectAttributes.addFlashAttribute(
                                "errorMessage",
                                "The selected category/subcategory is invalid for structured generation. Please edit the project and choose a valid category/subcategory."
                        );
                        return "redirect:/projects/" + id;
                    }
                } else {
                    prompt = promptBuilderService.buildPrompt(project, refs, requestedStyle, analysis);
                }

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

            redirectAttributes.addFlashAttribute("successMessage", "Image generation completed.");
            return "redirect:/projects/" + id;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Image generation failed.");
            return "redirect:/projects/" + id;
        }
    }

    @PostMapping("/{projectId}/delete")
    public String deleteProject(@PathVariable Long projectId,
                                RedirectAttributes redirectAttributes) {
        ProductProject project = productProjectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        try {
            List<ReferenceImage> referenceImages = referenceImageRepository.findByProject(project);
            for (ReferenceImage image : referenceImages) {
                fileCleanupService.deleteIfExists(image.getFilePath());
            }
            referenceImageRepository.deleteAll(referenceImages);

            List<GeneratedImage> generatedImages = generatedImageRepository.findByProject(project);
            for (GeneratedImage image : generatedImages) {
                fileCleanupService.deleteIfExists(image.getFilePath());
            }
            generatedImageRepository.deleteAll(generatedImages);

            fileCleanupService.deleteDirectoryIfExists("uploads/references/project-" + projectId);
            fileCleanupService.deleteDirectoryIfExists("uploads/generated/project-" + projectId);

            productAnalysisRepository.findByProject(project)
                    .ifPresent(productAnalysisRepository::delete);

            productProjectRepository.delete(project);

            redirectAttributes.addFlashAttribute("successMessage", "Project deleted successfully.");
            return "redirect:/projects";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete project.");
            return "redirect:/projects/" + projectId;
        }
    }

    @PostMapping("/{projectId}/reference-images/{imageId}/delete")
    public String deleteReferenceImage(@PathVariable Long projectId,
                                       @PathVariable Long imageId,
                                       RedirectAttributes redirectAttributes) {
        ProductProject project = productProjectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ReferenceImage image = referenceImageRepository.findById(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!image.getProject().getId().equals(project.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        try {
            fileCleanupService.deleteIfExists(image.getFilePath());
            referenceImageRepository.delete(image);

            redirectAttributes.addFlashAttribute("successMessage", "Reference image deleted.");
            return "redirect:/projects/" + projectId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete reference image.");
            return "redirect:/projects/" + projectId;
        }
    }

    @PostMapping("/{projectId}/generated-images/{imageId}/delete")
    public String deleteGeneratedImage(@PathVariable Long projectId,
                                       @PathVariable Long imageId,
                                       RedirectAttributes redirectAttributes) {
        ProductProject project = productProjectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        GeneratedImage image = generatedImageRepository.findById(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!image.getProject().getId().equals(project.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        try {
            fileCleanupService.deleteIfExists(image.getFilePath());
            generatedImageRepository.delete(image);

            redirectAttributes.addFlashAttribute("successMessage", "Generated image deleted.");
            return "redirect:/projects/" + projectId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete generated image.");
            return "redirect:/projects/" + projectId;
        }
    }

    private Map<String, String> parseJsonMap(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }

        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

    private GenerateImagesForm defaultGenerateForm(ProductProject project) {
        GenerateImagesForm form = new GenerateImagesForm();
        form.setImageCount(1);
        form.setStylePreset(parseStylePreset(project.getStylePreset()));
        return form;
    }

    private StylePreset parseStylePreset(String value) {
        if (value == null || value.isBlank()) {
            return StylePreset.WHOLESALE;
        }

        String normalized = value.trim()
                .replace(" ", "_")
                .replace("-", "_")
                .toUpperCase();

        try {
            return StylePreset.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return StylePreset.WHOLESALE;
        }
    }
}