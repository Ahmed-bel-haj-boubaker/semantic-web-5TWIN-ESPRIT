package com.example.energy.entities;

public class Equipement {
    private String type;
    private Integer capacite;

    public Equipement() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCapacite() {
        return capacite;
    }

    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }

    public Equipement(String type, Integer capacite) {
        this.type = type;
        this.capacite = capacite;
    }
}
