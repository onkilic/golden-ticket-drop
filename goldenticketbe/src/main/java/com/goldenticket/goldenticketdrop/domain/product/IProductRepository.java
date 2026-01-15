package com.goldenticket.goldenticketdrop.domain.product;

import java.util.List;
import java.util.Optional;

public interface IProductRepository {
    List<Product> list();
    Optional<Product> findById(ProductId id);
}
