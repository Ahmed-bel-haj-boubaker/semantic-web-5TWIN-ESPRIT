package com.example.energy.entities;
import lombok.*;
import lombok.experimental.FieldDefaults;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrganizationG {
    String legalisationApplicable;
}
