package com.fooddelivery.web;

import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.model.*;
import com.fooddelivery.service.AnalyticsService;
import com.fooddelivery.service.RestaurantService;
import com.fooddelivery.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminPageController {

    private final UserService        userService;
    private final RestaurantService  restaurantService;
    private final AnalyticsService   analyticsService;

    /* ---------- Dashboard ---------- */

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (requireAdmin(session) == null) return "redirect:/login";
        model.addAttribute("metrics", analyticsService.overview());
        model.addAttribute("pendingCount", restaurantService.getPendingVerification().size());
        addCommon(session, model);
        return "admin-dashboard";
    }

    /* ---------- Users ---------- */

    @GetMapping("/users")
    public String users(@RequestParam(required = false) String role,
                        HttpSession session, Model model) {
        if (requireAdmin(session) == null) return "redirect:/login";
        List<User> users;
        if (role != null && !role.isBlank()) {
            try {
                users = userService.getByRole(User.Role.valueOf(role));
            } catch (IllegalArgumentException e) {
                users = userService.getAll();
            }
        } else {
            users = userService.getAll();
        }
        model.addAttribute("users", users);
        model.addAttribute("filterRole", role);
        addCommon(session, model);
        return "admin-users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUserActive(@PathVariable Long id,
                                   @RequestParam boolean active,
                                   HttpSession session) {
        User admin = requireAdmin(session);
        if (admin == null) return "redirect:/login";
        if (admin.getId().equals(id)) {
            SessionUtil.flashError(session, "You can't suspend your own account");
            return "redirect:/admin/users";
        }
        try {
            userService.setActive(id, active);
            SessionUtil.flash(session, active ? "User reactivated" : "User suspended");
        } catch (ResourceNotFoundException e) {
            SessionUtil.flashError(session, e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /* ---------- Restaurant verification ---------- */

    @GetMapping("/restaurants")
    public String restaurants(@RequestParam(defaultValue = "all") String filter,
                              HttpSession session, Model model) {
        if (requireAdmin(session) == null) return "redirect:/login";
        List<Restaurant> list;
        switch (filter) {
            case "pending": list = restaurantService.getPendingVerification(); break;
            case "active":  list = restaurantService.getActive();              break;
            default:        list = restaurantService.getAll();
        }
        model.addAttribute("restaurants", list);
        model.addAttribute("filter", filter);
        addCommon(session, model);
        return "admin-restaurants";
    }

    @PostMapping("/restaurants/{id}/verify")
    public String verify(@PathVariable Long id, HttpSession session) {
        if (requireAdmin(session) == null) return "redirect:/login";
        try {
            restaurantService.setVerified(id, true);
            SessionUtil.flash(session, "Restaurant approved and is now live");
        } catch (ResourceNotFoundException e) {
            SessionUtil.flashError(session, e.getMessage());
        }
        return "redirect:/admin/restaurants?filter=pending";
    }

    @PostMapping("/restaurants/{id}/unverify")
    public String unverify(@PathVariable Long id, HttpSession session) {
        if (requireAdmin(session) == null) return "redirect:/login";
        try {
            restaurantService.setVerified(id, false);
            SessionUtil.flash(session, "Restaurant un-verified (hidden from customers)");
        } catch (ResourceNotFoundException e) {
            SessionUtil.flashError(session, e.getMessage());
        }
        return "redirect:/admin/restaurants";
    }

    @PostMapping("/restaurants/{id}/toggle")
    public String toggleRestaurant(@PathVariable Long id,
                                   @RequestParam boolean active,
                                   HttpSession session) {
        if (requireAdmin(session) == null) return "redirect:/login";
        try {
            restaurantService.setActive(id, active);
            SessionUtil.flash(session, active ? "Restaurant reactivated" : "Restaurant suspended");
        } catch (ResourceNotFoundException e) {
            SessionUtil.flashError(session, e.getMessage());
        }
        return "redirect:/admin/restaurants";
    }

    /* ---------- Helpers ---------- */

    private User requireAdmin(HttpSession session) {
        User user = SessionUtil.getCurrentUser(session);
        if (user == null) return null;
        if (user.getRole() != User.Role.ADMIN) {
            SessionUtil.flashError(session, "Admin access required");
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
