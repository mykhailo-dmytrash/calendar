# Calendar Service

A Spring Boot microservice for managing calendar events with MySQL database integration.

## Features

- RESTful API for event management
- MySQL database with Flyway migrations
- Pagination support
- Input validation
- OpenAPI documentation
- Docker containerization

## Prerequisites

- Docker and Docker Compose
- Java 25 (for local development)
- Maven (for local development)

## Quick Start with Docker

### Run the Application

#### Option 1: Full Stack (MySQL + Calendar Service)
```bash
# Build and start all services in background
docker-compose --profile full up -d --build

# View logs
docker-compose logs -f calendar-service

# Stop all services
docker-compose down
```

#### Option 2: Database Only (for local development)
```bash
# Start only MySQL database
docker-compose --profile database up -d

# Stop database
docker-compose down
```

### Access Points

- **Calendar API**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **MySQL Database**: localhost:3306
  - Username: `calendar_user`
  - Password: `calendar_password`
  - Database: `calendar`

## Local Development

### Database Configuration

The application uses the following database settings:
- **Host**: localhost (local) / mysql (Docker)
- **Port**: 3306
- **Database**: calendar
- **Username**: calendar_user
- **Password**: calendar_password

## API Endpoints

- `GET /api/events` - List events with pagination
- `POST /api/events` - Create new event
- `GET /api/events/{id}` - Get event by ID
- `PUT /api/events/{id}` - Update event
- `DELETE /api/events/{id}` - Delete event

## Technology Stack

- **Java**: 25
- **Spring Boot**: 3.5.7
- **Database**: MySQL 8.0
- **Migration**: Flyway
- **Documentation**: SpringDoc OpenAPI
- **Container**: Docker with Amazon Corretto JDK
