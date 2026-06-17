package com.fooddelivery.web;

import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.model.*;
import com.fooddelivery.service.AddressService;
import com.fooddelivery.service.DeliveryService;
import com.fooddelivery.service.PricingService;
import com.fooddelivery.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/delivery")
public class DeliveryPageController {

    private final DeliveryService   deliveryService;
    private final PricingService    pricingService;
    private final RestaurantService restaurantService;
    private final AddressService    addressService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = requireDriver(session);
        if (user == null) return "redirect:/login";

        List<Order> queue  = deliveryService.getDeliveryQueue();
        List<Order> active = deliveryService.getActiveDeliveriesByPartner(user.getId());

        Map<Long, Restaurant> restaurantMap = new HashMap<>();
        Map<Long, Address>    addressMap    = new HashMap<>();
        for (Order o : queue)  hydrate(o, restaurantMap, addressMap);
        for (Order o : active) hydrate(o, restaurantMap, addressMap);

        model.addAttribute("queue", queue);
        model.addAttribute("active", active);
        model.addAttribute("restaurantMap", restaurantMap);
        model.addAttribute("addressMap", addressMap);
        addCommon(session, model);
        return "delivery-dashboard";
    }

    @PostMapping("/orders/{orderId}/accept")
    public String accept(@PathVariable Long orderId, HttpSession session) {
        User user = requireDriver(session);
        if (user == null) return "redirect:/login";
        try {
            deliveryService.acceptDelivery(orderId, user.getId());
            SessionUtil.flash(session, "Order #" + orderId + " accepted. Head to the restaurant!");
        } catch (BusinessException | ResourceNotFoundException e) {
            SessionUtil.flashError(session, e.getMessage());
        }
        return "redirect:/delivery/dashboard";
    }

    @PostMapping("/orders/{orderId}/delivered")
    public String complete(@PathVariable Long orderId, HttpSession session) {
        User user = requireDriver(session);
        if (user == null) return "redirect:/login";
        try {
            deliveryService.completeDelivery(orderId, user.getId());
            SessionUtil.flash(session, "Marked as delivered. Nice work!");
        } catch (BusinessException | ResourceNotFoundException e) {
            SessionUtil.flashError(session, e.getMessage());
        }
        return "redirect:/delivery/dashboard";
    }

    @GetMapping("/history")
    public String history(HttpSession session, Model model) {
        User user = requireDriver(session);
        if (user == null) return "redirect:/login";

        List<Order> completed = deliveryService.getCompletedDeliveriesByPartner(user.getId());
        // Earnings computation delegated to PricingService.
        BigDecimal earnings = pricingService.computeEarnings(completed);

        Map<Long, Restaurant> restaurantMap = new HashMap<>();
        Map<Long, Address>    addressMap    = new HashMap<>();
        for (Order o : completed) hydrate(o, restaurantMap, addressMap);

        model.addAttribute("completed", completed);
        model.addAttribute("earnings", earnings);
        model.addAttribute("tripCount", completed.size());
        model.addAttribute("restaurantMap", restaurantMap);
        model.addAttribute("addressMap", addressMap);
        addCommon(session, model);
        return "delivery-history";
    }

    /* ---------- Helpers ---------- */

    private void hydrate(Order o,
                         Map<Long, Restaurant> rmap,
                         Map<Long, Address>    amap) {
        if (o.getRestaurantId() != null && !rmap.containsKey(o.getRestaurantId())) {
            try { rmap.put(o.getRestaurantId(), restaurantService.getById(o.getRestaurantId())); }
            catch (ResourceNotFoundException ignored) {}
        }
        if (o.getAddressId() != null && !amap.containsKey(o.getAddressId())) {
            try { amap.put(o.getAddressId(), addressService.getById(o.getAddressId())); }
            catch (ResourceNotFoundException ignored) {}
        }
    }

    private User requireDriver(HttpSession session) {
        User user = SessionUtil.getCurrentUser(session);
        if (user == null) return null;
        if (user.getRole() != User.Role.DELIVERY_PARTNER && user.getRole() != User.Role.ADMIN) {
            SessionUtil.flashError(session, "Delivery partner access required");
            return null;
        }
        return user;
    }

    private void addCommon(HttpSession session, Model model) {
        User user = SessionUtil.getCurrentUser(session);
        model.addAttribute("currentUser", user);
        model.addAttribute("cartCount", 0);
        Object flash      = session.getAttribute(SessionUtil.FLASH);
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
