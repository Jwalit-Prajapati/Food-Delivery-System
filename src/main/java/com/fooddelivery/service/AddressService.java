package com.fooddelivery.service;

import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.model.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface AddressService {
    Address create(Address address);
    Address getById(Long id);
    List<Address> getByUser(Long userId);
    Address update(Address address);
    void delete(Long id);
}
