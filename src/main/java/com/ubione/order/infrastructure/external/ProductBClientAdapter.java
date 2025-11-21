package com.ubione.order.infrastructure.external;

import com.ubione.order.domain.model.Order;
import com.ubione.order.domain.ports.SendOrderToProductBPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductBClientAdapter implements SendOrderToProductBPort {

    private static final Logger log = LoggerFactory.getLogger(ProductBClientAdapter.class);

    private final RestTemplate restTemplate;
    private final String productBUrl;

    public ProductBClientAdapter(@Value("${app.product-b.url:http://localhost:9090/product-b/orders}") String productBUrl) {
        this.productBUrl = productBUrl;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void send(Order order) {
        try {
            restTemplate.postForEntity(productBUrl, order, Void.class);
            log.info("Order {} sent to Product B", order.getExternalId());
        } catch (Exception ex) {
            log.error("Error sending order {} to Product B: {}", order.getExternalId(), ex.getMessage());
            throw ex;
        }
    }
}
