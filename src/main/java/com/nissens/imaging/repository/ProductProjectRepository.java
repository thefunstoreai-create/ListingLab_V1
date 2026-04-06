package com.nissens.imaging.repository;

import com.nissens.imaging.entity.ProductProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductProjectRepository extends JpaRepository<ProductProject, Long> {
    List<ProductProject> findAllByOrderByCreatedAtDesc();
}