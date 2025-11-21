# Order Service - Desafio de Gerenciamento de Pedidos (Clean Architecture + RabbitMQ + PostgreSQL)

Base package: `com.ubione.order`

Este projeto implementa o serviço **`order`** descrito no desafio:

- Recebe pedidos de um sistema externo A (simulado via chamada HTTP ou mensagem).
- Calcula o valor total do pedido (somando itens e quantidades).
- Garante **idempotência** pelo `externalId` (não duplica pedidos).
- Persiste os pedidos em **PostgreSQL**.
- Publica evento de pedido criado em **RabbitMQ**.
- Envia o pedido processado para o sistema externo B (abstraído por uma porta, hoje implementada via HTTP, mas facilmente adaptável para outra fila RabbitMQ).

Também foram considerados:
- Volume de **150k–200k pedidos/dia**.
- Confiabilidade dos dados e concorrência.
- Organização em **Clean Architecture**, separando domínio, aplicação, infraestrutura e interfaces.

---

## Arquitetura (visão geral)

Camadas principais:

- `domain`  
  - `model`: `Order`, `OrderItem`, `OrderStatus`  
  - `ports`:  
    - `OrderRepositoryPort` – abstração do repositório de pedidos  
    - `OrderEventPublisherPort` – abstração de publicação de eventos  
    - `SendOrderToProductBPort` – abstração do envio para o sistema B

- `application`  
  - `CreateOrderUseCase` – caso de uso para criar/processar pedido  
    - Valida duplicidade (`existsByExternalId`)  
    - Calcula totals  
    - Persiste pedido  
    - Publica evento em RabbitMQ  
    - Tenta enviar para o produto B e atualiza o status (`SENT_TO_PRODUCT_B` ou `ERROR_SENDING_TO_PRODUCT_B`)  
  - `GetOrderUseCase` – consulta de pedidos por `id` ou `externalId`

- `infrastructure`  
  - `persistence` (JPA + PostgreSQL)  
    - Entities: `OrderEntity`, `OrderItemEntity`  
    - `SpringDataOrderRepository` – interface Spring Data  
    - `OrderRepositoryAdapter` – implementação de `OrderRepositoryPort`, converte entre domínio e entity  
  - `messaging` (RabbitMQ)  
    - `RabbitConfig` – declara **exchange**, **fila** e **binding**  
      - `orders.exchange`  
      - `orders.created.queue`  
      - routing key `orders.created`  
    - `RabbitOrderEventPublisher` – implementação de `OrderEventPublisherPort`, publica o `externalId` do pedido  
  - `external`  
    - `ProductBClientAdapter` – implementação de `SendOrderToProductBPort` usando `RestTemplate` + URL configurável

- `interfaces/web`  
  - `dto`: `CreateOrderRequest`, `OrderResponse`  
  - `mapper`: `OrderMapper` – converte domínio ↔ DTO  
  - `controller`: `OrderController` – endpoints REST

---

## Modelagem de dados (PostgreSQL)

Migrations Flyway:

- `V1__create_orders.sql`
  - Tabela `orders` com:
    - `id` (BIGSERIAL, PK)
    - `external_id` (UK, para idempotência)
    - `status` (status do pedido)
    - `total_amount`
    - `received_at` (timestamp com timezone)

- `V2__create_order_items.sql`
  - Tabela `order_items` com:
    - `id` (BIGSERIAL, PK)
    - `order_id` (FK para `orders`)
    - `product_code`, `description`
    - `quantity`, `unit_price`, `line_total`

O índice único em `external_id` ajuda a garantir consistência e evitar duplicidade mesmo sob alta concorrência.

---

## Integração com RabbitMQ

Arquivo: `infrastructure/messaging/RabbitConfig.java`

- Declara:
  - `TopicExchange orders.exchange`
  - `Queue orders.created.queue`
  - `Binding` com routing key `orders.created`

Arquivo: `infrastructure/messaging/RabbitOrderEventPublisher.java`

- Implementa `OrderEventPublisherPort`.
- Usa `RabbitTemplate.convertAndSend(ORDER_EXCHANGE, ORDER_ROUTING_KEY, externalId)`.

