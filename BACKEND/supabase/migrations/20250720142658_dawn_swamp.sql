-- Insert default roles
INSERT IGNORE INTO roles (id, name, description) VALUES 
(1, 'ADMIN', 'Administrator role'),
(2, 'EMPLOYEE', 'Employee role'),
(3, 'CASHIER', 'Cashier role'),
(4, 'CUSTOMER', 'Customer role');

-- Insert default admin user (password: admin123)
INSERT IGNORE INTO users (id, email, password, first_name, last_name, is_active, is_verified, created_at, updated_at) VALUES 
(1, 'admin@dpatty.com', '$2a$10$YQsXj/7RhODVJqUV8RnJHOzqJ5SZ6kO3Y7QiW8K9V9TtQKJYfQzfG', 'Admin', 'DPatty', true, true, NOW(), NOW());

-- Assign admin role to admin user
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (1, 1);

-- Insert default store
INSERT IGNORE INTO stores (id, name, address, city, state, country, phone, email, is_active, is_main, created_at, updated_at) VALUES 
(1, 'DPattyModa Pampa Hermosa', 'Av. Principal 123', 'Pampa Hermosa', 'Loreto', 'Perú', '+51 999 999 999', 'tienda@dpatty.com', true, true, NOW(), NOW());

-- Insert sample categories
INSERT IGNORE INTO categories (id, name, description, is_active, sort_order, created_at, updated_at) VALUES 
(1, 'Hombres', 'Ropa para hombres', true, 1, NOW(), NOW()),
(2, 'Mujeres', 'Ropa para mujeres', true, 2, NOW(), NOW()),
(3, 'Camisas', 'Camisas casuales y formales', true, 1, NOW(), NOW()),
(4, 'Pantalones', 'Pantalones y jeans', true, 2, NOW(), NOW()),
(5, 'Vestidos', 'Vestidos casuales y elegantes', true, 1, NOW(), NOW()),
(6, 'Blusas', 'Blusas y tops', true, 2, NOW(), NOW());

-- Set parent-child relationships for categories
UPDATE categories SET parent_id = 1 WHERE id IN (3, 4);
UPDATE categories SET parent_id = 2 WHERE id IN (5, 6);

-- Insert sample products
INSERT IGNORE INTO products (id, name, description, price, sku, is_active, is_featured, created_at, updated_at) VALUES 
(1, 'Camisa Clásica Blanca', 'Camisa de algodón 100% de corte clásico para hombre', 89.90, 'CAM-001', true, true, NOW(), NOW()),
(2, 'Jean Slim Fit Azul', 'Pantalón jean de corte moderno y cómodo', 129.90, 'JEAN-001', true, false, NOW(), NOW()),
(3, 'Vestido Floral Casual', 'Vestido ligero con estampado floral para uso diario', 99.90, 'VEST-001', true, true, NOW(), NOW()),
(4, 'Blusa Elegante Rosa', 'Blusa de chiffon para ocasiones especiales', 79.90, 'BLUS-001', true, false, NOW(), NOW());

-- Associate products with categories
INSERT IGNORE INTO product_categories (product_id, category_id) VALUES 
(1, 1), (1, 3),
(2, 1), (2, 4),
(3, 2), (3, 5),
(4, 2), (4, 6);

-- Insert product variants
INSERT IGNORE INTO product_variants (id, product_id, sku, size, color, stock_quantity, is_active, created_at, updated_at) VALUES 
-- Camisa Clásica Blanca
(1, 1, 'CAM-001-S-WHITE', 'S', 'Blanco', 10, true, NOW(), NOW()),
(2, 1, 'CAM-001-M-WHITE', 'M', 'Blanco', 15, true, NOW(), NOW()),
(3, 1, 'CAM-001-L-WHITE', 'L', 'Blanco', 12, true, NOW(), NOW()),
(4, 1, 'CAM-001-XL-WHITE', 'XL', 'Blanco', 8, true, NOW(), NOW()),

-- Jean Slim Fit Azul
(5, 2, 'JEAN-001-30-BLUE', '30', 'Azul', 5, true, NOW(), NOW()),
(6, 2, 'JEAN-001-32-BLUE', '32', 'Azul', 8, true, NOW(), NOW()),
(7, 2, 'JEAN-001-34-BLUE', '34', 'Azul', 6, true, NOW(), NOW()),
(8, 2, 'JEAN-001-36-BLUE', '36', 'Azul', 4, true, NOW(), NOW()),

-- Vestido Floral Casual
(9, 3, 'VEST-001-S-FLORAL', 'S', 'Floral', 7, true, NOW(), NOW()),
(10, 3, 'VEST-001-M-FLORAL', 'M', 'Floral', 9, true, NOW(), NOW()),
(11, 3, 'VEST-001-L-FLORAL', 'L', 'Floral', 6, true, NOW(), NOW()),

-- Blusa Elegante Rosa
(12, 4, 'BLUS-001-S-PINK', 'S', 'Rosa', 8, true, NOW(), NOW()),
(13, 4, 'BLUS-001-M-PINK', 'M', 'Rosa', 12, true, NOW(), NOW()),
(14, 4, 'BLUS-001-L-PINK', 'L', 'Rosa', 5, true, NOW(), NOW());

-- Insert sample coupons
INSERT IGNORE INTO coupons (id, code, name, description, discount_type, discount_value, minimum_amount, valid_from, valid_until, is_active, created_at, updated_at) VALUES 
(1, 'WELCOME10', 'Descuento de Bienvenida', 'Descuento del 10% para nuevos clientes', 'PERCENTAGE', 10.00, 50.00, '2024-01-01 00:00:00', '2024-12-31 23:59:59', true, NOW(), NOW()),
(2, 'SUMMER20', 'Descuento de Verano', 'Descuento del 20% en toda la tienda', 'PERCENTAGE', 20.00, 100.00, '2024-01-01 00:00:00', '2024-03-31 23:59:59', true, NOW(), NOW()),
(3, 'FIXED50', 'Descuento Fijo', '50 soles de descuento en compras mayores a 200', 'FIXED_AMOUNT', 50.00, 200.00, '2024-01-01 00:00:00', '2024-12-31 23:59:59', true, NOW(), NOW());

-- Insert system settings
INSERT IGNORE INTO settings (setting_key, setting_value, setting_type, description, is_system) VALUES 
('store_name', 'DPattyModa', 'STRING', 'Nombre de la tienda', false),
('store_currency', 'PEN', 'STRING', 'Moneda de la tienda', false),
('tax_rate', '18.0', 'NUMBER', 'Tasa de impuesto (IGV)', false),
('free_shipping_threshold', '100.0', 'NUMBER', 'Monto mínimo para envío gratis', false),
('email_notifications', 'true', 'BOOLEAN', 'Activar notificaciones por email', false),
('sunat_enabled', 'false', 'BOOLEAN', 'Activar integración con SUNAT', true),
('pos_receipt_footer', 'Gracias por su compra - DPattyModa', 'STRING', 'Pie de página en recibos POS', false);