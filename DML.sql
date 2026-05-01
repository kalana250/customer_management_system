-- =============================================
-- DML.sql
-- Customer Management System
-- Sample Data for Testing
-- =============================================

USE customerdb;

-- =============================================
-- MASTER DATA: Countries
-- =============================================
INSERT IGNORE INTO country (name) VALUES
('Sri Lanka'),
('India'),
('United Kingdom'),
('United States'),
('Australia'),
('Canada'),
('Germany'),
('France'),
('Singapore'),
('Japan');

-- =============================================
-- MASTER DATA: Cities
-- =============================================
INSERT IGNORE INTO city (name, country_id) VALUES

-- Sri Lanka (country_id = 1)
('Colombo', 1),
('Kandy', 1),
('Galle', 1),
('Jaffna', 1),
('Negombo', 1),
('Matara', 1),
('Kurunegala', 1),
('Anuradhapura', 1),

-- India (country_id = 2)
('Mumbai', 2),
('Delhi', 2),
('Bangalore', 2),
('Chennai', 2),
('Hyderabad', 2),
('Kolkata', 2),

-- United Kingdom (country_id = 3)
('London', 3),
('Manchester', 3),
('Birmingham', 3),
('Leeds', 3),

-- United States (country_id = 4)
('New York', 4),
('Los Angeles', 4),
('Chicago', 4),
('Houston', 4),

-- Australia (country_id = 5)
('Sydney', 5),
('Melbourne', 5),
('Brisbane', 5),

-- Canada (country_id = 6)
('Toronto', 6),
('Vancouver', 6),
('Montreal', 6),

-- Germany (country_id = 7)
('Berlin', 7),
('Munich', 7),
('Hamburg', 7),

-- France (country_id = 8)
('Paris', 8),
('Lyon', 8),

-- Singapore (country_id = 9)
('Singapore City', 9),

-- Japan (country_id = 10)
('Tokyo', 10),
('Osaka', 10),
('Kyoto', 10);

-- =============================================
-- SAMPLE CUSTOMERS
-- =============================================
INSERT IGNORE INTO customer (name, date_of_birth, nic_number) VALUES
('John Doe',        '1990-01-15', '199012345678'),
('Jane Smith',      '1985-06-20', '198556789012'),
('Bob Johnson',     '1992-03-10', '199234567890'),
('Alice Brown',     '1988-11-25', '198823456789'),
('Charlie Lee',     '1995-07-08', '199512345678'),
('Diana Prince',    '1987-04-15', '198745678901'),
('Edward King',     '1993-09-22', '199367890123'),
('Fiona Green',     '1991-12-05', '199189012345'),
('George White',    '1986-08-30', '198690123456'),
('Hannah Clark',    '1994-02-18', '199401234567');

-- =============================================
-- SAMPLE MOBILE NUMBERS
-- =============================================
INSERT IGNORE INTO mobile_number (number, customer_id) VALUES
-- John Doe (customer_id = 1)
('0771234567', 1),
('0712345678', 1),

-- Jane Smith (customer_id = 2)
('0777654321', 2),

-- Bob Johnson (customer_id = 3)
('0763456789', 3),
('0754567890', 3),

-- Alice Brown (customer_id = 4)
('0741234567', 4),

-- Charlie Lee (customer_id = 5)
('0771111222', 5),

-- Diana Prince (customer_id = 6)
('0762222333', 6),

-- Edward King (customer_id = 7)
('0753333444', 7),

-- Fiona Green (customer_id = 8)
('0744444555', 8);

-- =============================================
-- SAMPLE ADDRESSES
-- =============================================
INSERT IGNORE INTO address
    (address_line_1, address_line_2, city_id, country_id, customer_id)
VALUES
-- John Doe - Colombo, Sri Lanka
('No 123, Main Street',     'Colombo 03',       1,  1,  1),

-- Jane Smith - Kandy, Sri Lanka
('No 45, Peradeniya Road',  'Kandy City',       2,  1,  2),

-- Bob Johnson - London, UK
('221B Baker Street',       'Westminster',      15, 3,  3),

-- Alice Brown - New York, USA
('350 Fifth Avenue',        'Manhattan',        19, 4,  4),

-- Charlie Lee - Sydney, Australia
('10 George Street',        'Sydney CBD',       23, 5,  5),

-- Diana Prince - Mumbai, India
('No 7, Marine Drive',      'South Mumbai',     9,  2,  6),

-- Edward King - Toronto, Canada
('100 King Street West',    'Downtown Toronto', 26, 6,  7),

-- Fiona Green - Colombo, Sri Lanka
('No 56, Galle Road',       'Colombo 06',       1,  1,  8),

-- George White - Berlin, Germany
('Unter den Linden 1',      'Mitte',            30, 7,  9),

-- Hannah Clark - Paris, France
('10 Rue de Rivoli',        'Paris 1er',        33, 8,  10);

-- =============================================
-- SAMPLE FAMILY MEMBERS
-- =============================================
INSERT IGNORE INTO customer_family
    (customer_id, family_member_id)
VALUES
-- John Doe and Jane Smith are family
(1, 2),
(2, 1),

-- Bob Johnson and Alice Brown are family
(3, 4),
(4, 3),

-- Charlie Lee and Diana Prince are family
(5, 6),
(6, 5),

-- Edward King and Fiona Green are family
(7, 8),
(8, 7);

-- =============================================
-- VERIFY DATA
-- =============================================
SELECT 'Countries inserted: ' AS Info, COUNT(*) AS Count FROM country
UNION ALL
SELECT 'Cities inserted: ',            COUNT(*)          FROM city
UNION ALL
SELECT 'Customers inserted: ',         COUNT(*)          FROM customer
UNION ALL
SELECT 'Mobile numbers inserted: ',    COUNT(*)          FROM mobile_number
UNION ALL
SELECT 'Addresses inserted: ',         COUNT(*)          FROM address
UNION ALL
SELECT 'Family links inserted: ',      COUNT(*)          FROM customer_family;