package com.fooddelivery.service.impl;

import com.fooddelivery.service.AddressService;
import com.fooddelivery.repository.AddressRepository;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.model.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    /**
     * Creating an address modifies the user's address list; evict the
     * list cache so the next call returns the complete updated set.
     */
    @Override
    @Transactional
    @CacheEvict(value = "addresses", key = "'user:' + #address.userId")
    public Address create(Address address) {
        if (address.isDefault()) {
            addressRepository.clearDefaultForUser(address.getUserId());
        }
        return addressRepository.save(address);
    }

    /**
     * Cached by address ID. Loaded on every order-placement screen to
     * display the selected delivery address — caching eliminates repeated hits.
     */
    @Override
    @Cacheable(value = "addresses", key = "#id")
    public Address getById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + id));
    }

    /**
     * Cached per user. Customers view their address book before every order;
     * this list rarely changes relative to its read frequency.
     */
    @Override
    @Cacheable(value = "addresses", key = "'user:' + #userId")
    public List<Address> getByUser(Long userId) {
        return addressRepository.findByUserIdOrderByIsDefaultDescIdDesc(userId);
    }

    /**
     * @CachePut refreshes the per-ID cache entry immediately.
     * The user's address list is evicted so the updated address is visible.
     */
    @Override
    @Transactional
    @Caching(
        put = {
            @CachePut(value = "addresses", key = "#address.id")
        },
        evict = {
            @CacheEvict(value = "addresses", key = "'user:' + #address.userId")
        }
    )
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

    /**
     * On deletion, evict the per-ID entry and the full list for that user.
     * We resolve the userId by fetching the address first, then delete.
     */
    @Override
    @Transactional
    public void delete(Long id) {
        Address address = getById(id);
        addressRepository.deleteById(id);
        evictAddressCaches(id, address.getUserId());
    }

    @Caching(evict = {
        @CacheEvict(value = "addresses", key = "#id"),
        @CacheEvict(value = "addresses", key = "'user:' + #userId")
    })
    private void evictAddressCaches(Long id, Long userId) {
        // Intentionally empty — annotations drive the eviction.
    }
}
