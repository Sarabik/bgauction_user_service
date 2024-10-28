INSERT INTO users (username, password, email, enabled, role, first_name, last_name, country, city, delivery_info) VALUES
('john_doe', '$2a$10$Lb9/ALyCj7iMVL7y3ojT4uvjVfcYffDwcBz6V2uTc912epmefzQIm', 'john.doe@example.com', TRUE, 'USER', 'John', 'Doe', 'USA', 'New York', '123 Main St'),
('jane_smith', '$2a$10$Q4L5J7G4nlPHibApUsWgjOr57ymUiy6sgv041YupYCpoKrIlRC0cS', 'jane.smith@example.com', TRUE, 'USER', 'Jane', 'Smith', 'USA', 'Los Angeles', '456 Elm St'),
('alice_johnson', '$2a$10$.o2qkK8yuHu4.RsbzMY8Gu9lESKF16q5PlwwLwoJvjLfGm270d1/G', 'alice.johnson@example.com', FALSE, 'USER', 'Alice', 'Johnson', 'USA', 'Chicago', '789 Oak St');
