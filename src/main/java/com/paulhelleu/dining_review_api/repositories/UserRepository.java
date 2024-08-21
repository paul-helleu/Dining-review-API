package com.paulhelleu.dining_review_api.repositories;

import com.paulhelleu.dining_review_api.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
  Optional<User> findByName(String name);
  Boolean existsByName(String name);
}
