package com.fooddelivery.controller;

import com.fooddelivery.dto.response.AddressResponse;
import com.fooddelivery.mapper.AddressMapper;
import com.fooddelivery.model.Address;
import com.fooddelivery.dto.request.AddressRequest;
import com.fooddelivery.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;
    private final AddressMapper addressMapper;

    @PostMapping
    public ResponseEntity<AddressResponse> create(@jakarta.validation.Valid @RequestBody AddressRequest request) {
        Address address = addressMapper.toEntity(request);
        return new ResponseEntity<>(addressMapper.toResponse(addressService.create(address)), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(addressMapper.toResponse(addressService.getById(id)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AddressResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getByUser(userId).stream().map(addressMapper::toResponse).collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> update(@PathVariable Long id, @jakarta.validation.Valid @RequestBody AddressRequest request) {
        Address address = addressMapper.toEntity(request);
        address.setId(id);
        return ResponseEntity.ok(addressMapper.toResponse(addressService.update(address)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
