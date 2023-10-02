package com.inflearn.optimization.domain.member.api;

import com.inflearn.optimization.application.Result;
import com.inflearn.optimization.domain.member.dto.MemberOrderItemDto;
import com.inflearn.optimization.domain.member.entity.Address;
import com.inflearn.optimization.domain.member.service.OrderItemService;
import com.inflearn.optimization.domain.order.OrderRepository;
import com.inflearn.optimization.domain.order.dto.OrderSearch;
import com.inflearn.optimization.domain.order.entity.Order;
import com.inflearn.optimization.domain.order.entity.OrderStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderItemApiController {
    private final OrderItemService orderItemService;

    private final OrderRepository repository;

    @PostMapping("/api/v1/myOrders")
    public ResponseEntity<Result<List<MemberOrderItemDto>>> getMyOrders(@RequestBody HashMap<String, Object> params) {
        log.info("/api/v1/myOrders start!");

        Long memberId = Long.valueOf(params.get("member_id").toString());

        List<MemberOrderItemDto> myOrderItems = orderItemService.getMyOrders(memberId);
        Result<List<MemberOrderItemDto>> result = new Result<>(myOrderItems.size(), myOrderItems);
        return ResponseEntity.ok(result);
    }

    /**
     * Order 내 Member
     * Member 내 Orders (ToMany)
     * Order 내 Member 무한루프 - 양방향 연관관계 발생 -> @JsonIgnore
     * Entity를 api로 노출한 것은 지양
     */
    @GetMapping("/api/v1/simple-orders")
    public ResponseEntity<Result> ordersV1() {
        List<Order> all = repository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // getMember 까지는 프록시객체(SQL 안날라감)
            order.getDelivery().getAddress();// getName(): Lazy 강제 초기화
        }
        Result result = new Result<>(all.size(), all);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/v2/simple-orders")
    public ResponseEntity<Result> ordersV2() {

        List<Order> orders = repository.findAllByString(new OrderSearch());

        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new Result(result.size(), result));
    }

    @GetMapping("/api/v3/simple-orders")
    public ResponseEntity<Result> ordersV3() {
        List<Order> orders = repository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .toList(); // Java 16부터 도입된 새로운 스트림 API 메서드입니다.
        return ResponseEntity.ok(new Result(result.size(), result));
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }
}
