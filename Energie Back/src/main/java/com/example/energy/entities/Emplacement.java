package com.example.energy.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Emplacement {
    String adresse;
    String conditions_environnementales;
    String coordonnees;
}
