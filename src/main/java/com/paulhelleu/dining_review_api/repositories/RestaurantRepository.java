package com.paulhelleu.dining_review_api.repositories;

import com.paulhelleu.dining_review_api.models.Restaurant;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends CrudRepository<Restaurant, Long> {
  Boolean existsByNameAndZipCode(String name, Integer zipCode);

  Optional<List<Restaurant>> findByZipCodeAndPeanutAllergyIsNotNullOrderByNameDesc(int zipCode);
  Optional<List<Restaurant>> findByZipCodeAndEggAllergyIsNotNullOrderByNameDesc(int zipCode);
  Optional<List<Restaurant>> findByZipCodeAndDairyAllergyIsNotNullOrderByNameDesc(int zipCode);
}
