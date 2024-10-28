package com.example.energy.entities;

public class TechnologieEnergetique {

    private String nomTechnologie;
    private double efficacite;

    public TechnologieEnergetique() {
        // Constructeur par défaut
    }

    // Getters et Setters
    public String getNomTechnologie() {
        return nomTechnologie;
    }

    public void setNomTechnologie(String nomTechnologie) {
        this.nomTechnologie = nomTechnologie;
    }

    public double getEfficacite() {
        return efficacite;
    }

    public void setEfficacite(double efficacite) {
        this.efficacite = efficacite;
    }
}