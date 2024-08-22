package com.paulhelleu.dining_review_api.controllers;

import com.paulhelleu.dining_review_api.models.AdminReviewAction;
import com.paulhelleu.dining_review_api.models.DiningReview;
import com.paulhelleu.dining_review_api.models.Restaurant;
import com.paulhelleu.dining_review_api.models.User;
import com.paulhelleu.dining_review_api.repositories.AdminReviewActionRepository;
import com.paulhelleu.dining_review_api.repositories.DiningReviewRepository;
import com.paulhelleu.dining_review_api.repositories.RestaurantRepository;
import com.paulhelleu.dining_review_api.repositories.UserRepository;
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

  // DiningReview
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

    return this.diningReviewRepository.save(diningReview);
  }

  @PutMapping("/review/{id}")
  public DiningReview updateDiningReview(@PathVariable Long id, @RequestBody DiningReview diningReviewUpdated) {
    Optional<DiningReview> diningReviewOptional = this.diningReviewRepository.findById(id);
    if (diningReviewOptional.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    DiningReview diningReview = diningReviewOptional.get();

    if (diningReviewUpdated.getCommentary() != null) {
      diningReview.setCommentary(diningReviewUpdated.getCommentary());
    }
    if (diningReviewUpdated.getDairyScore() != null) {
      diningReview.setDairyScore(diningReviewUpdated.getDairyScore());
    }
    if (diningReviewUpdated.getEggScore() != null) {
      diningReview.setEggScore(diningReviewUpdated.getEggScore());
    }
    if (diningReviewUpdated.getPeanutScore() != null) {
      diningReview.setPeanutScore(diningReviewUpdated.getPeanutScore());
    }

    return this.diningReviewRepository.save(diningReview);
  }

  @GetMapping("/admin/diningReview")
  public Iterable<DiningReview> getDiningReviews() {
    return this.diningReviewRepository.findAll();
  }

  @DeleteMapping("/review/{id}")
  public DiningReview deleteDiningReview(@PathVariable Long id) {
    Optional<DiningReview> diningReviewOptional = this.diningReviewRepository.findById(id);
    if (diningReviewOptional.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    DiningReview diningReview = diningReviewOptional.get();
    this.diningReviewRepository.delete(diningReview);
    return diningReview;
  }

  // AdminReviewAction
  @PostMapping("/admin/approve/{id}")
  @ResponseStatus(HttpStatus.CREATED)
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

  // User
  @GetMapping("/users/{name}")
  public User getUser(@PathVariable String name) {
    Optional<User> userOptional = this.userRepository.findByName(name);

    if (userOptional.isPresent()) {
      return userOptional.get();
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
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

    return this.userRepository.save(user);
  }

  @PutMapping("/users/{name}")
  public User updateUser(@PathVariable String name, @RequestBody User userUpdated) {
    Optional<User> userOptional = this.userRepository.findByName(name);
    if (userOptional.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    if (userUpdated == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User cannot be null");
    }

    User user = userOptional.get();
    if (userUpdated.getCity() != null) {
      user.setCity(userUpdated.getCity());
    }
    if (userUpdated.getState() != null) {
      user.setState(userUpdated.getState());
    }
    if (userUpdated.getZipcode() != null) {
      user.setZipcode(userUpdated.getZipcode());
    }
    if (userUpdated.getDairyAllergy() != null) {
      user.setDairyAllergy(userUpdated.getDairyAllergy());
    }
    if (userUpdated.getEggAllergy() != null) {
      user.setPeanutAllergy(userUpdated.getPeanutAllergy());
    }
    if (userUpdated.getPeanutAllergy() != null) {
      user.setPeanutAllergy(userUpdated.getPeanutAllergy());
    }

    return user;
  }

  @DeleteMapping("/users/{name}")
  public User deleteUser(@PathVariable String name) {
    Optional<User> userOptional = this.userRepository.findByName(name);
    if (userOptional.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    User user = userOptional.get();
    this.userRepository.delete(user);
    return user;
  }

  // Restaurant
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

  @PostMapping("/restaurants")
  @ResponseStatus(HttpStatus.CREATED)
  public Restaurant createRestaurant(@RequestBody Restaurant restaurant) {
    if (this.restaurantRepository.existsByNameAndZipCode(restaurant.getName(), restaurant.getZipCode())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Restaurant with the same name and zip code already exists");
    }

    return this.restaurantRepository.save(restaurant);
  }

  @PutMapping("/restaurants/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Restaurant updateRestaurant(@PathVariable Long id, @RequestBody Restaurant updatedRestaurant) {
    Optional<Restaurant> restaurantOptional = this.restaurantRepository.findById(id);
    if (restaurantOptional.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    Restaurant restaurant = restaurantOptional.get();
    if (updatedRestaurant.getName() != null) {
      restaurant.setName(updatedRestaurant.getName());
    }
    if (updatedRestaurant.getZipCode() != null) {
      restaurant.setZipCode(updatedRestaurant.getZipCode());
    }
    if (updatedRestaurant.getDairyAllergy() != null) {
      restaurant.setDairyAllergy(updatedRestaurant.getDairyAllergy());
    }
    if (updatedRestaurant.getEggAllergy() != null) {
      restaurant.setEggAllergy(updatedRestaurant.getEggAllergy());
    }
    if (updatedRestaurant.getPeanutAllergy() != null) {
      restaurant.setPeanutAllergy(updatedRestaurant.getPeanutAllergy());
    }
    if (updatedRestaurant.getOverallScore() != null) {
      restaurant.setOverallScore(updatedRestaurant.getOverallScore());
    }

    return restaurantRepository.save(restaurant);
  }

  @DeleteMapping("/restaurants/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Restaurant deleteRestaurant(@PathVariable Long id) {
    Optional<Restaurant> restaurantOptional = this.restaurantRepository.findById(id);
    if (restaurantOptional.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    Restaurant restaurant = restaurantOptional.get();
    this.restaurantRepository.delete(restaurant);

    return restaurant;
  }

}
