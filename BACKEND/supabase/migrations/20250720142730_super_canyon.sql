@@ .. @@
 -- Gift cards
 CREATE TABLE gift_cards (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     code VARCHAR(50) UNIQUE NOT NULL,
     initial_amount DECIMAL(10,2) NOT NULL,
     current_balance DECIMAL(10,2) NOT NULL,
+    purchased_by BIGINT,
     is_active BOOLEAN DEFAULT TRUE,
     expires_at DATETIME,
     created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
     updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     
+    FOREIGN KEY (purchased_by) REFERENCES users(id) ON DELETE SET NULL,
     INDEX idx_code (code),
     INDEX idx_active (is_active),
     INDEX idx_expires_at (expires_at)
 );
 
+-- Shopping carts
+CREATE TABLE carts (
+    id BIGINT AUTO_INCREMENT PRIMARY KEY,
+    user_id BIGINT,
+    session_id VARCHAR(100),
+    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
+    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
+    
+    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
+    INDEX idx_user_id (user_id),
+    INDEX idx_session_id (session_id)
+);
+
+-- Cart items
+CREATE TABLE cart_items (
+    id BIGINT AUTO_INCREMENT PRIMARY KEY,
+    cart_id BIGINT NOT NULL,
+    product_variant_id BIGINT NOT NULL,
+    quantity INT NOT NULL,
+    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
+    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
+    
+    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
+    FOREIGN KEY (product_variant_id) REFERENCES product_variants(id),
+    INDEX idx_cart_id (cart_id),
+    INDEX idx_variant_id (product_variant_id)
+);
+
 -- =====================================================
 -- CUSTOMER ENGAGEMENT TABLES
 -- =====================================================