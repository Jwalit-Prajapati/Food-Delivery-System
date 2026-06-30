package com.fooddelivery.web;

import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.model.*;
import com.fooddelivery.service.AddressService;
import com.fooddelivery.service.CartService;
import com.fooddelivery.service.FoodItemService;
import com.fooddelivery.service.OrderService;
import com.fooddelivery.service.PricingService;
import com.fooddelivery.service.RestaurantService;
import com.fooddelivery.service.ReviewService;
import com.fooddelivery.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CustomerPageController {

    private final RestaurantService restaurantService;
    private final FoodItemService   foodItemService;
    private final CartService       cartService;
    private final OrderService      orderService;
    private final PricingService    pricingService;
    private final AddressService    addressService;
    private final ReviewService     reviewService;
    private final UserService       userService;

    /* ---------- Home (restaurants list) ---------- */

    @GetMapping("/home")
    public String home(@RequestParam(required = false) String search,
                       @RequestParam(required = false) String cuisine,
                       HttpSession session,
                       Model model) {

        List<Restaurant> restaurants;
        if (search != null && !search.isBlank()) {
            restaurants = restaurantService.search(search.trim());
        } else if (cuisine != null && !cuisine.isBlank()) {
            restaurants = restaurantService.getByCuisine(cuisine.trim());
        } else {
            restaurants = restaurantService.getActive();
        }
        model.addAttribute("restaurants", restaurants);
        model.addAttribute("search", search);
        model.addAttribute("cuisine", cuisine);
        addCommon(session, model);
        return "home";
    }

    /* ---------- Restaurant detail / menu ---------- */

    @GetMapping("/restaurants")
    public String restaurantsList() {
        return "redirect:/home";
    }

    @GetMapping("/restaurants/{id}")
    public String restaurantDetail(@PathVariable Long id,
                                   @RequestParam(required = false) String diet,
                                   @RequestParam(required = false) String category,
                                   HttpSession session, Model model) {

        Restaurant restaurant = restaurantService.getById(id);
        List<FoodItem> items = foodItemService.getAvailableByRestaurant(id);

        // Apply optional filters
        if ("veg".equalsIgnoreCase(diet)) {
            items = items.stream().filter(FoodItem::isVeg).collect(java.util.stream.Collectors.toList());
        } else if ("nonveg".equalsIgnoreCase(diet)) {
            items = items.stream().filter(fi -> !fi.isVeg()).collect(java.util.stream.Collectors.toList());
        }
        if (category != null && !category.isBlank()) {
            items = items.stream()
                    .filter(fi -> category.equalsIgnoreCase(fi.getCategory()))
                    .collect(java.util.stream.Collectors.toList());
        }

        List<Review> reviews = reviewService.getByRestaurant(id);

        // Group menu items by category for nicer rendering
        Map<String, java.util.List<FoodItem>> grouped = new java.util.LinkedHashMap<>();
        for (FoodItem fi : items) {
            String cat = fi.getCategory() == null ? "Other" : fi.getCategory();
            grouped.computeIfAbsent(cat, k -> new java.util.ArrayList<>()).add(fi);
        }

        // Distinct categories for the chip bar (built from the full menu, not the filtered list)
        java.util.Set<String> allCategories = new java.util.LinkedHashSet<>();
        for (FoodItem fi : foodItemService.getAvailableByRestaurant(id)) {
            if (fi.getCategory() != null) allCategories.add(fi.getCategory());
        }

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("itemsByCategory", grouped);
        model.addAttribute("reviews", reviews);
        model.addAttribute("diet", diet);
        model.addAttribute("activeCategory", category);
        model.addAttribute("allCategories", allCategories);
        addCommon(session, model);
        return "restaurant";
    }

    /* ---------- Cart ---------- */

    @GetMapping("/cart")
    public String cart(HttpSession session, Model model) {
        User user = requireLogin(session);
        if (user == null) return "redirect:/login";

        Cart cart = cartService.getCartWithItems(user.getId());
        List<Address> addresses = addressService.getByUser(user.getId());

        // Delegate pricing to PricingService (single source of truth for all financial rules).
        Pricing pricing = pricingService.computePricingForCart(cart);
        BigDecimal subtotal = pricing.getSubtotal();
        BigDecimal tax = pricing.getTax();
        BigDecimal total = pricing.getTotal();

        // Enrich CartItems with food-item display data for the JSP template.
        // CartItem only stores FKs; we hydrate names and prices here in the view layer.
        List<CartItemView> cartItemViews = cart.getItems().stream()
                .map(ci -> {
                    try {
                        com.fooddelivery.model.FoodItem food = foodItemService.getById(ci.getFoodItemId());
                        return new CartItemView(ci, food.getName(), food.getPrice(), food.isVeg(), food.getImageUrl());
                    } catch (com.fooddelivery.exception.ResourceNotFoundException e) {
                        // Food item may have been deleted — show placeholder
                        return new CartItemView(ci, "(unavailable)", java.math.BigDecimal.ZERO, true, null);
                    }
                })
                .collect(java.util.stream.Collectors.toList());

        Restaurant restaurant = null;
        if (cart.getRestaurantId() != null) {
            try { restaurant = restaurantService.getById(cart.getRestaurantId()); }
            catch (ResourceNotFoundException ignored) {}
        }

        model.addAttribute("cart", cart);
        model.addAttribute("cartItems", cartItemViews);
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("addresses", addresses);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("tax", tax);
        model.addAttribute("deliveryFee", pricing.getDeliveryFee());
        model.addAttribute("total", total);
        addCommon(session, model);
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long foodItemId,
                            @RequestParam(defaultValue = "1") int quantity,
                            @RequestParam(required = false) Long restaurantId,
                            HttpSession session) {
        User user = requireLogin(session);
        if (user == null) return "redirect:/login";
        try {
            cartService.addItem(user.getId(), foodItemId, quantity);
            SessionUtil.flash(session, "Added to cart");
        } catch (BusinessException | ResourceNotFoundException e) {
            SessionUtil.flashError(session, e.getMessage());
        }
        return restaurantId != null
                ? "redirect:/restaurants/" + restaurantId
                : "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam Long itemId,
                             @RequestParam int quantity,
                             HttpSession session) {
        if (!SessionUtil.isLoggedIn(session)) return "redirect:/login";
        cartService.updateItemQuantity(itemId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long itemId, HttpSession session) {
        if (!SessionUtil.isLoggedIn(session)) return "redirect:/login";
        cartService.removeItem(itemId);
        SessionUtil.flash(session, "Item removed");
        return "redirect:/cart";
    }

    @PostMapping("/cart/clear")
    public String clearCart(HttpSession session) {
        User user = requireLogin(session);
        if (user == null) return "redirect:/login";
        cartService.clearCart(user.getId());
        SessionUtil.flash(session, "Cart cleared");
        return "redirect:/cart";
    }

    /* ---------- Orders ---------- */

    @PostMapping("/orders/place")
    public String placeOrder(@RequestParam Long addressId,
                             @RequestParam(defaultValue = "COD") String paymentMethod,
                             HttpSession session) {
        User user = requireLogin(session);
        if (user == null) return "redirect:/login";
        try {
            Order order = orderService.placeOrder(user.getId(), addressId, paymentMethod);
            SessionUtil.flash(session, "Order #" + order.getId() + " placed successfully");
            return "redirect:/orders/" + order.getId();
        } catch (BusinessException e) {
            SessionUtil.flashError(session, e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/orders")
    public String orders(HttpSession session, Model model) {
        User user = requireLogin(session);
        if (user == null) return "redirect:/login";
        List<Order> orders = orderService.getByUser(user.getId());

        Map<Long, Restaurant> restaurantMap = new HashMap<>();
        for (Order o : orders) {
            restaurantMap.computeIfAbsent(o.getRestaurantId(), rid -> {
                try { return restaurantService.getById(rid); }
                catch (ResourceNotFoundException e) { return null; }
            });
        }
        model.addAttribute("orders", orders);
        model.addAttribute("restaurantMap", restaurantMap);
        addCommon(session, model);
        return "orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, HttpSession session, Model model) {
        User user = requireLogin(session);
        if (user == null) return "redirect:/login";

        Order order = orderService.getById(id);
        if (!order.getUserId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            SessionUtil.flashError(session, "You don't have access to that order");
            return "redirect:/orders";
        }
        Restaurant restaurant = null;
        try { restaurant = restaurantService.getById(order.getRestaurantId()); }
        catch (ResourceNotFoundException ignored) {}
        Address address = null;
        try { address = addressService.getById(order.getAddressId()); }
        catch (ResourceNotFoundException ignored) {}

        // Enrich order items with food-item name for display.
        // OrderItem stores only the FK (foodItemId); JSP needs the name.
        List<OrderItemView> orderItemViews = order.getItems().stream()
                .map(oi -> {
                    String name = "(item removed)";
                    try {
                        name = foodItemService.getById(oi.getFoodItemId()).getName();
                    } catch (ResourceNotFoundException ignored2) {}
                    return new OrderItemView(oi, name);
                })
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderItemViews);
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("address", address);
        addCommon(session, model);
        return "order-detail";
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(@PathVariable Long id, HttpSession session) {
        User user = requireLogin(session);
        if (user == null) return "redirect:/login";
        try {
            orderService.cancel(id);
            SessionUtil.flash(session, "Order cancelled");
        } catch (BusinessException e) {
            SessionUtil.flashError(session, e.getMessage());
        }
        return "redirect:/orders/" + id;
    }

    /* ---------- Profile + addresses ---------- */

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = requireLogin(session);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", userService.getById(user.getId()));
        model.addAttribute("addresses", addressService.getByUser(user.getId()));
        addCommon(session, model);
        return "profile";
    }

    @PostMapping("/profile/addresses/add")
    public String addAddress(@RequestParam String street,
                             @RequestParam String city,
                             @RequestParam String state,
                             @RequestParam String zipCode,
                             @RequestParam(required = false) String landmark,
                             @RequestParam(defaultValue = "false") boolean isDefault,
                             HttpSession session) {
        User user = requireLogin(session);
        if (user == null) return "redirect:/login";

        Address a = new Address();
        a.setUserId(user.getId());
        a.setStreet(street);
        a.setCity(city);
        a.setState(state);
        a.setZipCode(zipCode);
        a.setLandmark(landmark);
        a.setDefault(isDefault);
        addressService.create(a);
        SessionUtil.flash(session, "Address added");
        return "redirect:/profile";
    }

    @PostMapping("/profile/addresses/delete")
    public String deleteAddress(@RequestParam Long addressId, HttpSession session) {
        if (!SessionUtil.isLoggedIn(session)) return "redirect:/login";
        addressService.delete(addressId);
        SessionUtil.flash(session, "Address removed");
        return "redirect:/profile";
    }

    /* ---------- Reviews ---------- */

    @PostMapping("/reviews/add")
    public String addReview(@RequestParam Long restaurantId,
                            @RequestParam int rating,
                            @RequestParam(required = false) String comment,
                            @RequestParam(required = false) Long orderId,
                            HttpSession session) {
        User user = requireLogin(session);
        if (user == null) return "redirect:/login";
        try {
            Review r = new Review();
            r.setUserId(user.getId());
            r.setRestaurantId(restaurantId);
            r.setRating(rating);
            r.setComment(comment);
            r.setOrderId(orderId);
            reviewService.create(r);
            SessionUtil.flash(session, "Thanks for your review!");
        } catch (BusinessException e) {
            SessionUtil.flashError(session, e.getMessage());
        }
        return "redirect:/restaurants/" + restaurantId;
    }

    /* ---------- Helpers ---------- */

    private User requireLogin(HttpSession session) {
        return SessionUtil.getCurrentUser(session);
    }

    private void addCommon(HttpSession session, Model model) {
        User user = SessionUtil.getCurrentUser(session);
        model.addAttribute("currentUser", user);
        if (user != null) {
            Cart cart = cartService.getCartWithItems(user.getId());
            int cartCount = cart.getItems().stream().mapToInt(CartItem::getQuantity).sum();
            model.addAttribute("cartCount", cartCount);
        } else {
            model.addAttribute("cartCount", 0);
        }
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
