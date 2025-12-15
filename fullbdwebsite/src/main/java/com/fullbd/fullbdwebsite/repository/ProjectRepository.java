package com.fullbd.fullbdwebsite.repository;

import com.fullbd.fullbdwebsite.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    boolean existsByTitle(String title);

    List<Project> findByCategoryId(Long categoryId);

    @Query("SELECT p.category.name, COUNT(p) FROM Project p GROUP BY p.category.name")
    List<Object[]> countProjectsByCategory();
}
