package com.inflearn.optimization.domain.item.items.entity;

import com.inflearn.optimization.domain.item.Item;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("M")
@Getter
@Setter
public class Movie  extends Item {
    private String director;
    private String actor;
}
