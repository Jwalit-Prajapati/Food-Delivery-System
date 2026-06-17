package com.fooddelivery.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {
    private Long id;
    private Long userId;
    private Long restaurantId;
    private Long orderId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
