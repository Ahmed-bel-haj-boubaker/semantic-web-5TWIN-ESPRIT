package com.example.energy.entities;
public class Source_énergie {
    private String conditionsUtilisation;
    private Integer potentielEnergitique;

    public Source_énergie() {}

    public Source_énergie(String conditionsUtilisation, Integer potentielEnergitique) {
        this.conditionsUtilisation = conditionsUtilisation;
        this.potentielEnergitique = potentielEnergitique;
    }

    // Getters and Setters
    public String getConditionsUtilisation() {
        return conditionsUtilisation;
    }

    public void setConditionsUtilisation(String conditionsUtilisation) {
        this.conditionsUtilisation = conditionsUtilisation;
    }

    public Integer getPotentielEnergitique() {
        return potentielEnergitique;
    }

    public void setPotentielEnergitique(Integer potentielEnergitique) {
        this.potentielEnergitique = potentielEnergitique;
    }
}