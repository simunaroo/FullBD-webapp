package com.fullbd.fullbdwebsite.dto;

public class AiDesignRequest {
    private Double area;        // Diện tích đất (m2)
    private Integer floors;     // Số tầng
    private String style;       // Phong cách (modern, classic, neo-classic)
    private String material;    // Vật liệu (standard, luxury)
    private Double budget;      // Ngân sách dự kiến

    // Getters & Setters
    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }
    public Integer getFloors() { return floors; }
    public void setFloors(Integer floors) { this.floors = floors; }
    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }
    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }
}