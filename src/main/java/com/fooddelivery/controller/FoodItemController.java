package com.fooddelivery.controller;

import com.fooddelivery.dto.request.FoodItemCreateRequest;
import com.fooddelivery.dto.response.FoodItemResponse;
import com.fooddelivery.mapper.FoodItemMapper;
import com.fooddelivery.model.FoodItem;
import com.fooddelivery.service.FoodItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/food-items")
public class FoodItemController {

    private final FoodItemService foodItemService;
    private final FoodItemMapper foodItemMapper;

    @PostMapping
    public ResponseEntity<FoodItemResponse> create(@jakarta.validation.Valid @RequestBody FoodItemCreateRequest request) {
        FoodItem item = foodItemMapper.toEntity(request);
        return new ResponseEntity<>(foodItemMapper.toResponse(foodItemService.create(item)), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodItemResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(foodItemMapper.toResponse(foodItemService.getById(id)));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<FoodItemResponse>> getByRestaurant(
            @PathVariable Long restaurantId) {
        List<FoodItem> items = foodItemService.getByRestaurant(restaurantId);
        return ResponseEntity.ok(items.stream().map(foodItemMapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<FoodItemResponse>> search(@RequestParam String query) {
        return ResponseEntity.ok(foodItemService.search(query).stream().map(foodItemMapper::toResponse).collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodItemResponse> update(@PathVariable Long id, @jakarta.validation.Valid @RequestBody FoodItemCreateRequest request) {
        FoodItem item = foodItemMapper.toEntity(request);
        item.setId(id);
        return ResponseEntity.ok(foodItemMapper.toResponse(foodItemService.update(item)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        foodItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
