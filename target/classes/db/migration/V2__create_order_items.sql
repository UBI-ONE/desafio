CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    product_code VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(19,2) NOT NULL,
    line_total NUMERIC(19,2) NOT NULL
);
