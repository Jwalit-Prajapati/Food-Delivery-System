package com.fooddelivery.service;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public interface AnalyticsService {
    Map<String, Object> overview();
    BigDecimal revenueToday();
}
