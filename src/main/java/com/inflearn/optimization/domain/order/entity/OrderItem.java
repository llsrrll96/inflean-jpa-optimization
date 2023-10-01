package com.inflearn.optimization.domain.order.entity;

import com.inflearn.optimization.domain.item.Item;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_item")
@Getter
@Setter
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private int orderPrice;

    @Column(nullable = false)
    private int count;

    /**
     * 생성 메소드
     */
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) throws Exception {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    public void cancel() {
        getItem().addStock(count); // 주문양만큼 되돌리기
    }

    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
