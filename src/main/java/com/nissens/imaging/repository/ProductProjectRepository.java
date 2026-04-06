package com.nissens.imaging.repository;

import com.nissens.imaging.entity.ProductProject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductProjectRepository extends JpaRepository<ProductProject, Long> {
}