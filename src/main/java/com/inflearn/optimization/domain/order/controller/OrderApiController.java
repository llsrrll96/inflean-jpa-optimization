package com.inflearn.optimization.domain.order.controller;

import com.inflearn.optimization.application.Result;
import com.inflearn.optimization.domain.member.entity.Address;
import com.inflearn.optimization.domain.order.dto.OrderSearch;
import com.inflearn.optimization.domain.order.entity.Order;
import com.inflearn.optimization.domain.order.entity.OrderItem;
import com.inflearn.optimization.domain.order.entity.OrderStatus;
import com.inflearn.optimization.domain.order.repository.OrderRepository;
import com.inflearn.optimization.domain.order.repository.query.OrderFlatDto;
import com.inflearn.optimization.domain.order.repository.query.OrderItemQueryDto;
import com.inflearn.optimization.domain.order.repository.query.OrderQueryDto;
import com.inflearn.optimization.domain.order.repository.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository repository;
    private final OrderQueryRepository queryRepository;

    @GetMapping("/api/v1/orders")
    public ResponseEntity<Result<List<Order>>> ordersV1() {
        List<Order> orderList = repository.findAllByString(new OrderSearch());
        for(Order order : orderList) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            // 프록시 강제초기화
            // 하이버네이트 모듈 : 프록시 강제초기화 데이터가 있으면 출력
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return ResponseEntity.ok(new Result<>(orderList));
    }

    @GetMapping("/api/v2/orders")
    public ResponseEntity<Result<List<OrderDto>>> orderV2() {
        List<Order> orders = repository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .toList();
        return ResponseEntity.ok(new Result<>(collect));
    }

    /**
     * 페치 조인
     */
    @GetMapping("/api/v3/orders")
    public ResponseEntity<Result<List<OrderDto>>> orderV3() {
        // N 만큼 데이터 나옴
        List<Order> orders = repository.findAllWithItem();

        // for(Order order : orders) System.out.println("order ref=" + order+"id="+order.getId());

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .toList();
        return ResponseEntity.ok(new Result<>(result));
    }

    @GetMapping("/api/v3-1/orders")
    public ResponseEntity<Result<List<OrderDto>>> orderV3_page(
            @RequestParam(value = "offset", defaultValue = "0") String offset,
            @RequestParam(value = "limit", defaultValue = "100") String limit
    ) {
        // N 만큼 데이터 나옴
        List<Order> orders = repository.findAllWithMemberDelivery(offset, limit); // ToOne 관계이기 때문에 페이징 가능

        // for(Order order : orders) System.out.println("order ref=" + order+"id="+order.getId());

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .toList();
        return ResponseEntity.ok(new Result<>(result));
    }

    /**
     * forEach 반복 조회
     */
    @GetMapping("/api/v4/orders")
    public ResponseEntity<Result<List<OrderQueryDto>>> orderV4() {
        List<OrderQueryDto> orderQueryDtos = queryRepository.findOrderQueryDtos();
        return ResponseEntity.ok(new Result<>(orderQueryDtos));
    }

    /**
     * IN 쿼리 사용
     */
    @GetMapping("/api/v5/orders")
    public ResponseEntity<Result<List<OrderQueryDto>>> orderV5() {
        List<OrderQueryDto> orderQueryDtos = queryRepository.findAllByDto_optimization();
        return ResponseEntity.ok(new Result<>(orderQueryDtos));
    }

    /**
     * 한번 조회
     */
    @GetMapping("/api/v6/orders")
    public ResponseEntity<Result<List<OrderQueryDto>>> orderV6() {
        List<OrderFlatDto> orderFlatDtos = queryRepository.findAllByDto_flat();
        List<OrderQueryDto> orderQueryDtos = orderFlatDtos.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());
        return ResponseEntity.ok(new Result<>(orderQueryDtos));
    }

    @Getter
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .toList();
        }
    }

    @Getter
    static class OrderItemDto {
        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getOrderPrice();
        }
    }
}
