package com.fooddelivery.service.impl;

import com.fooddelivery.service.*;

import com.fooddelivery.service.AddressService;

import com.fooddelivery.dao.AddressRepository;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.model.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public Address create(Address address) {
        if (address.isDefault()) {
            addressRepository.clearDefaultForUser(address.getUserId());
        }
        return addressRepository.save(address);
    }

    @Override
    public Address getById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + id));
    }

    @Override
    public List<Address> getByUser(Long userId) {
        return addressRepository.findByUserIdOrderByIsDefaultDescIdDesc(userId);
    }

    @Override
    @Transactional
    public Address update(Address address) {
        Address existingAddress = getById(address.getId());
        if (address.isDefault()) {
            addressRepository.clearDefaultForUser(address.getUserId());
        }
        existingAddress.setStreet(address.getStreet());
        existingAddress.setCity(address.getCity());
        existingAddress.setState(address.getState());
        existingAddress.setZipCode(address.getZipCode());
        existingAddress.setCountry(address.getCountry());
        existingAddress.setLandmark(address.getLandmark());
        existingAddress.setDefault(address.isDefault());
        return addressRepository.save(existingAddress);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getById(id);
        addressRepository.deleteById(id);
    }
}
