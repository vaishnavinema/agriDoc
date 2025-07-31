package com.agridoc.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "diagnosis_records")
public class DiagnosisRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;
    @Column(name = "disease_name", nullable = false)
    private String diseaseName;
    @Column(name = "confidence_score", precision = 5, scale = 2)
    private BigDecimal confidenceScore;
    @Column(name = "treatment_recommended", columnDefinition = "TEXT")
    private String treatmentRecommended;
    @Column(name = "diagnosed_at", updatable = false)
    private Instant diagnosedAt = Instant.now();
    @Column(columnDefinition = "TEXT")
    private String notes;
    // Getters and Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getDiseaseName() { return diseaseName; }
    public void setDiseaseName(String diseaseName) { this.diseaseName = diseaseName; }
    public BigDecimal getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(BigDecimal confidenceScore) { this.confidenceScore = confidenceScore; }
    public String getTreatmentRecommended() { return treatmentRecommended; }
    public void setTreatmentRecommended(String treatmentRecommended) { this.treatmentRecommended = treatmentRecommended; }
    public Instant getDiagnosedAt() { return diagnosedAt; }
    public void setDiagnosedAt(Instant diagnosedAt) { this.diagnosedAt = diagnosedAt; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}