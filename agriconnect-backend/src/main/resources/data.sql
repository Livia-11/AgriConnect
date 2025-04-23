-- Insert test farmer user (password is 'password123')
INSERT INTO users (email, username, password, role, created_at, updated_at)
VALUES ('test.farmer@example.com', 'Test Farmer', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'FARMER', NOW(), NOW())
ON DUPLICATE KEY UPDATE email=email;

-- Insert some initial categories
INSERT INTO categories (name, description, created_at, updated_at)
VALUES 
    ('Vegetables', 'Fresh vegetables from local farms', NOW(), NOW()),
    ('Fruits', 'Fresh fruits from local farms', NOW(), NOW()),
    ('Grains', 'Various types of grains', NOW(), NOW()),
    ('Dairy', 'Fresh dairy products', NOW(), NOW())
ON DUPLICATE KEY UPDATE name=name; 