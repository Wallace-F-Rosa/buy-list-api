package com.project.buylist.buylist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.buylist.ingredients.Ingredient;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "buyList" })
@ToString(exclude = "buyList")
@EqualsAndHashCode(exclude = "buyList")
public class BuyListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    private Double quantity;

    @NotNull(message = "Ingredient is required")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Ingredient ingredient;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnore
    private BuyList buyList;
}
