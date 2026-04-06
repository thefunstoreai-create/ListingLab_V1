package com.nissens.imaging.service;

import com.nissens.imaging.entity.ProductAnalysis;
import com.nissens.imaging.entity.ProductProject;
import com.nissens.imaging.entity.ReferenceImage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class ProductAnalysisService {

    public ProductAnalysis analyze(ProductProject project, List<ReferenceImage> referenceImages) {
        ProductAnalysis analysis = new ProductAnalysis();
        analysis.setProject(project);

        String productName = safe(project.getProductName()).toLowerCase(Locale.ROOT);
        String notes = safe(project.getNotes()).toLowerCase(Locale.ROOT);
        String combined = productName + " " + notes + " " + joinedFilenames(referenceImages).toLowerCase(Locale.ROOT);

        analysis.setDetectedProductType(detectProductType(combined));
        analysis.setDetectedPrimaryColor(detectColor(combined));
        analysis.setDetectedMaterial(detectMaterial(combined));
        analysis.setDetectedStyle(detectStyle(combined));
        analysis.setDetectedBrandText("Not detected");
        analysis.setPackagingType(detectPackaging(combined));
        analysis.setAnalysisNotes(buildNotes(referenceImages));

        return analysis;
    }

    private String detectProductType(String text) {
        if (text.contains("ring")) return "Ring";
        if (text.contains("necklace")) return "Necklace";
        if (text.contains("bracelet")) return "Bracelet";
        if (text.contains("earring")) return "Earrings";
        if (text.contains("stud")) return "Stud Earrings";
        if (text.contains("hoop")) return "Hoop Earrings";
        if (text.contains("nose")) return "Nose Jewellery";
        if (text.contains("pendant")) return "Pendant";
        if (text.contains("jewellery") || text.contains("jewelry")) return "Jewellery";
        return "General Product";
    }

    private String detectColor(String text) {
        if (text.contains("rose gold")) return "Rose Gold";
        if (text.contains("gold")) return "Gold";
        if (text.contains("silver")) return "Silver";
        if (text.contains("black")) return "Black";
        if (text.contains("white")) return "White";
        if (text.contains("blue")) return "Blue";
        if (text.contains("red")) return "Red";
        if (text.contains("green")) return "Green";
        return "Not clearly detected";
    }

    private String detectMaterial(String text) {
        if (text.contains("sterling")) return "Sterling Silver";
        if (text.contains("silver")) return "Silver-tone metal";
        if (text.contains("gold")) return "Gold-tone metal";
        if (text.contains("steel")) return "Steel";
        if (text.contains("leather")) return "Leather";
        if (text.contains("wood")) return "Wood";
        if (text.contains("plastic")) return "Plastic";
        return "Not clearly detected";
    }

    private String detectStyle(String text) {
        if (text.contains("minimal")) return "Minimalist";
        if (text.contains("luxury")) return "Luxury";
        if (text.contains("premium")) return "Premium";
        if (text.contains("wholesale")) return "Wholesale";
        if (text.contains("modern")) return "Modern";
        return "Standard ecommerce";
    }

    private String detectPackaging(String text) {
        if (text.contains("box")) return "Box";
        if (text.contains("card")) return "Card backing";
        if (text.contains("bag")) return "Bag";
        if (text.contains("pouch")) return "Pouch";
        return "Not clearly detected";
    }

    private String buildNotes(List<ReferenceImage> referenceImages) {
        int count = referenceImages == null ? 0 : referenceImages.size();
        return "MVP local analysis completed using project text and uploaded filenames. Reference image count: " + count + ".";
    }

    private String joinedFilenames(List<ReferenceImage> referenceImages) {
        if (referenceImages == null || referenceImages.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (ReferenceImage img : referenceImages) {
            if (img.getOriginalFilename() != null) {
                sb.append(img.getOriginalFilename()).append(" ");
            }
        }
        return sb.toString();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}