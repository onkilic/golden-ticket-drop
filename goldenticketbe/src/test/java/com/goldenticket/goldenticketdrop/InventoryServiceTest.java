package com.goldenticket.goldenticketdrop;

import com.goldenticket.goldenticketdrop.domain.product.ProductId;
import com.goldenticket.goldenticketdrop.dto.BuyResponse;
import com.goldenticket.goldenticketdrop.dto.BuyResultStatus;
import com.goldenticket.goldenticketdrop.repository.InventoryInMemoryRepository;
import com.goldenticket.goldenticketdrop.repository.ProductInMemoryRepository;
import com.goldenticket.goldenticketdrop.service.ProductService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {

    @Test
    void buyOneConcurrentRequestsNeverOversells() throws Exception {
        InventoryInMemoryRepository inventoryInMemoryRepository = new InventoryInMemoryRepository();
        inventoryInMemoryRepository.updateForTest(1,1);
        var service = new ProductService(new ProductInMemoryRepository(), inventoryInMemoryRepository);

        int threads = 20;
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);

        List<Future<BuyResponse>> futures = new ArrayList<>();
        ProductId productId = new ProductId(1);
        for (int i = 0; i < threads; i++) {
            futures.add(pool.submit(() -> {
                ready.countDown();
                start.await(2, TimeUnit.SECONDS);
                return service.buy(productId);
            }));
        }

        assertTrue(ready.await(2, TimeUnit.SECONDS));
        start.countDown();

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger soldOutCount = new AtomicInteger();

        for (Future<BuyResponse> f : futures) {
            BuyResponse r = f.get(2, TimeUnit.SECONDS);
            if (r.status() == BuyResultStatus.SUCCESS) successCount.incrementAndGet();
            if (r.status() == BuyResultStatus.SOLD_OUT) soldOutCount.incrementAndGet();
        }

        pool.shutdown();
        pool.awaitTermination(2, TimeUnit.SECONDS);

        assertEquals(1, successCount.get());
        assertEquals(threads - 1, soldOutCount.get());

        assertTrue(service.getRemaining(productId).isPresent());
        assertEquals(0, service.getRemaining(productId).get());
    }
}