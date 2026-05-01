-- =============================================
-- DDL.sql
-- Customer Management System
-- Database: MariaDB 11.x
-- =============================================

-- Create Database
CREATE DATABASE IF NOT EXISTS customerdb;
USE customerdb;

-- =============================================
-- Table: country (Master Data)
-- =============================================
CREATE TABLE IF NOT EXISTS country (
                                       id      BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       name    VARCHAR(255) NOT NULL UNIQUE
    );

-- =============================================
-- Table: city (Master Data)
-- =============================================
CREATE TABLE IF NOT EXISTS city (
                                    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    name        VARCHAR(255) NOT NULL,
    country_id  BIGINT,
    CONSTRAINT fk_city_country
    FOREIGN KEY (country_id)
    REFERENCES country(id)
    );

-- =============================================
-- Table: customer
-- =============================================
CREATE TABLE IF NOT EXISTS customer (
                                        id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        name            VARCHAR(255) NOT NULL,
    date_of_birth   DATE NOT NULL,
    nic_number      VARCHAR(255) NOT NULL UNIQUE
    );

-- =============================================
-- Table: mobile_number
-- =============================================
CREATE TABLE IF NOT EXISTS mobile_number (
                                             id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             number      VARCHAR(255) NOT NULL,
    customer_id BIGINT,
    CONSTRAINT fk_mobile_customer
    FOREIGN KEY (customer_id)
    REFERENCES customer(id)
    ON DELETE CASCADE
    );

-- =============================================
-- Table: address
-- =============================================
CREATE TABLE IF NOT EXISTS address (
                                       id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       address_line_1  VARCHAR(255),
    address_line_2  VARCHAR(255),
    city_id         BIGINT,
    country_id      BIGINT,
    customer_id     BIGINT,
    CONSTRAINT fk_address_city
    FOREIGN KEY (city_id)
    REFERENCES city(id),
    CONSTRAINT fk_address_country
    FOREIGN KEY (country_id)
    REFERENCES country(id),
    CONSTRAINT fk_address_customer
    FOREIGN KEY (customer_id)
    REFERENCES customer(id)
    ON DELETE CASCADE
    );

-- =============================================
-- Table: customer_family (Self-referencing)
-- =============================================
CREATE TABLE IF NOT EXISTS customer_family (
                                               customer_id         BIGINT NOT NULL,
                                               family_member_id    BIGINT NOT NULL,
                                               PRIMARY KEY (customer_id, family_member_id),
    CONSTRAINT fk_family_customer
    FOREIGN KEY (customer_id)
    REFERENCES customer(id)
    ON DELETE CASCADE,
    CONSTRAINT fk_family_member
    FOREIGN KEY (family_member_id)
    REFERENCES customer(id)
    ON DELETE CASCADE
    );