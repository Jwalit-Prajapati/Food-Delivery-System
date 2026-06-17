-- =====================================================
-- Food Delivery System - MySQL Schema
-- =====================================================

DROP DATABASE IF EXISTS food_delivery_db;
CREATE DATABASE food_delivery_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE food_delivery_db;

-- =====================================================
-- USERS
-- =====================================================
CREATE TABLE users (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    email           VARCHAR(150) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    phone           VARCHAR(20),
    role            ENUM('CUSTOMER','RESTAURANT_OWNER','DELIVERY_PARTNER','ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role)
);

-- =====================================================
-- ADDRESSES
-- =====================================================
CREATE TABLE addresses (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    street          VARCHAR(255) NOT NULL,
    city            VARCHAR(100) NOT NULL,
    state           VARCHAR(100) NOT NULL,
    zip_code        VARCHAR(20) NOT NULL,
    country         VARCHAR(100) NOT NULL DEFAULT 'India',
    landmark        VARCHAR(255),
    is_default      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_address_user (user_id)
);

-- =====================================================
-- RESTAURANTS
-- =====================================================
CREATE TABLE restaurants (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(150) NOT NULL,
    description     TEXT,
    owner_id        BIGINT NOT NULL,
    address_id      BIGINT NOT NULL,
    phone           VARCHAR(20),
    cuisine_type    VARCHAR(100),
    rating          DECIMAL(3,2) DEFAULT 0.00,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    is_verified     BOOLEAN NOT NULL DEFAULT FALSE,
    opens_at        TIME,
    closes_at       TIME,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(id),
    FOREIGN KEY (address_id) REFERENCES addresses(id),
    INDEX idx_restaurant_name (name),
    INDEX idx_restaurant_cuisine (cuisine_type),
    INDEX idx_restaurant_verified (is_verified)
);

-- =====================================================
-- FOOD ITEMS
-- =====================================================
CREATE TABLE food_items (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id   BIGINT NOT NULL,
    name            VARCHAR(150) NOT NULL,
    description     TEXT,
    price           DECIMAL(10,2) NOT NULL,
    category        VARCHAR(100),
    is_veg          BOOLEAN NOT NULL DEFAULT TRUE,
    is_available    BOOLEAN NOT NULL DEFAULT TRUE,
    image_url       VARCHAR(500),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE,
    INDEX idx_fooditem_restaurant (restaurant_id),
    INDEX idx_fooditem_category (category)
);

-- =====================================================
-- CART
-- =====================================================
CREATE TABLE carts (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE,
    restaurant_id   BIGINT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE SET NULL
);

-- =====================================================
-- CART ITEMS
-- =====================================================
CREATE TABLE cart_items (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id         BIGINT NOT NULL,
    food_item_id    BIGINT NOT NULL,
    quantity        INT NOT NULL DEFAULT 1,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (food_item_id) REFERENCES food_items(id) ON DELETE CASCADE,
    UNIQUE KEY uq_cart_food (cart_id, food_item_id),
    INDEX idx_cartitem_cart (cart_id)
);

-- =====================================================
-- ORDERS
-- =====================================================
CREATE TABLE orders (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    restaurant_id       BIGINT NOT NULL,
    address_id          BIGINT NOT NULL,
    delivery_partner_id BIGINT NULL,
    total_amount        DECIMAL(10,2) NOT NULL,
    delivery_fee        DECIMAL(10,2) DEFAULT 0.00,
    tax_amount          DECIMAL(10,2) DEFAULT 0.00,
    status              ENUM('PLACED','CONFIRMED','PREPARING','READY_FOR_PICKUP','OUT_FOR_DELIVERY','DELIVERED','CANCELLED','REJECTED') NOT NULL DEFAULT 'PLACED',
    payment_status      ENUM('PENDING','PAID','FAILED','REFUNDED') NOT NULL DEFAULT 'PENDING',
    payment_method      VARCHAR(50),
    order_date          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    picked_up_at        TIMESTAMP NULL,
    delivery_date       TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
    FOREIGN KEY (address_id) REFERENCES addresses(id),
    FOREIGN KEY (delivery_partner_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_order_user (user_id),
    INDEX idx_order_restaurant (restaurant_id),
    INDEX idx_order_status (status),
    INDEX idx_order_delivery_partner (delivery_partner_id)
);

-- =====================================================
-- ORDER ITEMS
-- =====================================================
CREATE TABLE order_items (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id        BIGINT NOT NULL,
    food_item_id    BIGINT NOT NULL,
    quantity        INT NOT NULL,
    price           DECIMAL(10,2) NOT NULL,
    subtotal        DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (food_item_id) REFERENCES food_items(id),
    INDEX idx_orderitem_order (order_id)
);

-- =====================================================
-- REVIEWS
-- =====================================================
CREATE TABLE reviews (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    restaurant_id   BIGINT NOT NULL,
    order_id        BIGINT,
    rating          INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment         TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE SET NULL,
    INDEX idx_review_restaurant (restaurant_id),
    INDEX idx_review_user (user_id)
);

-- =====================================================
-- SAMPLE SEED DATA
-- Password hash below is BCrypt for "password"
-- =====================================================
INSERT INTO users (name, email, password, phone, role) VALUES
('Admin User',         'admin@fooddelivery.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '9999999999', 'ADMIN'),
('John Customer',      'john@example.com',       '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '9876543210', 'CUSTOMER'),
('Raj Owner',          'raj@restaurant.com',     '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '9876543211', 'RESTAURANT_OWNER'),
('Priya Driver',       'priya@delivery.com',     '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '9876543212', 'DELIVERY_PARTNER'),
('Aakash Driver',      'aakash@delivery.com',    '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '9876543213', 'DELIVERY_PARTNER'),
('Meera Owner',        'meera@restaurant.com',   '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '9876543214', 'RESTAURANT_OWNER');

INSERT INTO addresses (user_id, street, city, state, zip_code, country, is_default) VALUES
(2, '123 MG Road',  'Ahmedabad', 'Gujarat', '380001', 'India', TRUE),
(3, '45 SG Highway', 'Ahmedabad', 'Gujarat', '380015', 'India', TRUE),
(6, '78 CG Road',   'Ahmedabad', 'Gujarat', '380009', 'India', TRUE);

-- Verified, active restaurants ready for ordering
INSERT INTO restaurants (name, description, owner_id, address_id, phone, cuisine_type, opens_at, closes_at, is_verified, is_active) VALUES
('Spice Garden', 'Authentic Indian cuisine with a modern twist', 3, 2, '9876500000', 'Indian', '10:00:00', '23:00:00', TRUE, TRUE),
('Pizza Palace', 'Wood-fired pizzas and Italian classics',       3, 2, '9876500001', 'Italian', '11:00:00', '23:30:00', TRUE, TRUE),
('Dragon Wok',   'Indo-Chinese street favourites',               6, 3, '9876500002', 'Chinese', '12:00:00', '23:00:00', TRUE, TRUE),
-- Pending verification (for admin demo)
('Burger Junction', 'Gourmet burgers and shakes — new!',         6, 3, '9876500003', 'American', '11:00:00', '23:00:00', FALSE, TRUE);

INSERT INTO food_items (restaurant_id, name, description, price, category, is_veg) VALUES
(1, 'Paneer Butter Masala',  'Creamy tomato gravy with paneer cubes',   280.00, 'Main Course', TRUE),
(1, 'Chicken Biryani',       'Hyderabadi style biryani with raita',     350.00, 'Main Course', FALSE),
(1, 'Garlic Naan',           'Tandoor-baked naan with garlic',           60.00, 'Bread',       TRUE),
(1, 'Gulab Jamun',           'Two pieces, served warm',                  90.00, 'Dessert',     TRUE),
(2, 'Margherita Pizza',      'Classic mozzarella and basil',            320.00, 'Pizza',       TRUE),
(2, 'Pepperoni Pizza',       'Loaded with pepperoni',                   450.00, 'Pizza',       FALSE),
(2, 'Garlic Bread',          'Crispy with herb butter',                 150.00, 'Sides',       TRUE),
(3, 'Veg Hakka Noodles',     'Stir-fried with vegetables',              220.00, 'Noodles',     TRUE),
(3, 'Chilli Chicken',        'Spicy, dry-style chilli chicken',         310.00, 'Main Course', FALSE),
(3, 'Spring Rolls',          'Crispy veg spring rolls, 4 pcs',          180.00, 'Starters',    TRUE);
