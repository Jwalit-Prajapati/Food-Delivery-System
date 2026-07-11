package com.fooddelivery.web;

import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.model.User;
import com.fooddelivery.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class AuthPageController {

    private final UserService userService;

    @GetMapping("/")
    public String root(HttpSession session) {
        return SessionUtil.isLoggedIn(session) ? "redirect:/home" : "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        if (SessionUtil.isLoggedIn(session)) {
            return "redirect:/home";
        }
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        try {
            User user = userService.login(email, password);
            SessionUtil.setCurrentUser(session, user);
            SessionUtil.flash(session, "Welcome back, " + user.getName() + "!");
            return roleHome(user.getRole());
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            return "login";
        }
    }

    private String roleHome(User.Role role) {
        if (role == User.Role.RESTAURANT_OWNER)   return "redirect:/owner/dashboard";
        if (role == User.Role.DELIVERY_PARTNER)   return "redirect:/delivery/dashboard";
        if (role == User.Role.ADMIN)              return "redirect:/admin/dashboard";
        return "redirect:/home";
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session) {
        if (SessionUtil.isLoggedIn(session)) {
            return "redirect:/home";
        }
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam(required = false) String phone,
                             @RequestParam(defaultValue = "CUSTOMER") String role,
                             HttpSession session,
                             Model model) {
        try {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setPhone(phone);
            user.setRole(User.Role.valueOf(role));
            user.setActive(true);
            User created = userService.register(user);
            SessionUtil.setCurrentUser(session, created);
            SessionUtil.flash(session, "Account created. Welcome, " + created.getName() + "!");
            return roleHome(created.getRole());
        } catch (BusinessException | IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            model.addAttribute("phone", phone);
            model.addAttribute("role", role);
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
