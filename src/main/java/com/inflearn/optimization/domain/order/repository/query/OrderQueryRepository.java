package com.inflearn.optimization.domain.order.repository.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 화면, 특정
// 관심사 분리
@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders(); // query 1번 -> N개
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId()); // query N번 반복
            o.setOrderItems(orderItems);
        });
        return result;
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> orderDtos = findOrders();
        List<Long> orderIds = orderDtos.stream().map(o -> o.getOrderId()).collect(Collectors.toList());
        // ctrl alt v
        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new com.inflearn.optimization.domain.order.repository.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count) " +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        // 최적화 - 메모리에서 매칭해서 set
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));// id를 기준으로 map 으로
        orderDtos.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId()))); // set OrderItem
        return orderDtos;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new com.inflearn.optimization.domain.order.repository.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count) " +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<OrderQueryDto> findOrders() {
        return em.createQuery(
                "select new com.inflearn.optimization.domain.order.repository.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o " +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery(
                "select new " +
                        " com.inflearn.optimization.domain.order.repository.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" +
                        " join o.orderItems oi" +
                        " join oi.item i", OrderFlatDto.class)
                .getResultList();
    }
}
