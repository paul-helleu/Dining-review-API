package com.paulhelleu.dining_review_api.controllers;

import com.paulhelleu.dining_review_api.models.AdminReviewAction;
import com.paulhelleu.dining_review_api.models.DiningReview;
import com.paulhelleu.dining_review_api.models.Restaurant;
import com.paulhelleu.dining_review_api.models.User;
import com.paulhelleu.dining_review_api.repositories.AdminReviewActionRepository;
import com.paulhelleu.dining_review_api.repositories.DiningReviewRepository;
import com.paulhelleu.dining_review_api.repositories.RestaurantRepository;
import com.paulhelleu.dining_review_api.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/diningReview")
public class DiningReviewController {
  private final DiningReviewRepository diningReviewRepository;
  private final RestaurantRepository restaurantRepository;
  private final UserRepository userRepository;
  private final AdminReviewActionRepository adminReviewActionRepository;

  public DiningReviewController(final DiningReviewRepository diningReviewRepository,
                                final RestaurantRepository restaurantRepository,
                                final UserRepository userRepository,
                                final AdminReviewActionRepository adminReviewActionRepository) {
    this.diningReviewRepository = diningReviewRepository;
    this.restaurantRepository = restaurantRepository;
    this.userRepository = userRepository;
    this.adminReviewActionRepository = adminReviewActionRepository;
  }

  @GetMapping
  public Iterable<DiningReview> getAdminStatusApproveDiningReview() {
    return this.diningReviewRepository.findByAdminStatusApproveDiningReviewTrue();
  }

  @GetMapping("/review/{id}")
  public DiningReview getDiningReview(@PathVariable Long id) {
    Optional<DiningReview> diningReviewOptional = this.diningReviewRepository.findById(id);

    if (diningReviewOptional.isPresent()) {
      return diningReviewOptional.get();
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
  }

  @GetMapping("/users/{name}")
  public User getUser(@PathVariable String name) {
    Optional<User> userOptional = this.userRepository.findByName(name);

    if (userOptional.isPresent()) {
      return userOptional.get();
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
  }

  @GetMapping("/restaurants/{id}")
  public Restaurant getRestaurant(@PathVariable Long id) {
    Optional<Restaurant> restaurantOptional = this.restaurantRepository.findById(id);

    if (restaurantOptional.isPresent()) {
      return restaurantOptional.get();
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
  }

  @GetMapping("/restaurants")
  public Iterable<Restaurant> getRestaurants() {
    return this.restaurantRepository.findAll();
  }

  @GetMapping("/restaurants/search")
  public List<Restaurant> getRestaurants(@RequestParam Integer zipCode, @RequestParam String allergy) {
    if (zipCode == null || allergy == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    Optional<List<Restaurant>> restaurantsOptional = switch (allergy) {
      case "peanutAllergy" -> this.restaurantRepository.findByZipCodeAndPeanutAllergyIsNotNullOrderByNameDesc(zipCode);
      case "eggAllergy" -> this.restaurantRepository.findByZipCodeAndEggAllergyIsNotNullOrderByNameDesc(zipCode);
      case "dairyAllergy" -> this.restaurantRepository.findByZipCodeAndDairyAllergyIsNotNullOrderByNameDesc(zipCode);
      default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    };

    if (restaurantsOptional.isPresent()) {
      return restaurantsOptional.get();
    }

    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
  }

  @GetMapping("/admin/diningReview")
  public Iterable<DiningReview> getDiningReviews() {
    return this.diningReviewRepository.findAll();
  }

  @Transactional
  @PostMapping("/admin/approve/{id}")
  public AdminReviewAction approveReview(@PathVariable Long id, @RequestBody Boolean status) {
    if (status == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status cannot be empty");
    }

    Optional<DiningReview> diningReviewOptional = this.diningReviewRepository.findById(id);
    if (diningReviewOptional.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Dining review not found");
    }

    DiningReview diningReview = diningReviewOptional.get();
    AdminReviewAction adminReviewAction = diningReview.getAdminStatus();

    adminReviewAction.setApproveDiningReview(status);

    return this.adminReviewActionRepository.save(adminReviewAction);
  }

  @PostMapping("/restaurants")
  @ResponseStatus(HttpStatus.CREATED)
  public Restaurant createRestaurant(@RequestBody Restaurant restaurant) {
    if (this.restaurantRepository.existsByNameAndZipCode(restaurant.getName(), restaurant.getZipCode())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Restaurant with the same name and zip code already exists");
    }

    try {
      return this.restaurantRepository.save(restaurant);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public DiningReview createDiningReview(@RequestBody DiningReview diningReview) {
    if (diningReview == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dining review cannot be null");
    }

    String username = diningReview.getUsername();
    if (username.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be empty");
    }

    Long restaurantId = diningReview.getRestaurantId();
    if (restaurantId == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Restaurant id cannot be empty");
    }

    boolean restaurantExist = this.restaurantRepository.existsById(restaurantId);
    if (!restaurantExist) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found");
    }

    boolean userExist = this.userRepository.existsByName(username);
    if (!userExist) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, username + " not found");
    }

    AdminReviewAction adminReviewAction = new AdminReviewAction();
    adminReviewAction.setApproveDiningReview(false);
    adminReviewAction.setDiningReview(diningReview);

    diningReview.setAdminStatus(adminReviewAction);

    try {
      return this.diningReviewRepository.save(diningReview);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  public User createUser(@RequestBody User user) {
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User cannot be null");
    }
    if (user.getName() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be empty");
    }

    Boolean userExist = this.userRepository.existsByName(user.getName());
    if (userExist) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
    }

    try {
      return this.userRepository.save(user);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

}
