package com.ubione.order.infrastructure.messaging;

import com.ubione.order.domain.model.Order;
import com.ubione.order.domain.ports.OrderEventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitOrderEventPublisher implements OrderEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitOrderEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public RabbitOrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishOrderCreated(Order order) {
        log.info("Publishing order created event for order {}", order.getExternalId());
        rabbitTemplate.convertAndSend(
                RabbitConfig.ORDER_EXCHANGE,
                RabbitConfig.ORDER_ROUTING_KEY,
                order.getExternalId()
        );
    }
}
