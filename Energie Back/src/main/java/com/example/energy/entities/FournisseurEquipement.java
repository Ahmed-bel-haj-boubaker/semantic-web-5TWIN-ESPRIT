package com.example.energy.entities;

public class FournisseurEquipement {
    private String nom;
    private  String contact;
    private String statut;
    private String disponibilite;
    private String type_equipement;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(String disponibilite) {
        this.disponibilite = disponibilite;
    }

    public String getType_equipement() {
        return type_equipement;
    }

    public void setType_equipement(String type_equipement) {
        this.type_equipement = type_equipement;
    }

    public FournisseurEquipement() {
    }

    public FournisseurEquipement(String nom, String contact, String statut, String disponibilite, String type_equipement) {
        this.nom = nom;
        this.contact = contact;
        this.statut = statut;
        this.disponibilite = disponibilite;
        this.type_equipement = type_equipement;
    }
}
