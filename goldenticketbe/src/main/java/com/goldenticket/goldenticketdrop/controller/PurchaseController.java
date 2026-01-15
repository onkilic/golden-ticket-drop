package com.goldenticket.goldenticketdrop.controller;

import com.goldenticket.goldenticketdrop.domain.product.ProductId;
import com.goldenticket.goldenticketdrop.dto.BuyRequest;
import com.goldenticket.goldenticketdrop.dto.BuyResponse;
import com.goldenticket.goldenticketdrop.dto.BuyResultStatus;
import com.goldenticket.goldenticketdrop.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/buy")
@Slf4j
public class PurchaseController {

    private final ProductService productService;

    public PurchaseController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<BuyResponse> buy(@RequestBody BuyRequest request) {
        System.out.println("Buy request received: {}"+request);
        if (request.quantity() != 1) {
            return ResponseEntity.badRequest().body(new BuyResponse(BuyResultStatus.INVALID_QUANTITY, 0));
        }

        BuyResponse response = productService.buy(ProductId.of(request.productId()));

        if (response.status() == BuyResultStatus.SUCCESS) {
            return ResponseEntity.ok(response);
        }
        if (response.status() == BuyResultStatus.SOLD_OUT) {
            return ResponseEntity.status(409).body(response);
        }
        return ResponseEntity.status(404).body(response);
    }
}
