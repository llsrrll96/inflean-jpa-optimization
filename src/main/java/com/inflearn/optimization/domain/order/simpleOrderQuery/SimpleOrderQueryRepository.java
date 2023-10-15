package com.inflearn.optimization.domain.order.simpleOrderQuery;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SimpleOrderQueryRepository {
    private final EntityManager em;

    public List<SimpleOrderQueryDto> findOrderDtos() {
        return em.createQuery(
                        "SELECT NEW com.inflearn.optimization.domain.order.simpleOrderQuery.SimpleOrderQueryDto(" +
                                "o.id, m.name, o.orderDate, o.status, d.address)" +
                                " FROM Order o" +
                                " JOIN o.member m" +
                                " JOIN o.delivery d", SimpleOrderQueryDto.class)
                .getResultList();
    }
}
