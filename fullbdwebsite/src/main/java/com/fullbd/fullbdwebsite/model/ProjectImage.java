package com.fullbd.fullbdwebsite.model;

import jakarta.persistence.*;

@Entity
@Table(name = "project_images")
public class ProjectImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    // Constructor, Getter, Setter
    public ProjectImage() {
    }

    public ProjectImage(String imageUrl, Project project) {
        this.imageUrl = imageUrl;
        this.project = project;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}