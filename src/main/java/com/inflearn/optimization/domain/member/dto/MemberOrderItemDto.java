package com.inflearn.optimization.domain.member.dto;

import com.inflearn.optimization.domain.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberOrderItemDto {
    private Long orderItemId;
    private String userName;
    private String itemName;
    private int orderPrice;
    private int count;
    private OrderStatus status;
    private LocalDateTime orderDate;
}
