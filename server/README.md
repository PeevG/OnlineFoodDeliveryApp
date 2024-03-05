# Yummy Delivery Project
## Description
Food delivery application that allows users to browse and order a variety of foods. The platform also provides easy management for administrators
## Features
- User authentication with login and registration endpoints
- Admin dashboard for managing food and beverage items
- REST API endpoints for retrieving, adding, updating, and deleting food items
- Cloudinary integration for automatic upload and deletion of product images
- Pagination functionality for efficient management of large datasets
- Email verification on successful user registration
- Shopping cart functionality with endpoints for adding and removing items
- Order management with endpoints for creating new orders and retrieving order history
- Address management with endpoints for adding, updating, and deleting user addresses
- Change password functionality for users
## Tech stack
- `Java`, `Spring Boot`, `PostgreSQL`, `Junit`, `Mockito`
- `Swagger-UI`, `Docker`
- `Cloudinary` (for storing product images)
## Api
![Swagger-ss1.png](readme-images%2FSwagger-ss1.png)
![Swagger-ss2.png](readme-images%2FSwagger-ss2.png)
![Swagger-ss3.png](readme-images%2FSwagger-ss3.png)
## How to run
- Ensure you have Docker installed and running on your machine
- Clone the Yummy Delivery project repository
- If you don't have Cloudinary account, you will have to create one to get access credentials
- In .env file configure the required environment variables
- In folder /docker run 'docker-compose up -d' to build and start the Docker containers
## ER Diagram
![Er diagram.png](readme-images%2FEr%20diagram.png)