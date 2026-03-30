-- Remova o DELETE se não houver necessidade, pois o banco :mem: inicia vazio.
-- Se mantiver, garanta que a tabela já existe (o defer-initialization cuida disso).
DELETE FROM products;

-- Inserindo sem a coluna ID (o H2 gera o 1, 2, 3... automaticamente)
INSERT INTO products (name, description, price, quantity, category_type, created_at)
VALUES ('Logitech G Pro Mouse', 'High-performance wireless gaming mouse', 599.90, 15, 'PHYSICAL', CURRENT_TIMESTAMP);

INSERT INTO products (name, description, price, quantity, category_type, created_at)
VALUES ('UltraWide Monitor 34"', 'Professional 34-inch Curved UltraWide Gaming Monitor with 144Hz refresh rate, HDR10 support and IPS panel technology', 2499.00, 8, 'PHYSICAL', CURRENT_TIMESTAMP);

INSERT INTO products (name, description, price, quantity, category_type, created_at)
VALUES ('Mechanical Keyboard K95', 'RGB Mechanical keyboard with Cherry MX switches', 899.00, 4, 'PHYSICAL', CURRENT_TIMESTAMP);

INSERT INTO products (name, description, price, quantity, category_type, created_at)
VALUES ('Clean Code E-book', 'A Handbook of Agile Software Craftsmanship', 45.00, 0, 'DIGITAL', CURRENT_TIMESTAMP);

INSERT INTO products (name, description, price, quantity, category_type, created_at)
VALUES ('Windows 11 Pro Key', 'Genuine activation key for Windows 11 Professional', 120.00, 0, 'DIGITAL', CURRENT_TIMESTAMP);

INSERT INTO products (name, description, price, quantity, category_type, created_at)
VALUES ('Spring Boot Course', 'Complete masterclass from zero to hero', 199.90, 0, 'DIGITAL', CURRENT_TIMESTAMP);

