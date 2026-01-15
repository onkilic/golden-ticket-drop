package com.goldenticket.goldenticketdrop.domain.product;

public record ProductId(int value) {
    public static ProductId of(int value) {
        if (value <= 0) throw new IllegalArgumentException("productId must be positive");
        return new ProductId(value);
    }
}
