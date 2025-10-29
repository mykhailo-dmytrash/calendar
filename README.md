# Calendar Application

This repository contains a full-stack calendar application with a Spring Boot backend and Angular frontend.

## Services

- **MySQL Database** - Running on port `3306`
- **Spring Boot Backend** - Running on port `8080`
- **Angular Frontend** - Running on port `4200`

## Quick Start

### Prerequisites

- Docker
- Docker Compose

### Running the Application

From the root directory, run:

```bash
docker compose up --build -d
```

The `-d` flag runs the containers in detached mode (in the background).

This command will:
1. Start the MySQL database
2. Build and start the Spring Boot backend (waits for MySQL to be healthy)
3. Build and start the Angular frontend

To view logs:

```bash
docker compose logs -f
```

### Accessing the Application

- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080
- **MySQL**: localhost:3306

### Stopping the Application

```bash
docker compose down
```

To stop and remove volumes (including database data):

```bash
docker compose down -v
```

## Development

### Backend (Spring Boot)

Located in `calendar/` directory.

### Frontend (Angular)

Located in `calendar-front/` directory.

## Architecture

The frontend uses nginx as a web server and proxies API requests to the backend through the `/api/` path. This avoids CORS issues when running in Docker.

### API Proxy Configuration

When running in Docker, the frontend makes API calls to `/api/*` which nginx proxies to the backend service at `http://calendar-backend:8080/`.

## Notes

- The backend waits for MySQL to be healthy before starting
- All services are connected through a Docker bridge network
- Persistent data is stored in a Docker volume named `mysql_data`

