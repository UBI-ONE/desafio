package com.ubione.order.domain.ports;

import com.ubione.order.domain.model.Order;

public interface SendOrderToProductBPort {

    void send(Order order);
}
