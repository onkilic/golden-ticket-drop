package com.goldenticket.goldenticketdrop.repository;

import com.goldenticket.goldenticketdrop.domain.product.IProductRepository;
import com.goldenticket.goldenticketdrop.domain.product.Product;
import com.goldenticket.goldenticketdrop.domain.product.ProductId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ProductInMemoryRepository implements IProductRepository {
    private final Map<Integer, Product> products = new ConcurrentHashMap<>();

    public ProductInMemoryRepository() {
        put(new Product(ProductId.of(1), "Golden Ticket (Limited Drop)", "Limited run ticket"));
        put(new Product(ProductId.of(2), "Rare Sneaker Drop", "Limited sneaker drop"));
        put(new Product(ProductId.of(3), "VIP Concert Pass", "VIP entry pass"));
    }

    private void put(Product p) { products.put(p.id().value(), p); }

    @Override
    public List<Product> list() {
        return products.values().stream().toList();
    }

    @Override
    public Optional<Product> findById(ProductId id) {
        return Optional.ofNullable(products.get(id.value()));
    }
}
