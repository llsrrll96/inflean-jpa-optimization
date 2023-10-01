package com.inflearn.optimization.domain.member.service;

import com.inflearn.optimization.domain.member.OrderItemRepository;
import com.inflearn.optimization.domain.member.dto.MemberOrderItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    final OrderItemRepository orderItemRepository;

    public List<MemberOrderItemDto> getMyOrders(Long memberId) {
        return orderItemRepository.findMemberOrderItems(memberId);
    }
}
