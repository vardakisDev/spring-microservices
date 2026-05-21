CREATE USER user_service WITH PASSWORD 'user_service';
CREATE USER product_service WITH PASSWORD 'product_service';
CREATE USER cart_service WITH PASSWORD 'cart_service';

CREATE DATABASE user_service OWNER user_service;
CREATE DATABASE product_service OWNER product_service;
CREATE DATABASE cart_service OWNER cart_service;
