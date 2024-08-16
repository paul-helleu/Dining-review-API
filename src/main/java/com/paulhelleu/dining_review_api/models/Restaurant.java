package com.paulhelleu.dining_review_api.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Restaurants")
public class Restaurant {
  @Id
  @GeneratedValue
  private Long id;

  @Getter @Setter private Double peanutAllergy;
  @Getter @Setter private Double eggAllergy;
  @Getter @Setter private Double dairyAllergy;

  @Getter @Setter private Double overallScore;
}
