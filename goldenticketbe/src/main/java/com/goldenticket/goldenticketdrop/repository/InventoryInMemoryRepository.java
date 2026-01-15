package com.goldenticket.goldenticketdrop.repository;

import com.goldenticket.goldenticketdrop.domain.product.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InventoryInMemoryRepository implements IInventoryRepository {
    private final ConcurrentHashMap<Integer, AtomicInteger> stock = new ConcurrentHashMap<>();

    public InventoryInMemoryRepository() {
        stock.put(1, new AtomicInteger(100));
    }

    @Override
    public Optional<Integer> getRemaining(ProductId productId) {
        AtomicInteger remaining = stock.get(productId.value());
        return remaining == null ? Optional.empty() : Optional.of(remaining.get());
    }

    @Override
    public DecrementResult tryDecrement(ProductId productId) {
        AtomicInteger remaining = stock.get(productId.value());
        if (remaining == null) {
            return DecrementResult.notFound();
        }

        int previous = remaining.getAndUpdate(current ->
                current > 0 ? current - 1 : current
        );

        if (previous <= 0) {
            return DecrementResult.soldOut(0);
        }
        return DecrementResult.success(previous - 1);
    }

    public void updateForTest(int productId, int initialStock) {
        stock.put(productId, new AtomicInteger(initialStock));
    }
}
