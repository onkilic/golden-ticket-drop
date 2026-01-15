package com.goldenticket.goldenticketdrop.domain.product;

public sealed interface DecrementResult permits DecrementResult.Success, DecrementResult.SoldOut, DecrementResult.NotFound {
    record Success(int remaining) implements DecrementResult {}
    record SoldOut(int remaining) implements DecrementResult {}
    record NotFound() implements DecrementResult {}

    static DecrementResult success(int remaining) { return new Success(remaining); }
    static DecrementResult soldOut(int remaining) { return new SoldOut(remaining); }
    static DecrementResult notFound() { return new NotFound(); }
}