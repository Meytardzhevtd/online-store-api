CREATE TABLE
    cart_items (
        id BIGSERIAL PRIMARY KEY,
        user_id BIGINT NOT NULL,
        CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
        product_id BIGINT NOT NULL,
        CONSTRAINT fk_cart_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
        quantity INT NOT NULL DEFAULT 1,
        CONSTRAINT unique_user_product UNIQUE (user_id, product_id)
    );

CREATE INDEX idx_cart_user_id ON cart_items (user_id);