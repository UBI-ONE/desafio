CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    external_id VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL,
    total_amount NUMERIC(19,2) NOT NULL,
    received_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_orders_external_id UNIQUE (external_id)
);
