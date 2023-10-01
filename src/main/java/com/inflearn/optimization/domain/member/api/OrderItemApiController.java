package com.inflearn.optimization.domain.member.api;

import com.inflearn.optimization.application.Result;
import com.inflearn.optimization.domain.member.dto.MemberOrderItemDto;
import com.inflearn.optimization.domain.member.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderItemApiController {
    private final OrderItemService orderItemService;

    @PostMapping("/api/v1/myOrders")
    public ResponseEntity<Result<List<MemberOrderItemDto>>> getMyOrders(@RequestBody HashMap<String, Object> params) {
        log.info("/api/v1/myOrders start!");

        Long memberId = Long.valueOf(params.get("member_id").toString());

        List<MemberOrderItemDto> myOrderItems = orderItemService.getMyOrders(memberId);
        Result<List<MemberOrderItemDto>> result = new Result<>(myOrderItems.size(), myOrderItems);
        return ResponseEntity.ok(result);
    }
}
