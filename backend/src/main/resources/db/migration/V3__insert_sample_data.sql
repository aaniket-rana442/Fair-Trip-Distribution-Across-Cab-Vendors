-- V3: Insert sample users, vendors, targets, and system configurations

-- Sample Users:
-- Admin: admin@example.com (password: Admin@123)
-- User: user@example.com (password: User@123)
-- Vendors: vendor1@example.com, vendor2@example.com, vendor3@example.com (password: Vendor@123)
INSERT INTO users (id, name, email, password_hash, role_id, status) VALUES
(1, 'System Administrator', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 2, 'ACTIVE'),
(2, 'Corporate Employee', 'user@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, 'ACTIVE'),
(3, 'Vendor 1 Operator', 'vendor1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 3, 'ACTIVE'),
(4, 'Vendor 2 Operator', 'vendor2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 3, 'ACTIVE'),
(5, 'Vendor 3 Operator', 'vendor3@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 3, 'ACTIVE');

-- Sample Vendors linked to Vendor Users
INSERT INTO vendors (id, user_id, vendor_code, vendor_name, contact_name, email, phone, status, total_capacity, available_capacity, supports_normal, supports_escort) VALUES
(1, 3, 'V1', 'Vendor V1 - Express Cabs', 'Rajesh Sharma', 'vendor1@example.com', '+91 98765 43210', 'ACTIVE', 15, 15, TRUE, TRUE),
(2, 4, 'V2', 'Vendor V2 - City Fleet', 'Priya Patel', 'vendor2@example.com', '+91 98765 43211', 'ACTIVE', 12, 12, TRUE, TRUE),
(3, 5, 'V3', 'Vendor V3 - Metro Shuttles', 'Amit Verma', 'vendor3@example.com', '+91 98765 43212', 'ACTIVE', 10, 10, TRUE, TRUE);

-- System Configurations
INSERT INTO system_configurations (config_key, config_value, description) VALUES
('VENDOR_RESPONSE_TIMEOUT_MINUTES', '5', 'Time in minutes before an unacknowledged vendor offer expires and triggers reallocation'),
('VENDOR_COOLDOWN_MINUTES', '10', 'Cooldown period in minutes after a vendor rejects a trip for that trip'),
('MAX_RETRY_ATTEMPTS', '5', 'Maximum reallocation attempts before marking trip as FAILED');

-- Targets for ZONE_0_15 (NORMAL: 50/30/20, ESCORT: 40/35/25)
INSERT INTO vendor_targets (vendor_id, zone, trip_category, target_percentage) VALUES
(1, 'ZONE_0_15', 'NORMAL', 50.0000),
(2, 'ZONE_0_15', 'NORMAL', 30.0000),
(3, 'ZONE_0_15', 'NORMAL', 20.0000),
(1, 'ZONE_0_15', 'ESCORT', 40.0000),
(2, 'ZONE_0_15', 'ESCORT', 35.0000),
(3, 'ZONE_0_15', 'ESCORT', 25.0000);

-- Targets for ZONE_15_25 (NORMAL: 45/35/20, ESCORT: 40/35/25)
INSERT INTO vendor_targets (vendor_id, zone, trip_category, target_percentage) VALUES
(1, 'ZONE_15_25', 'NORMAL', 45.0000),
(2, 'ZONE_15_25', 'NORMAL', 35.0000),
(3, 'ZONE_15_25', 'NORMAL', 20.0000),
(1, 'ZONE_15_25', 'ESCORT', 40.0000),
(2, 'ZONE_15_25', 'ESCORT', 35.0000),
(3, 'ZONE_15_25', 'ESCORT', 25.0000);

-- Targets for ZONE_25_PLUS (NORMAL: 40/35/25, ESCORT: 35/35/30)
INSERT INTO vendor_targets (vendor_id, zone, trip_category, target_percentage) VALUES
(1, 'ZONE_25_PLUS', 'NORMAL', 40.0000),
(2, 'ZONE_25_PLUS', 'NORMAL', 35.0000),
(3, 'ZONE_25_PLUS', 'NORMAL', 25.0000),
(1, 'ZONE_25_PLUS', 'ESCORT', 35.0000),
(2, 'ZONE_25_PLUS', 'ESCORT', 35.0000),
(3, 'ZONE_25_PLUS', 'ESCORT', 30.0000);
