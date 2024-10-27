package com.example.energy.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Organization {
      String nom;
      OrganizationG organizationG;

      public Organization(String nom) {
            this.nom = nom;
      }

}
