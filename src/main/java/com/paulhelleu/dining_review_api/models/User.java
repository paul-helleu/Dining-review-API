package com.paulhelleu.dining_review_api.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "name", unique = true, nullable = false)
  @Setter String name;

  @Column(name = "city")
  @Setter private String city;

  @Column(name = "state")
  @Setter private String state;

  @Column(name = "zipcode")
  @Setter private Integer zipcode;

  @Column(name = "peanut_allergy")
  @Setter private Boolean peanutAllergy;

  @Column(name = "egg_allergy")
  @Setter private Boolean eggAllergy;

  @Column(name = "dairy_allergy")
  @Setter private Boolean dairyAllergy;
}
