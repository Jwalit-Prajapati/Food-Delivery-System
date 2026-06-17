package com.fooddelivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import com.fooddelivery.dto.request.ReviewRequest;
import com.fooddelivery.dto.response.ReviewResponse;
import com.fooddelivery.model.Review;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Review toEntity(ReviewRequest request);

    ReviewResponse toResponse(Review review);
}
