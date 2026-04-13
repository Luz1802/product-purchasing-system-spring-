-- ============================================
-- Product Purchasing System - Schema DDL
-- ============================================
--
-- Este script crea la estructura básica de la base de datos.
--
-- NOTA: Con hibernate.hbm2ddl.auto=update, Hibernate creará
-- las tablas automáticamente. Este script es de referencia
-- y para ambientes donde se use DDL manual.
--
-- Para ejecutar (como root):
-- mysql -u root -p < schema.sql
-- (Se solicitará la contraseña de root)
-- ============================================

-- ============================================
-- Crear base de datos si no existe
-- ============================================
CREATE DATABASE IF NOT EXISTS pps_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- ============================================
-- Crear usuario de aplicación (no usar root)
-- ============================================
-- IMPORTANTE: Reemplazar 'YOUR_SECURE_PASSWORD' con una contraseña segura
-- que cumpla con la política de seguridad de MySQL:
-- - Mínimo 8 caracteres
-- - Al menos una mayúscula
-- - Al menos una minúscula
-- - Al menos un número
-- - Al menos un carácter especial (@, !, #, $, etc.)

-- Eliminar usuario si existe (para re-ejecución del script)
DROP USER IF EXISTS 'user_pps'@'localhost';

-- Crear usuario con contraseña segura
-- NOTA: Cambiar 'YOUR_SECURE_PASSWORD' por la contraseña real
CREATE USER 'user_pps'@'localhost' IDENTIFIED WITH mysql_native_password BY 'YOUR_SECURE_PASSWORD';

-- Otorgar todos los privilegios sobre la base de datos pps_db
GRANT ALL PRIVILEGES ON pps_db.* TO 'user_pps'@'localhost' WITH GRANT OPTION;

-- Aplicar cambios de privilegios
FLUSH PRIVILEGES;

-- ============================================
-- Usar la base de datos
-- ============================================
USE pps_db;

-- ============================================
-- Tabla: roles
-- ============================================
CREATE TABLE IF NOT EXISTS roles (
                                     role_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(50) NOT NULL UNIQUE,
                                     description VARCHAR(255),
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     INDEX idx_role_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Tabla: users
-- ============================================
CREATE TABLE IF NOT EXISTS users (
                                     user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     role_id BIGINT NOT NULL,
                                     email VARCHAR(255) NOT NULL UNIQUE,
                                     password_hash VARCHAR(255) NOT NULL,
                                     first_name VARCHAR(100) NOT NULL,
                                     last_name VARCHAR(100) NOT NULL,
                                     phone VARCHAR(20),
                                     status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') DEFAULT 'ACTIVE',
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     FOREIGN KEY (role_id) REFERENCES roles(role_id),
                                     INDEX idx_user_email (email),
                                     INDEX idx_user_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Tabla: addresses
-- ============================================
CREATE TABLE IF NOT EXISTS addresses (
                                         address_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         user_id BIGINT NOT NULL,
                                         type ENUM('SHIPPING', 'BILLING') NOT NULL,
                                         line1 VARCHAR(255) NOT NULL,
                                         line2 VARCHAR(255),
                                         city VARCHAR(100) NOT NULL,
                                         state VARCHAR(100) NOT NULL,
                                         country VARCHAR(100) NOT NULL,
                                         postal_code VARCHAR(20) NOT NULL,
                                         is_default BOOLEAN DEFAULT FALSE,
                                         FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                         INDEX idx_address_user (user_id),
                                         INDEX idx_address_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Tabla: categories
-- ============================================
CREATE TABLE IF NOT EXISTS categories (
                                          category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          parent_id BIGINT,
                                          name VARCHAR(100) NOT NULL,
                                          slug VARCHAR(100) NOT NULL UNIQUE,
                                          FOREIGN KEY (parent_id) REFERENCES categories(category_id) ON DELETE SET NULL,
                                          INDEX idx_category_slug (slug),
                                          INDEX idx_category_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Tabla: products
-- ============================================
CREATE TABLE IF NOT EXISTS products (
                                        product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        category_id BIGINT NOT NULL,
                                        sku VARCHAR(50) NOT NULL UNIQUE,
                                        name VARCHAR(255) NOT NULL,
                                        description TEXT,
                                        image VARCHAR(1000),
                                        price DECIMAL(10, 2) NOT NULL,
                                        stock_qty INT NOT NULL DEFAULT 0,
                                        is_active BOOLEAN DEFAULT TRUE,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        FOREIGN KEY (category_id) REFERENCES categories(category_id),
                                        INDEX idx_product_sku (sku),
                                        INDEX idx_product_category (category_id),
                                        INDEX idx_product_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Tabla: user_sessions
-- ============================================
CREATE TABLE IF NOT EXISTS user_sessions (
                                             session_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             user_id BIGINT,
                                             session_token VARCHAR(255) NOT NULL UNIQUE,
                                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                             expires_at TIMESTAMP NOT NULL,
                                             FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                             INDEX idx_session_token (session_token),
                                             INDEX idx_session_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Tabla: carts
-- ============================================
CREATE TABLE IF NOT EXISTS carts (
                                     cart_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     user_id BIGINT,
                                     session_id BIGINT,
                                     status ENUM('OPEN', 'CONVERTED', 'ABANDONED') DEFAULT 'OPEN',
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
                                     FOREIGN KEY (session_id) REFERENCES user_sessions(session_id) ON DELETE SET NULL,
                                     INDEX idx_cart_user (user_id),
                                     INDEX idx_cart_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Tabla: cart_items
-- ============================================
CREATE TABLE IF NOT EXISTS cart_items (
                                          cart_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          cart_id BIGINT NOT NULL,
                                          product_id BIGINT NOT NULL,
                                          quantity INT NOT NULL DEFAULT 1,
                                          unit_price DECIMAL(10, 2) NOT NULL,
                                          added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          FOREIGN KEY (cart_id) REFERENCES carts(cart_id) ON DELETE CASCADE,
                                          FOREIGN KEY (product_id) REFERENCES products(product_id),
                                          INDEX idx_cart_item_cart (cart_id),
                                          INDEX idx_cart_item_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Tabla: order_statuses
-- ============================================
CREATE TABLE IF NOT EXISTS order_statuses (
                                              order_status_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              name VARCHAR(50) NOT NULL UNIQUE,
                                              description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Tabla: orders
-- ============================================
CREATE TABLE IF NOT EXISTS orders (
                                      order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      order_number VARCHAR(50) NOT NULL UNIQUE,
                                      user_id BIGINT NOT NULL,
                                      order_status_id BIGINT NOT NULL,
                                      shipping_address_id BIGINT NOT NULL,
                                      billing_address_id BIGINT NOT NULL,
                                      subtotal DECIMAL(10, 2) NOT NULL,
                                      tax DECIMAL(10, 2) NOT NULL,
                                      shipping_cost DECIMAL(10, 2) NOT NULL,
                                      total DECIMAL(10, 2) NOT NULL,
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      FOREIGN KEY (user_id) REFERENCES users(user_id),
                                      FOREIGN KEY (order_status_id) REFERENCES order_statuses(order_status_id),
                                      FOREIGN KEY (shipping_address_id) REFERENCES addresses(address_id),
                                      FOREIGN KEY (billing_address_id) REFERENCES addresses(address_id),
                                      INDEX idx_order_number (order_number),
                                      INDEX idx_order_user (user_id),
                                      INDEX idx_order_status (order_status_id),
                                      INDEX idx_order_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Tabla: order_items
-- ============================================
CREATE TABLE IF NOT EXISTS order_items (
                                           order_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           order_id BIGINT NOT NULL,
                                           product_id BIGINT NOT NULL,
                                           quantity INT NOT NULL,
                                           unit_price DECIMAL(10, 2) NOT NULL,
                                           line_total DECIMAL(10, 2) NOT NULL,
                                           FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
                                           FOREIGN KEY (product_id) REFERENCES products(product_id),
                                           INDEX idx_order_item_order (order_id),
                                           INDEX idx_order_item_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Tabla: payment_methods
-- ============================================
CREATE TABLE IF NOT EXISTS payment_methods (
                                               payment_method_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                               name VARCHAR(50) NOT NULL UNIQUE,
                                               description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Tabla: payment_statuses
-- ============================================
CREATE TABLE IF NOT EXISTS payment_statuses (
                                                payment_status_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                name VARCHAR(50) NOT NULL UNIQUE,
                                                description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Tabla: payments
-- ============================================
CREATE TABLE IF NOT EXISTS payments (
                                        payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        order_id BIGINT NOT NULL,
                                        payment_method_id BIGINT NOT NULL,
                                        payment_status_id BIGINT NOT NULL,
                                        amount DECIMAL(10, 2) NOT NULL,
                                        currency ENUM('USD', 'COP', 'EUR') DEFAULT 'USD',
                                        transaction_id VARCHAR(255),
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
                                        FOREIGN KEY (payment_method_id) REFERENCES payment_methods(payment_method_id),
                                        FOREIGN KEY (payment_status_id) REFERENCES payment_statuses(payment_status_id),
                                        INDEX idx_payment_order (order_id),
                                        INDEX idx_payment_transaction (transaction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;