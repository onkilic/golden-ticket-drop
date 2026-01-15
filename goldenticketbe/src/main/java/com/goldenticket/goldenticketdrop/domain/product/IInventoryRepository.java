package com.goldenticket.goldenticketdrop.domain.product;

import java.util.Optional;

public interface IInventoryRepository {

    Optional<Integer> getRemaining(ProductId productId);
    DecrementResult tryDecrement(ProductId productId);
}

