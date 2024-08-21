package com.paulhelleu.dining_review_api.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Table(name = "restaurants")
public class Restaurant {
  @Id
  @GeneratedValue
  private Long id;

  @Column(name = "name", nullable = false)
  @Setter private String name;

  @Column(name = "zip_code", unique = true, nullable = false)
  @Setter private Integer zipCode;

  @Column(name = "peanut_allergy")
  @Setter private Double peanutAllergy;

  @Column(name = "egg_allergy")
  @Setter private Double eggAllergy;

  @Column(name = "dairy_allergy")
  @Setter private Double dairyAllergy;

  @Column(name = "overall_score")
  @Setter private Double overallScore;
}
