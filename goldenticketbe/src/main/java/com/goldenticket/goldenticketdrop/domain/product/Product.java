package com.goldenticket.goldenticketdrop.domain.product;

public record Product(
        ProductId id,
        String name,
        String description) {
    public Product {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");
        if (description == null) description = "";
    }
}