package com.fooddelivery.config;

import com.fooddelivery.model.Address;
import com.fooddelivery.model.FoodItem;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.model.User;
import com.fooddelivery.repository.AddressRepository;
import com.fooddelivery.repository.FoodItemRepository;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodItemRepository foodItemRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            System.out.println("Seeding database with demo accounts and data...");

            // Seed Customer
            User customer = User.builder()
                    .name("John Doe")
                    .email("john@example.com")
                    .password(passwordEncoder.encode("password"))
                    .phone("1234567890")
                    .role(User.Role.CUSTOMER)
                    .active(true)
                    .build();
            userRepository.save(customer);

            // Seed Restaurant Owners
            User owner1 = User.builder()
                    .name("Raj Patel")
                    .email("raj@restaurant.com")
                    .password(passwordEncoder.encode("password"))
                    .phone("0987654321")
                    .role(User.Role.RESTAURANT_OWNER)
                    .active(true)
                    .build();
            owner1 = userRepository.save(owner1);

            User owner2 = User.builder()
                    .name("Maria Garcia")
                    .email("maria@restaurant.com")
                    .password(passwordEncoder.encode("password"))
                    .phone("1122334455")
                    .role(User.Role.RESTAURANT_OWNER)
                    .active(true)
                    .build();
            owner2 = userRepository.save(owner2);

            User owner3 = User.builder()
                    .name("Li Wei")
                    .email("li@restaurant.com")
                    .password(passwordEncoder.encode("password"))
                    .phone("1122334466")
                    .role(User.Role.RESTAURANT_OWNER)
                    .active(true)
                    .build();
            owner3 = userRepository.save(owner3);

            User owner4 = User.builder()
                    .name("Sophie Martin")
                    .email("sophie@restaurant.com")
                    .password(passwordEncoder.encode("password"))
                    .phone("1122334477")
                    .role(User.Role.RESTAURANT_OWNER)
                    .active(true)
                    .build();
            owner4 = userRepository.save(owner4);

            User owner5 = User.builder()
                    .name("Anil Kapoor")
                    .email("anil@restaurant.com")
                    .password(passwordEncoder.encode("password"))
                    .phone("1122334488")
                    .role(User.Role.RESTAURANT_OWNER)
                    .active(true)
                    .build();
            owner5 = userRepository.save(owner5);

            User owner6 = User.builder()
                    .name("Elena Rossi")
                    .email("elena@restaurant.com")
                    .password(passwordEncoder.encode("password"))
                    .phone("1122334499")
                    .role(User.Role.RESTAURANT_OWNER)
                    .active(true)
                    .build();
            owner6 = userRepository.save(owner6);

            User owner7 = User.builder()
                    .name("Kenji Tanaka")
                    .email("kenji@restaurant.com")
                    .password(passwordEncoder.encode("password"))
                    .phone("1122334400")
                    .role(User.Role.RESTAURANT_OWNER)
                    .active(true)
                    .build();
            owner7 = userRepository.save(owner7);

            // Seed Delivery Partners
            User deliveryPartner1 = User.builder()
                    .name("Alex Smith")
                    .email("alex@delivery.com")
                    .password(passwordEncoder.encode("password"))
                    .phone("5556667777")
                    .role(User.Role.DELIVERY_PARTNER)
                    .active(true)
                    .build();
            userRepository.save(deliveryPartner1);

            User deliveryPartner2 = User.builder()
                    .name("Chen Wei")
                    .email("chen@delivery.com")
                    .password(passwordEncoder.encode("password"))
                    .phone("8889990000")
                    .role(User.Role.DELIVERY_PARTNER)
                    .active(true)
                    .build();
            userRepository.save(deliveryPartner2);
            
            // Seed Admin
            User admin = User.builder()
                    .name("System Admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin"))
                    .phone("1111111111")
                    .role(User.Role.ADMIN)
                    .active(true)
                    .build();
            userRepository.save(admin);

            // Seed Addresses for Restaurants
            Address address1 = Address.builder()
                    .userId(owner1.getId())
                    .street("123 Spice Lane")
                    .city("Mumbai")
                    .state("Maharashtra")
                    .zipCode("400001")
                    .country("India")
                    .isDefault(true)
                    .build();
            address1 = addressRepository.save(address1);

            Address address2 = Address.builder()
                    .userId(owner2.getId())
                    .street("456 Olive Way")
                    .city("Delhi")
                    .state("Delhi")
                    .zipCode("110001")
                    .country("India")
                    .isDefault(true)
                    .build();
            address2 = addressRepository.save(address2);

            Address address3 = Address.builder()
                    .userId(owner3.getId())
                    .street("789 Dragon Blvd")
                    .city("Mumbai")
                    .state("Maharashtra")
                    .zipCode("400002")
                    .country("India")
                    .isDefault(true)
                    .build();
            address3 = addressRepository.save(address3);

            Address address4 = Address.builder()
                    .userId(owner4.getId())
                    .street("101 French St")
                    .city("Mumbai")
                    .state("Maharashtra")
                    .zipCode("400003")
                    .country("India")
                    .isDefault(true)
                    .build();
            address4 = addressRepository.save(address4);

            Address address5 = Address.builder()
                    .userId(owner5.getId())
                    .street("202 Curry Ave")
                    .city("Delhi")
                    .state("Delhi")
                    .zipCode("110002")
                    .country("India")
                    .isDefault(true)
                    .build();
            address5 = addressRepository.save(address5);

            Address address6 = Address.builder()
                    .userId(owner6.getId())
                    .street("303 Pizza Rd")
                    .city("Delhi")
                    .state("Delhi")
                    .zipCode("110003")
                    .country("India")
                    .isDefault(true)
                    .build();
            address6 = addressRepository.save(address6);

            Address address7 = Address.builder()
                    .userId(owner7.getId())
                    .street("404 Sushi Ln")
                    .city("Mumbai")
                    .state("Maharashtra")
                    .zipCode("400004")
                    .country("India")
                    .isDefault(true)
                    .build();
            address7 = addressRepository.save(address7);

            // Seed Restaurants
            Restaurant restaurant1 = Restaurant.builder()
                    .name("Spice Route")
                    .description("Authentic Indian Cuisine")
                    .ownerId(owner1.getId())
                    .addressId(address1.getId())
                    .phone("9988776655")
                    .cuisineType("Indian")
                    .rating(new BigDecimal("4.5"))
                    .active(true)
                    .verified(true)
                    .opensAt(LocalTime.of(10, 0))
                    .closesAt(LocalTime.of(22, 0))
                    .build();
            restaurant1 = restaurantRepository.save(restaurant1);

            Restaurant restaurant2 = Restaurant.builder()
                    .name("Bella Italia")
                    .description("Classic Italian Dishes")
                    .ownerId(owner2.getId())
                    .addressId(address2.getId())
                    .phone("9988774433")
                    .cuisineType("Italian")
                    .rating(new BigDecimal("4.8"))
                    .active(true)
                    .verified(true)
                    .opensAt(LocalTime.of(11, 0))
                    .closesAt(LocalTime.of(23, 0))
                    .build();
            restaurant2 = restaurantRepository.save(restaurant2);

            Restaurant restaurant3 = Restaurant.builder()
                    .name("Dragon Wok")
                    .description("Authentic Chinese")
                    .ownerId(owner3.getId())
                    .addressId(address3.getId())
                    .phone("9988771111")
                    .cuisineType("Chinese")
                    .rating(new BigDecimal("4.2"))
                    .active(true)
                    .verified(true)
                    .opensAt(LocalTime.of(11, 0))
                    .closesAt(LocalTime.of(22, 0))
                    .build();
            restaurant3 = restaurantRepository.save(restaurant3);

            Restaurant restaurant4 = Restaurant.builder()
                    .name("Le Petit Bistro")
                    .description("French delicacies")
                    .ownerId(owner4.getId())
                    .addressId(address4.getId())
                    .phone("9988772222")
                    .cuisineType("French")
                    .rating(new BigDecimal("4.6"))
                    .active(true)
                    .verified(true)
                    .opensAt(LocalTime.of(10, 0))
                    .closesAt(LocalTime.of(21, 0))
                    .build();
            restaurant4 = restaurantRepository.save(restaurant4);

            Restaurant restaurant5 = Restaurant.builder()
                    .name("Curry House")
                    .description("Spicy Indian Food")
                    .ownerId(owner5.getId())
                    .addressId(address5.getId())
                    .phone("9988773333")
                    .cuisineType("Indian")
                    .rating(new BigDecimal("4.4"))
                    .active(true)
                    .verified(true)
                    .opensAt(LocalTime.of(9, 0))
                    .closesAt(LocalTime.of(23, 0))
                    .build();
            restaurant5 = restaurantRepository.save(restaurant5);

            Restaurant restaurant6 = Restaurant.builder()
                    .name("Pizza Paradise")
                    .description("Wood-fired Pizzas")
                    .ownerId(owner6.getId())
                    .addressId(address6.getId())
                    .phone("9988774444")
                    .cuisineType("Italian")
                    .rating(new BigDecimal("4.7"))
                    .active(true)
                    .verified(true)
                    .opensAt(LocalTime.of(12, 0))
                    .closesAt(LocalTime.of(23, 0))
                    .build();
            restaurant6 = restaurantRepository.save(restaurant6);

            Restaurant restaurant7 = Restaurant.builder()
                    .name("Tokyo Diner")
                    .description("Sushi and Ramen")
                    .ownerId(owner7.getId())
                    .addressId(address7.getId())
                    .phone("9988775555")
                    .cuisineType("Japanese")
                    .rating(new BigDecimal("4.9"))
                    .active(true)
                    .verified(true)
                    .opensAt(LocalTime.of(11, 30))
                    .closesAt(LocalTime.of(22, 30))
                    .build();
            restaurant7 = restaurantRepository.save(restaurant7);

            // Seed Food Items (Menu)
            FoodItem foodItem1 = FoodItem.builder()
                    .restaurantId(restaurant1.getId())
                    .name("Butter Chicken")
                    .description("Creamy tomato gravy with tender chicken pieces")
                    .price(new BigDecimal("350.00"))
                    .category("Main Course")
                    .veg(false)
                    .available(true)
                    .build();
            foodItemRepository.save(foodItem1);

            FoodItem foodItem2 = FoodItem.builder()
                    .restaurantId(restaurant1.getId())
                    .name("Paneer Tikka Masala")
                    .description("Grilled paneer in a spiced gravy")
                    .price(new BigDecimal("280.00"))
                    .category("Main Course")
                    .veg(true)
                    .available(true)
                    .build();
            foodItemRepository.save(foodItem2);

            FoodItem foodItem3 = FoodItem.builder()
                    .restaurantId(restaurant2.getId())
                    .name("Margherita Pizza")
                    .description("Classic pizza with fresh mozzarella and basil")
                    .price(new BigDecimal("450.00"))
                    .category("Pizza")
                    .veg(true)
                    .available(true)
                    .build();
            foodItemRepository.save(foodItem3);

            FoodItem foodItem4 = FoodItem.builder()
                    .restaurantId(restaurant2.getId())
                    .name("Spaghetti Carbonara")
                    .description("Pasta with creamy egg and pancetta sauce")
                    .price(new BigDecimal("380.00"))
                    .category("Pasta")
                    .veg(false)
                    .available(true)
                    .build();
            foodItemRepository.save(foodItem4);

            FoodItem foodItem5 = FoodItem.builder()
                    .restaurantId(restaurant3.getId())
                    .name("Kung Pao Chicken")
                    .description("Spicy diced chicken")
                    .price(new BigDecimal("320.00"))
                    .category("Main Course")
                    .veg(false)
                    .available(true)
                    .build();
            foodItemRepository.save(foodItem5);

            FoodItem foodItem6 = FoodItem.builder()
                    .restaurantId(restaurant3.getId())
                    .name("Veg Fried Rice")
                    .description("Classic fried rice")
                    .price(new BigDecimal("220.00"))
                    .category("Rice")
                    .veg(true)
                    .available(true)
                    .build();
            foodItemRepository.save(foodItem6);

            FoodItem foodItem7 = FoodItem.builder()
                    .restaurantId(restaurant4.getId())
                    .name("Coq au Vin")
                    .description("Chicken braised with wine")
                    .price(new BigDecimal("550.00"))
                    .category("Main Course")
                    .veg(false)
                    .available(true)
                    .build();
            foodItemRepository.save(foodItem7);

            FoodItem foodItem8 = FoodItem.builder()
                    .restaurantId(restaurant4.getId())
                    .name("French Onion Soup")
                    .description("Traditional onion soup")
                    .price(new BigDecimal("250.00"))
                    .category("Starter")
                    .veg(true)
                    .available(true)
                    .build();
            foodItemRepository.save(foodItem8);

            FoodItem foodItem9 = FoodItem.builder()
                    .restaurantId(restaurant5.getId())
                    .name("Palak Paneer")
                    .description("Spinach and paneer curry")
                    .price(new BigDecimal("290.00"))
                    .category("Main Course")
                    .veg(true)
                    .available(true)
                    .build();
            foodItemRepository.save(foodItem9);

            FoodItem foodItem10 = FoodItem.builder()
                    .restaurantId(restaurant5.getId())
                    .name("Chicken Tikka")
                    .description("Grilled chicken chunks")
                    .price(new BigDecimal("310.00"))
                    .category("Starter")
                    .veg(false)
                    .available(true)
                    .build();
            foodItemRepository.save(foodItem10);

            FoodItem foodItem11 = FoodItem.builder()
                    .restaurantId(restaurant6.getId())
                    .name("Pepperoni Pizza")
                    .description("Pizza with pepperoni slices")
                    .price(new BigDecimal("480.00"))
                    .category("Pizza")
                    .veg(false)
                    .available(true)
                    .build();
            foodItemRepository.save(foodItem11);

            FoodItem foodItem12 = FoodItem.builder()
                    .restaurantId(restaurant6.getId())
                    .name("Garlic Bread")
                    .description("Cheesy garlic bread")
                    .price(new BigDecimal("180.00"))
                    .category("Sides")
                    .veg(true)
                    .available(true)
                    .build();
            foodItemRepository.save(foodItem12);

            FoodItem foodItem13 = FoodItem.builder()
                    .restaurantId(restaurant7.getId())
                    .name("Sushi Platter")
                    .description("Assorted sushi rolls")
                    .price(new BigDecimal("600.00"))
                    .category("Main Course")
                    .veg(false)
                    .available(true)
                    .build();
            foodItemRepository.save(foodItem13);

            FoodItem foodItem14 = FoodItem.builder()
                    .restaurantId(restaurant7.getId())
                    .name("Miso Soup")
                    .description("Traditional Japanese soup")
                    .price(new BigDecimal("150.00"))
                    .category("Soup")
                    .veg(true)
                    .available(true)
                    .build();
            foodItemRepository.save(foodItem14);

            System.out.println("Demo data seeded successfully.");
        }
    }
}
