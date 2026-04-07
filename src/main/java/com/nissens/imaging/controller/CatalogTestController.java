package com.nissens.imaging.controller;

import com.nissens.imaging.catalog.CategoryConfigService;
import com.nissens.imaging.service.PromptBuilderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class CatalogTestController {

    private final CategoryConfigService categoryConfigService;
    private final PromptBuilderService promptBuilderService;

    public CatalogTestController(CategoryConfigService categoryConfigService,
                                 PromptBuilderService promptBuilderService) {
        this.categoryConfigService = categoryConfigService;
        this.promptBuilderService = promptBuilderService;
    }

    @GetMapping("/catalog-test")
    public String catalogTest(Model model) {
        String jewelleryPrompt = promptBuilderService.buildStructuredPrompt(
                "Jewellery",
                "Nose Rings",
                Map.of(
                        "material", "Stainless Steel",
                        "colour", "Silver",
                        "style", "Minimalist",
                        "target", "Retail"
                )
        );

        String disposablesPrompt = promptBuilderService.buildStructuredPrompt(
                "Disposables",
                "Paper Cups",
                Map.of(
                        "size", "350ml",
                        "colourOrPrint", "White with gold print",
                        "material", "Ripple",
                        "packSize", "100"
                )
        );

        model.addAttribute("categories", categoryConfigService.getAllCategories());
        model.addAttribute("jewelleryPrompt", jewelleryPrompt);
        model.addAttribute("disposablesPrompt", disposablesPrompt);

        return "catalog-test";
    }
}