**Uso prático no caso de uso:**

`CreateOrderUseCase` chama `eventPublisher.publishOrderCreated(saved);` logo após a persistência do pedido.

Isso permite que outros serviços (monitoramento, faturamento, analytics, etc.) consumam eventos de novos pedidos sem acoplar diretamente ao serviço `order`.

---

## Considerações de volume e concorrência

- Idempotência por `externalId` + constraint única em banco.
- Acesso ao banco via pool Hikari configurado em `application.yaml`:
  - `maximum-pool-size: 20`
  - `minimum-idle: 5`
- Processamento de pedido é transacional (`@Transactional`).
- Publicação em RabbitMQ e envio para o sistema B são feitos após persistência.
- Em caso de erro de integração com B, o status do pedido é atualizado para `ERROR_SENDING_TO_PRODUCT_B`, permitindo reprocesso futuro.

Para um sistema real com 150k–200k pedidos/dia, bastaria:
- Escalonar horizontalmente a aplicação (várias instâncias Spring Boot).
- Ajustar pool de conexões, sizing do PostgreSQL e tuning do RabbitMQ.
- Implementar filas específicas para reprocesso de erros.

---

## Como rodar

Pré-requisitos:
- Docker e Docker Compose
- JDK 21
- Maven 3.9+

### 1. Subir infraestrutura (PostgreSQL + RabbitMQ)

Na raiz do projeto:

```bash
docker compose up -d
```

Isso sobe:
- PostgreSQL na porta `5432`  
  - DB: `orders`  
  - user: `order_user`  
  - password: `order_pass`
- RabbitMQ na porta `5672` (AMQP) e `15672` (management UI – usuário/pwd `guest`/`guest`).

### 2. Rodar a aplicação

```bash
./mvnw spring-boot:run
```

ou, se preferir, usando Maven instalado:

```bash
mvn spring-boot:run
```

A API sobe em: `http://localhost:8080`.

### 3. Criar um pedido (simulando Produto Externo A)

`POST http://localhost:8080/api/orders`

Body exemplo:

```json
{
  "externalId": "ORDER-123",
  "items": [
    { "productCode": "BEER-001", "description": "Beer 600ml", "quantity": 2, "unitPrice": 12.90 },
    { "productCode": "SNACK-010", "description": "Snacks", "quantity": 1, "unitPrice": 8.50 }
  ]
}
```

Resposta (simplificada):

```json
{
  "id": 1,
  "externalId": "ORDER-123",
  "status": "PROCESSED",
  "totalAmount": 34.30,
  "receivedAt": "2024-11-19T21:00:00Z",
  "items": [
    { "productCode": "BEER-001", "description": "Beer 600ml", "quantity": 2, "unitPrice": 12.90, "lineTotal": 25.80 },
    { "productCode": "SNACK-010", "description": "Snacks", "quantity": 1, "unitPrice": 8.50, "lineTotal": 8.50 }
  ]
}
```

Se você enviar o mesmo `externalId` novamente, o serviço retorna o pedido já existente, evitando duplicidade.

### 4. Consultar pedido (para simular acesso do Produto Externo B)

- Por ID interno:

  `GET http://localhost:8080/api/orders/1`

- Por `externalId`:

  `GET http://localhost:8080/api/orders/external/ORDER-123`

---

## Como o projeto atende ao desafio

- **Banco:** PostgreSQL com migrations Flyway.
- **Comunicação:** RabbitMQ para eventos de pedido criado, facilmente extensível para consumir pedidos de A e enviar para B via filas.
- **Clean Architecture:** separação clara entre domínio, aplicação, infraestrutura e interfaces.
- **Verificação de duplicidade:** `existsByExternalId` + constraint única em `orders.external_id`.
- **Escalabilidade:** uso de fila para desacoplar integração, idempotência, e possibilidade de escalar horizontalmente o serviço.
- **Código em português onde faz sentido (README, comentários, explicação), mantendo nomes de classes em inglês, como é comum em projetos Java corporativos.**

Você pode usar este README e o desenho da arquitetura desse projeto para apresentar na entrevista, explicando como cada parte atende aos requisitos do PNG do desafio.
