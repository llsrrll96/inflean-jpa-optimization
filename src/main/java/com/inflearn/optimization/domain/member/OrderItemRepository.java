package com.inflearn.optimization.domain.member;

import com.inflearn.optimization.domain.member.dto.MemberOrderItemDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderItemRepository {
    private final EntityManager em;


    public List<MemberOrderItemDto> findMemberOrderItems(Long memberId) {
        TypedQuery<MemberOrderItemDto> query = em.createQuery("SELECT new com.inflearn.optimization.domain.member.dto.MemberOrderItemDto(oi.id, m.name, i.name, oi.orderPrice, oi.count, o.status, o.orderDate) " +
                "FROM Member m " +
                "JOIN Order o ON m.id = o.id " +
                "JOIN OrderItem oi ON oi.order.id = o.id " +
                "JOIN Item i ON i.id = oi.item.id " +
                "WHERE m.id = :member_id", MemberOrderItemDto.class);
        query.setParameter("member_id", memberId);
        return query.getResultList();
    }
}
