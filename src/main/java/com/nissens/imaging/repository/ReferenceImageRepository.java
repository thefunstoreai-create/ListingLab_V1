package com.nissens.imaging.repository;

import com.nissens.imaging.entity.ProductProject;
import com.nissens.imaging.entity.ReferenceImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferenceImageRepository extends JpaRepository<ReferenceImage, Long> {
    List<ReferenceImage> findByProject(ProductProject project);
}