package com.paulhelleu.dining_review_api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Table(name = "admin_review_actions")
public class AdminReviewAction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "approve_dining_review")
  @Setter private Boolean approveDiningReview;

  @OneToOne(mappedBy = "adminStatus", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  @Setter private DiningReview diningReview;
}
