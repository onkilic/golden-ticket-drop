package com.goldenticket.goldenticketdrop.controller;

import com.goldenticket.goldenticketdrop.domain.product.ProductId;
import com.goldenticket.goldenticketdrop.dto.InventoryResponse;
import com.goldenticket.goldenticketdrop.dto.ProductResponse;
import com.goldenticket.goldenticketdrop.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductResponse> getProducts() {
        return service.listProducts().stream()
                .map(p -> new ProductResponse(
                        p.id().value(),
                        p.name(),
                        p.description()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> get(@PathVariable int id) {
        System.out.println("get product request received.Product id:"+ id);
        return service.getProduct(ProductId.of(id))
                .map(p -> ResponseEntity.ok(new ProductResponse(
                        p.id().value(), p.name(), p.description()
                )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/inventory")
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable int id) {
        System.out.println("get inventory request received.Product id:"+id);
        var pid = ProductId.of(id);
        var remainingOpt = service.getRemaining(pid);
        return remainingOpt.map(integer ->
                ResponseEntity.ok(new InventoryResponse(pid.value(), integer)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
