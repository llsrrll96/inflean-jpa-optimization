package com.inflearn.optimization.domain.item.items.entity;

import com.inflearn.optimization.domain.item.Item;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("B")
@Getter
@Setter
public class Book extends Item {
    private String autor;
    private String isbn;
}
