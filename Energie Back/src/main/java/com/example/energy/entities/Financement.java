package com.example.energy.entities;

public class Financement {
    private double montant_total;
    private String sourceFinancement;
    private TechnologieEnergetique technologie; // Relation avec TechnologieEnergetique

    public Financement() {
        // Constructeur par défaut
    }

    // Getters et Setters
    public double getMontantTotal() {
        return montant_total;
    }

    public void setMontantTotal(double montant_total) {
        this.montant_total = montant_total;
    }

    public String getSourceFinancement() {
        return sourceFinancement;
    }

    public void setSourceFinancement(String sourceFinancement) {
        this.sourceFinancement = sourceFinancement;
    }

    public TechnologieEnergetique getTechnologie() {
        return technologie;
    }

    public void setTechnologie(TechnologieEnergetique technologie) {
        this.technologie = technologie;
    }
}
