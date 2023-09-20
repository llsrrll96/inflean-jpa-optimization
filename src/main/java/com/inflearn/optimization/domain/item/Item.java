package com.inflearn.optimization.domain.item;

import com.inflearn.optimization.domain.item.category.entity.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
// 상속 관계에 있는 엔티티 클래스들을 하나의 테이블에 매핑하는 전략 지정
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// 이 칼럼을 통해 어떤 하위 클래스의 데이터인지 구분
@DiscriminatorColumn(name ="dtype")
@Getter
@Setter
public abstract class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();
}
