CREATE DATABASE hotel_db;
USE hotel_db;

CREATE TABLE rooms (
  id INT PRIMARY KEY AUTO_INCREMENT,
  room_number INT UNIQUE NOT NULL,
  room_type VARCHAR(50) NOT NULL,
  price_per_night DECIMAL(10,2) NOT NULL
);

CREATE TABLE reservations (
  id INT PRIMARY KEY AUTO_INCREMENT,
  guest_name VARCHAR(100) NOT NULL,
  room_id INT NOT NULL,
  check_in DATE NOT NULL,
  check_out DATE NOT NULL,
  status VARCHAR(20) DEFAULT 'Booked',
  FOREIGN KEY (room_id) REFERENCES rooms(id)
);

INSERT INTO rooms (room_number, room_type, price_per_night) VALUES
(101, 'Standard', 2000),
(102, 'Standard', 2000),
(201, 'Deluxe', 3500);
