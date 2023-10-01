package com.inflearn.optimization;

import com.inflearn.optimization.domain.delivery.entity.Delivery;
import com.inflearn.optimization.domain.item.items.entity.Book;
import com.inflearn.optimization.domain.member.entity.Address;
import com.inflearn.optimization.domain.member.entity.Member;
import com.inflearn.optimization.domain.order.entity.Order;
import com.inflearn.optimization.domain.order.entity.OrderItem;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitDb {
    private final InitService initService;

    @PostConstruct // DI 이후 실행
    public void init() throws Exception {
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;

        public void dbInit1() throws Exception {
            Member member = createMember("userA", "서울", "1", "15352");
            em.persist(member);

            Book book1 = createBook("JPA1 BOOK", 10000, 100);
            em.persist(book1);
            Book book2 = createBook("JPA2 BOOK", 20000, 100);
            em.persist(book2);

            // 주문 아이템
            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 100);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000,2 );

            // 배달
            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }
        public void dbInit2() throws Exception {
            Member member = createMember("userB", "진주", "2", "2222");
            em.persist(member);

            Book book1 = createBook("SPRING1 BOOK", 20000, 200);
            em.persist(book1);

            Book book2 = createBook("SPRING2 BOOK", 40000, 300);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        private Member createMember(String name, String city, String street, String zipcode) {
            return Member.builder()
                    .name(name)
                    .address(new Address(city, street, zipcode))
                    .build();
        }

        private Book createBook(String name, int price, int stockQuantity) {
            Book book1 = new Book();
            book1.setName(name);
            book1.setPrice(price);
            book1.setStockQuantity(stockQuantity);
            return book1;
        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }
    }
}
