package com.inflearn.optimization.domain.order.repository;


import com.inflearn.optimization.domain.order.dto.OrderSearch;
import com.inflearn.optimization.domain.order.entity.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 순수한 엔티티 조회 용도
 */
@Repository
public class OrderRepository {

    private final EntityManager em;

    public OrderRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAll() {
        return em.createQuery("select o from Order o", Order.class)
                .getResultList();
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {

        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            jpql += " where";
            isFirstCondition = false;
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    /**
     * JPA Criteria
     */
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "SELECT o from Order o " +
                        " JOIN FETCH o.member m" +
                        " JOIN FETCH o.delivery d", Order.class)
                .getResultList();
    }

    public List<Order> findAllWithItem() {
       return em.createQuery(
               "SELECT distinct o from Order o " + //  distinct : 엔티티 중복시 엔티티를 걸러서 컬렉션에 담아준다.
                       " JOIN FETCH o.member m" +          // ToOne (페치조인)
                       " JOIN FETCH o.delivery d" +        // ToOne (페치조인)
                       " JOIN FETCH o.orderItems oi" +     // ToMany
                       " JOIN FETCH oi.item i", Order.class)
               .getResultList();
    }

    public List<Order> findAllWithMemberDelivery(String offset, String limit) {
        return em.createQuery(
                        "SELECT o FROM Order o" +
                                " JOIN FETCH o.member m" +
                                " JOIN FETCH o.delivery d", Order.class)
                .setFirstResult(Integer.parseInt(offset))
                .setMaxResults(Integer.parseInt(limit))
                .getResultList();
                // SELECT o FROM Order o
    }
}


