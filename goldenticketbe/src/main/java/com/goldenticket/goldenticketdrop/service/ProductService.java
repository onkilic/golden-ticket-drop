package com.goldenticket.goldenticketdrop.service;

import com.goldenticket.goldenticketdrop.domain.product.*;
import com.goldenticket.goldenticketdrop.domain.product.IInventoryRepository;
import com.goldenticket.goldenticketdrop.dto.BuyResponse;
import com.goldenticket.goldenticketdrop.dto.BuyResultStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final IProductRepository productRepository;
    private final IInventoryRepository inventoryRepository;

    public ProductService(IProductRepository productRepository, IInventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public List<Product> listProducts() {
        return productRepository.list();
    }

    public Optional<Product> getProduct(ProductId id) {
        return productRepository.findById(id);
    }

    public Optional<Integer> getRemaining(ProductId id) {
        if (productRepository.findById(id).isEmpty()) return Optional.empty();
        return inventoryRepository.getRemaining(id);
    }

    public BuyResponse buy(ProductId id) {
        if (productRepository.findById(id).isEmpty()) return new BuyResponse(BuyResultStatus.NOT_FOUND, 0);

        DecrementResult decrementResult = inventoryRepository.tryDecrement(id);

        if (decrementResult instanceof DecrementResult.Success success) {
            return new BuyResponse(BuyResultStatus.SUCCESS, success.remaining());
        }
        if (decrementResult instanceof DecrementResult.SoldOut soldOut) {
            return new BuyResponse(BuyResultStatus.SOLD_OUT, soldOut.remaining());
        }
        return new BuyResponse(BuyResultStatus.SOLD_OUT, 0);
    }
}
