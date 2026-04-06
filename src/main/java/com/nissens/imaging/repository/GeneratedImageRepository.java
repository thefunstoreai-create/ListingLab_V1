package com.nissens.imaging.repository;

import com.nissens.imaging.entity.GeneratedImage;
import com.nissens.imaging.entity.ProductProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeneratedImageRepository extends JpaRepository<GeneratedImage, Long> {
    List<GeneratedImage> findByProject(ProductProject project);
}