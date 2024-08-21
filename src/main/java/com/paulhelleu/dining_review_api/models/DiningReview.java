package com.paulhelleu.dining_review_api.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Table(name = "reviews")
public class DiningReview {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "restaurant_id", nullable = false)
  @Setter private Long restaurantId;

  @Column(name = "username", nullable = false)
  @Setter private String username;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "admin_review_action_id", referencedColumnName = "id", nullable = false)
  @Setter private AdminReviewAction adminStatus;

  @Column(name = "peanut_score")
  @Setter private Integer peanutScore;

  @Column(name = "egg_score")
  @Setter private Integer eggScore;

  @Column(name = "dairy_score")
  @Setter private Integer dairyScore;

  @Column(name = "commentary")
  @Setter private String commentary;
}
