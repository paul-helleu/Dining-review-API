package com.paulhelleu.dining_review_api.repositories;

import com.paulhelleu.dining_review_api.models.DiningReview;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DiningReviewRepository extends CrudRepository<DiningReview, Long> {
  List<DiningReview> findByAdminStatusApproveDiningReviewTrue();
}
