package com.nissens.imaging.repository;

import com.nissens.imaging.entity.ProductAnalysis;
import com.nissens.imaging.entity.ProductProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductAnalysisRepository extends JpaRepository<ProductAnalysis, Long> {
    Optional<ProductAnalysis> findByProject(ProductProject project);
}