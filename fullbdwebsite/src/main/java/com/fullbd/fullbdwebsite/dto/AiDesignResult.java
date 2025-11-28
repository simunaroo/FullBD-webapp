package com.fullbd.fullbdwebsite.dto;

public class AiDesignResult {
    // Chi phí
    private long roughCost;     // Phần thô
    private long finishCost;    // Hoàn thiện
    private long furnitureCost; // Nội thất
    private long totalCost;     // Tổng cộng

    // Hình ảnh (Đường dẫn)
    private String layout2D;
    private String perspective3D;
    
    private String advice;      // Lời khuyên của AI

    // Getters & Setters
    public long getRoughCost() { return roughCost; }
    public void setRoughCost(long roughCost) { this.roughCost = roughCost; }
    public long getFinishCost() { return finishCost; }
    public void setFinishCost(long finishCost) { this.finishCost = finishCost; }
    public long getFurnitureCost() { return furnitureCost; }
    public void setFurnitureCost(long furnitureCost) { this.furnitureCost = furnitureCost; }
    public long getTotalCost() { return totalCost; }
    public void setTotalCost(long totalCost) { this.totalCost = totalCost; }
    public String getLayout2D() { return layout2D; }
    public void setLayout2D(String layout2D) { this.layout2D = layout2D; }
    public String getPerspective3D() { return perspective3D; }
    public void setPerspective3D(String perspective3D) { this.perspective3D = perspective3D; }
    public String getAdvice() { return advice; }
    public void setAdvice(String advice) { this.advice = advice; }
}