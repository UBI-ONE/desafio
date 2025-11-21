package com.ubione.order.interfaces.web.controller;

import com.ubione.order.application.CreateOrderUseCase;
import com.ubione.order.application.GetOrderUseCase;
import com.ubione.order.interfaces.web.dto.CreateOrderRequest;
import com.ubione.order.interfaces.web.dto.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase, GetOrderUseCase getOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return createOrderUseCase.execute(request);
    }

    @GetMapping("/{id}")
    public OrderResponse byId(@PathVariable Long id) {
        return getOrderUseCase.byId(id);
    }

    @GetMapping("/external/{externalId}")
    public OrderResponse byExternal(@PathVariable String externalId) {
        return getOrderUseCase.byExternalId(externalId);
    }
}
