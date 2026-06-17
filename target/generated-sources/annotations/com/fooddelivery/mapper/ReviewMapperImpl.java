package com.fooddelivery.mapper;

import com.fooddelivery.dto.request.ReviewRequest;
import com.fooddelivery.dto.response.ReviewResponse;
import com.fooddelivery.model.Review;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-17T00:37:30+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class ReviewMapperImpl implements ReviewMapper {

    @Override
    public Review toEntity(ReviewRequest request) {
        if ( request == null ) {
            return null;
        }

        Review.ReviewBuilder review = Review.builder();

        review.userId( request.getUserId() );
        review.restaurantId( request.getRestaurantId() );
        review.orderId( request.getOrderId() );
        review.rating( request.getRating() );
        review.comment( request.getComment() );

        return review.build();
    }

    @Override
    public ReviewResponse toResponse(Review review) {
        if ( review == null ) {
            return null;
        }

        ReviewResponse.ReviewResponseBuilder reviewResponse = ReviewResponse.builder();

        reviewResponse.id( review.getId() );
        reviewResponse.userId( review.getUserId() );
        reviewResponse.restaurantId( review.getRestaurantId() );
        reviewResponse.orderId( review.getOrderId() );
        reviewResponse.rating( review.getRating() );
        reviewResponse.comment( review.getComment() );
        reviewResponse.createdAt( review.getCreatedAt() );

        return reviewResponse.build();
    }
}
