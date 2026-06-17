package com.fooddelivery.web;

import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.model.*;
import com.fooddelivery.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/owner")
public class OwnerPageController {

    @Autowired private RestaurantService restaurantService;
    @Autowired private FoodItemService foodItemService;
    @Autowired private OrderService orderService;
    @Autowired private AddressService addressService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = requireOwner(session);
        if (user == null) return "redirect:/login";

        List<Restaurant> restaurants = restaurantService.getByOwner(user.getId());
        List<Order> recentOrders = new ArrayList<>();
        for (Restaurant r : restaurants) {
            recentOrders.addAll(orderService.getByRestaurant(r.getId()));
        }
        model.addAttribute("restaurants", restaurants);
        model.addAttribute("recentOrders", recentOrders);
        addCommon(session, model);
        return "owner-dashboard";
    }

    @PostMapping("/restaurants/add")
    public String addRestaurant(@RequestParam String name,
                                @RequestParam(required = false) String description,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String cuisineType,
                                @RequestParam(required = false) String opensAt,
                                @RequestParam(required = false) String closesAt,
                                @RequestParam String street,
                                @RequestParam String city,
                                @RequestParam String state,
                                @RequestParam String zipCode,
                                @RequestParam(required = false, defaultValue = "India") String country,
                                @RequestParam(required = false) String landmark,
                                HttpSession session) {
        User user = requireOwner(session);
        if (user == null) return "redirect:/login";
        try {
            // 1) Create the restaurant's address first so we have an ID for the FK.
            Address address = new Address();
            address.setUserId(user.getId());
            address.setStreet(street);
            address.setCity(city);
            address.setState(state);
            address.setZipCode(zipCode);
            address.setCountry(country == null || country.isBlank() ? "India" : country);
            address.setLandmark(landmark);
            address.setDefault(false);
            Address savedAddress = addressService.create(address);

            // 2) Create the restaurant pointing at that address.
            Restaurant r = new Restaurant();
            r.setName(name);
            r.setDescription(description);
            r.setOwnerId(user.getId());
            r.setAddressId(savedAddress.getId());
            r.setPhone(phone);
            r.setCuisineType(cuisineType);
            r.setActive(true);
            r.setRating(BigDecimal.ZERO);
            if (opensAt != null && !opensAt.isBlank()) r.setOpensAt(LocalTime.parse(opensAt));
            if (closesAt != null && !closesAt.isBlank()) r.setClosesAt(LocalTime.parse(closesAt));
            restaurantService.create(r);
            SessionUtil.flash(session, "Restaurant created");
        } catch (Exception e) {
            SessionUtil.flashError(session, e.getMessage());
        }
        return "redirect:/owner/dashboard";
    }

    @GetMapping("/restaurants/{id}/menu")
    public String menu(@PathVariable Long id, HttpSession session, Model model) {
        User user = requireOwner(session);
        if (user == null) return "redirect:/login";

        Restaurant restaurant = restaurantService.getById(id);
        if (!restaurant.getOwnerId().equals(user.getId())) {
            SessionUtil.flashError(session, "Not your restaurant");
            return "redirect:/owner/dashboard";
        }
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("items", foodItemService.getByRestaurant(id));
        addCommon(session, model);
        return "owner-menu";
    }

    @PostMapping("/restaurants/{id}/menu/add")
    public String addMenuItem(@PathVariable Long id,
                              @RequestParam String name,
                              @RequestParam(required = false) String description,
                              @RequestParam BigDecimal price,
                              @RequestParam(required = false) String category,
                              @RequestParam(required = false) String imageUrl,
                              @RequestParam(defaultValue = "true") boolean veg,
                              HttpSession session) {
        User user = requireOwner(session);
        if (user == null) return "redirect:/login";
        try {
            Restaurant restaurant = restaurantService.getById(id);
            if (!restaurant.getOwnerId().equals(user.getId())) {
                SessionUtil.flashError(session, "Not your restaurant");
                return "redirect:/owner/dashboard";
            }
            FoodItem item = new FoodItem();
            item.setRestaurantId(id);
            item.setName(name);
            item.setDescription(description);
            item.setPrice(price);
            item.setCategory(category);
            item.setImageUrl(imageUrl);
            item.setVeg(veg);
            item.setAvailable(true);
            foodItemService.create(item);
            SessionUtil.flash(session, "Menu item added");
        } catch (Exception e) {
            SessionUtil.flashError(session, e.getMessage());
        }
        return "redirect:/owner/restaurants/" + id + "/menu";
    }

    /** Show the edit form for a menu item. */
    @GetMapping("/menu/{itemId}/edit")
    public String editMenuItemPage(@PathVariable Long itemId, HttpSession session, Model model) {
        User user = requireOwner(session);
        if (user == null) return "redirect:/login";
        FoodItem item = foodItemService.getById(itemId);
        Restaurant restaurant = restaurantService.getById(item.getRestaurantId());
        if (!restaurant.getOwnerId().equals(user.getId())) {
            SessionUtil.flashError(session, "Not your restaurant");
            return "redirect:/owner/dashboard";
        }
        model.addAttribute("item", item);
        model.addAttribute("restaurant", restaurant);
        addCommon(session, model);
        return "owner-menu-edit";
    }

    @PostMapping("/menu/{itemId}/edit")
    public String editMenuItem(@PathVariable Long itemId,
                               @RequestParam String name,
                               @RequestParam(required = false) String description,
                               @RequestParam BigDecimal price,
                               @RequestParam(required = false) String category,
                               @RequestParam(required = false) String imageUrl,
                               @RequestParam(defaultValue = "false") boolean veg,
                               HttpSession session) {
        User user = requireOwner(session);
        if (user == null) return "redirect:/login";
        try {
            FoodItem item = foodItemService.getById(itemId);
            Restaurant restaurant = restaurantService.getById(item.getRestaurantId());
            if (!restaurant.getOwnerId().equals(user.getId())) {
                SessionUtil.flashError(session, "Not your restaurant");
                return "redirect:/owner/dashboard";
            }
            item.setName(name);
            item.setDescription(description);
            item.setPrice(price);
            item.setCategory(category);
            item.setImageUrl(imageUrl);
            item.setVeg(veg);
            foodItemService.update(item);
            SessionUtil.flash(session, "Menu item updated");
            return "redirect:/owner/restaurants/" + item.getRestaurantId() + "/menu";
        } catch (Exception e) {
            SessionUtil.flashError(session, e.getMessage());
            return "redirect:/owner/menu/" + itemId + "/edit";
        }
    }

    @PostMapping("/menu/{itemId}/toggle")
    public String toggleAvailability(@PathVariable Long itemId,
                                     @RequestParam Long restaurantId,
                                     @RequestParam boolean available,
                                     HttpSession session) {
        if (requireOwner(session) == null) return "redirect:/login";
        foodItemService.toggleAvailability(itemId, available);
        return "redirect:/owner/restaurants/" + restaurantId + "/menu";
    }

    @PostMapping("/menu/{itemId}/delete")
    public String deleteMenuItem(@PathVariable Long itemId,
                                 @RequestParam Long restaurantId,
                                 HttpSession session) {
        if (requireOwner(session) == null) return "redirect:/login";
        foodItemService.delete(itemId);
        SessionUtil.flash(session, "Menu item removed");
        return "redirect:/owner/restaurants/" + restaurantId + "/menu";
    }

    /** Restaurant accepts a PLACED order -> CONFIRMED. */
    @PostMapping("/orders/{orderId}/accept")
    public String acceptOrder(@PathVariable Long orderId, HttpSession session) {
        if (requireOwner(session) == null) return "redirect:/login";
        try {
            orderService.acceptOrder(orderId);
            SessionUtil.flash(session, "Order accepted");
        } catch (BusinessException | ResourceNotFoundException e) {
            SessionUtil.flashError(session, e.getMessage());
        }
        return "redirect:/owner/dashboard";
    }

    /** Restaurant rejects an order. */
    @PostMapping("/orders/{orderId}/reject")
    public String rejectOrder(@PathVariable Long orderId, HttpSession session) {
        if (requireOwner(session) == null) return "redirect:/login";
        try {
            orderService.rejectOrder(orderId);
            SessionUtil.flash(session, "Order rejected");
        } catch (BusinessException | ResourceNotFoundException e) {
            SessionUtil.flashError(session, e.getMessage());
        }
        return "redirect:/owner/dashboard";
    }

    /** Restaurant marks the order ready for the delivery partner to pick up. */
    @PostMapping("/orders/{orderId}/ready")
    public String markReady(@PathVariable Long orderId, HttpSession session) {
        if (requireOwner(session) == null) return "redirect:/login";
        try {
            orderService.markReadyForPickup(orderId);
            SessionUtil.flash(session, "Order marked ready for pickup");
        } catch (BusinessException | ResourceNotFoundException e) {
            SessionUtil.flashError(session, e.getMessage());
        }
        return "redirect:/owner/dashboard";
    }

    @PostMapping("/orders/{orderId}/status")
    public String updateOrderStatus(@PathVariable Long orderId,
                                    @RequestParam String status,
                                    HttpSession session) {
        if (requireOwner(session) == null) return "redirect:/login";
        try {
            orderService.updateStatus(orderId, Order.Status.valueOf(status));
            SessionUtil.flash(session, "Order status updated");
        } catch (BusinessException | ResourceNotFoundException | IllegalArgumentException e) {
            SessionUtil.flashError(session, e.getMessage());
        }
        return "redirect:/owner/dashboard";
    }

    /* ---------- Helpers ---------- */

    private User requireOwner(HttpSession session) {
        User user = SessionUtil.getCurrentUser(session);
        if (user == null) return null;
        if (user.getRole() != User.Role.RESTAURANT_OWNER && user.getRole() != User.Role.ADMIN) {
            SessionUtil.flashError(session, "Restaurant-owner access required");
            return null;
        }
        return user;
    }

    private void addCommon(HttpSession session, Model model) {
        User user = SessionUtil.getCurrentUser(session);
        model.addAttribute("currentUser", user);
        model.addAttribute("cartCount", 0);
        Object flash = session.getAttribute(SessionUtil.FLASH);
        Object flashError = session.getAttribute(SessionUtil.FLASH_ERROR);
        if (flash != null) {
            model.addAttribute("flash", flash);
            session.removeAttribute(SessionUtil.FLASH);
        }
        if (flashError != null) {
            model.addAttribute("flashError", flashError);
            session.removeAttribute(SessionUtil.FLASH_ERROR);
        }
    }
}