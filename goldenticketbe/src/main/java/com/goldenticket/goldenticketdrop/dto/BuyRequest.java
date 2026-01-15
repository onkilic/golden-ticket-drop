package com.goldenticket.goldenticketdrop.dto;

public record BuyRequest(
        int productId,
        int quantity
) {}