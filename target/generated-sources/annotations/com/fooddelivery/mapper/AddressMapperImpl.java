package com.fooddelivery.mapper;

import com.fooddelivery.dto.request.AddressRequest;
import com.fooddelivery.dto.response.AddressResponse;
import com.fooddelivery.model.Address;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-17T00:37:30+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class AddressMapperImpl implements AddressMapper {

    @Override
    public Address toEntity(AddressRequest request) {
        if ( request == null ) {
            return null;
        }

        Address.AddressBuilder address = Address.builder();

        address.userId( request.getUserId() );
        address.street( request.getStreet() );
        address.city( request.getCity() );
        address.state( request.getState() );
        address.zipCode( request.getZipCode() );
        address.country( request.getCountry() );
        address.landmark( request.getLandmark() );

        return address.build();
    }

    @Override
    public AddressResponse toResponse(Address address) {
        if ( address == null ) {
            return null;
        }

        AddressResponse.AddressResponseBuilder addressResponse = AddressResponse.builder();

        addressResponse.id( address.getId() );
        addressResponse.userId( address.getUserId() );
        addressResponse.street( address.getStreet() );
        addressResponse.city( address.getCity() );
        addressResponse.state( address.getState() );
        addressResponse.zipCode( address.getZipCode() );
        addressResponse.country( address.getCountry() );
        addressResponse.landmark( address.getLandmark() );

        return addressResponse.build();
    }
}
