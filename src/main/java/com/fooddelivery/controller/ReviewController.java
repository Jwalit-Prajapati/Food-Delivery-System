package com.fooddelivery.controller;

import com.fooddelivery.dto.request.ReviewRequest;
import com.fooddelivery.dto.response.ReviewResponse;
import com.fooddelivery.mapper.ReviewMapper;
import com.fooddelivery.model.Review;
import com.fooddelivery.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @PostMapping
    public ResponseEntity<ReviewResponse> create(@jakarta.validation.Valid @RequestBody ReviewRequest request) {
        Review review = reviewMapper.toEntity(request);
        return new ResponseEntity<>(reviewMapper.toResponse(reviewService.create(review)), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewMapper.toResponse(reviewService.getById(id)));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<ReviewResponse>> getByRestaurant(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(reviewService.getByRestaurant(restaurantId).stream().map(reviewMapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getByUser(userId).stream().map(reviewMapper::toResponse).collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> update(@PathVariable Long id, @jakarta.validation.Valid @RequestBody ReviewRequest request) {
        Review review = reviewMapper.toEntity(request);
        review.setId(id);
        return ResponseEntity.ok(reviewMapper.toResponse(reviewService.update(review)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
