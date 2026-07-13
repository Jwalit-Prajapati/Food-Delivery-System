package com.fooddelivery.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Exception handler for JSP web page controllers (@Controller).
 * Handles exceptions by returning proper view names rather than JSON,
 * preventing the 500 "Internal Server Error" on login/register pages.
 *
 * Note: @ControllerAdvice(annotations = Controller.class) scopes this handler
 * to only non-REST controllers (i.e. the web UI @Controller beans).
 */
@ControllerAdvice(annotations = Controller.class)
public class WebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebExceptionHandler.class);

    /**
     * Catches any unexpected exception from a web @Controller and logs it.
     * Returns the login page with a generic error message so the user
     * sees a proper page instead of a raw JSON 500 response.
     */
    @ExceptionHandler(Exception.class)
    public String handleGenericWebError(Exception ex, Model model) {
        log.error("Unexpected error in web controller: {}", ex.getMessage(), ex);
        model.addAttribute("error", "An unexpected error occurred. Please try again.");
        return "login";
    }

    /**
     * Catches ResourceNotFoundException from web controllers
     * and redirects to login.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        log.warn("Resource not found in web controller: {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return "login";
    }

    /**
     * Catches BusinessException from web controllers and returns
     * the login page with the business error message displayed.
     */
    @ExceptionHandler(BusinessException.class)
    public String handleBusiness(BusinessException ex, Model model) {
        log.warn("Business rule violation in web controller: {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return "login";
    }
}
