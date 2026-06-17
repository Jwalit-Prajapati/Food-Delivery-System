package com.fooddelivery.controller;

import com.fooddelivery.dto.request.RestaurantCreateRequest;
import com.fooddelivery.dto.response.RestaurantResponse;
import com.fooddelivery.mapper.RestaurantMapper;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;

    @PostMapping
    public ResponseEntity<RestaurantResponse> create(@jakarta.validation.Valid @RequestBody RestaurantCreateRequest request) {
        Restaurant r = restaurantMapper.toEntity(request);
        return new ResponseEntity<>(restaurantMapper.toResponse(restaurantService.create(r)), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantMapper.toResponse(restaurantService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> list(
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        List<Restaurant> list;
        if (search != null) list = restaurantService.search(search);
        else if (cuisine != null) list = restaurantService.getByCuisine(cuisine);
        else if (activeOnly) list = restaurantService.getActive();
        else list = restaurantService.getAll();
        
        return ResponseEntity.ok(list.stream().map(restaurantMapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<RestaurantResponse>> getByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(restaurantService.getByOwner(ownerId).stream().map(restaurantMapper::toResponse).collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponse> update(@PathVariable Long id, @jakarta.validation.Valid @RequestBody RestaurantCreateRequest request) {
        Restaurant r = restaurantMapper.toEntity(request);
        r.setId(id);
        return ResponseEntity.ok(restaurantMapper.toResponse(restaurantService.update(r)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        restaurantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
