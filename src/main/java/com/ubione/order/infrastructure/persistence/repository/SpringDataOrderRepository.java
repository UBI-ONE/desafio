package com.ubione.order.infrastructure.persistence.repository;

import com.ubione.order.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataOrderRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findByExternalId(String externalId);

    boolean existsByExternalId(String externalId);
}
