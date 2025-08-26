# Sample - Microservice Development with Kafka

This project demonstrates a microservice architecture using Spring Boot, Apache Kafka, and Docker Compose.

## Architecture

The project consists of the following services:

- **Zookeeper** (Port 2181): Apache Zookeeper for Kafka coordination
- **Kafka** (Ports 9092, 9094): Apache Kafka message broker
- **Eureka** (Port 8761): Netflix Eureka naming server for service discovery
- **Hello Service** (Port 8080): Spring Boot service that produces messages to Kafka
- **Messenger Service** (Port 8081): Spring Boot service that consumes messages from Kafka

## Prerequisites

- Docker and Docker Compose installed
- Maven (for local development)

## Quick Start

### 1. Build and Start All Services

```bash
# Build all services and start the stack
docker-compose up --build -d

# Check service status
docker-compose ps
```

### 2. Verify Services

- **Eureka Dashboard**: http://localhost:8761
- **Hello Service**: http://localhost:8080/hello/{name}
- **Messenger Service**: http://localhost:8081/health

### 3. Test the Message Flow

1. **Produce a message** (Hello Service → Kafka):
   ```bash
   curl "http://localhost:8080/hello/Alice"
   ```

2. **View consumed messages** (Kafka → Messenger Service):
   ```bash
   # Check messenger service logs
   docker-compose logs -f messenger
   ```

3. **Direct Kafka consumer** (optional):
   ```bash
   # Connect to Kafka container and use console consumer
   docker-compose exec kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic greetings --from-beginning
   ```

## Service Details

### Zookeeper
- **Port**: 2181
- **Purpose**: Coordination service for Kafka
- **Health Check**: Port connectivity check

### Kafka
- **Ports**: 9092 (internal), 9094 (external)
- **Purpose**: Message broker
- **Topics**: `greetings` (auto-created)
- **Health Check**: Broker API version check

### Eureka Naming Server
- **Port**: 8761
- **Purpose**: Service discovery and registration
- **Health Check**: HTTP endpoint check

### Hello Service
- **Port**: 8080
- **Purpose**: Produces greeting messages to Kafka
- **Endpoints**:
  - `GET /hello/{name}` - Sends "Hello {name}" to Kafka
- **Dependencies**: Eureka, Kafka

### Messenger Service
- **Port**: 8081
- **Purpose**: Consumes messages from Kafka
- **Endpoints**:
  - `GET /health` - Health check endpoint
- **Dependencies**: Kafka
- **Consumer Group**: `messenger-group`

## Development

### Local Development

```bash
# Build individual services
mvn clean package -pl sample-microservice-eureka-naming-server
mvn clean package -pl sample-microservice-hello
mvn clean package -pl sample-microservice-messenger

# Run with Docker Compose
docker-compose up -d
```

### Service Management

```bash
# Start specific service
docker-compose up -d zookeeper kafka

# Stop all services
docker-compose down

# View logs
docker-compose logs -f [service-name]

# Rebuild specific service
docker-compose build [service-name]
```

### Troubleshooting

1. **Zookeeper unhealthy**:
   ```bash
   # Check Zookeeper logs
   docker-compose logs zookeeper
   
   # Test connectivity
   docker-compose exec zookeeper nc -z localhost 2181
   ```

2. **Kafka connection issues**:
   ```bash
   # Check Kafka logs
   docker-compose logs kafka
   
   # Test broker connectivity
   docker-compose exec kafka kafka-broker-api-versions --bootstrap-server localhost:9092
   ```

3. **Service registration issues**:
   - Check Eureka dashboard: http://localhost:8761
   - Verify service logs for registration errors

## Configuration

### Environment Variables

- `KAFKA_BOOTSTRAP_SERVERS`: Kafka broker addresses
- `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`: Eureka server URL
- `APP_KAFKA_TOPIC`: Kafka topic name (default: `greetings`)
- `JAVA_OPTS`: JVM options for memory tuning

### Kafka Topics

- **greetings**: Topic for greeting messages
  - Partitions: 1
  - Replication Factor: 1
  - Auto-created by Spring Boot

## Project Structure

```
├── docker-compose.yml                    # Main orchestration file
├── zookeeper/
│   └── Dockerfile                       # Zookeeper container
├── kafka/
│   └── Dockerfile                       # Kafka container
├── sample-microservice-eureka-naming-server/
│   ├── Dockerfile                       # Eureka service container
│   └── src/                             # Spring Boot application
├── sample-microservice-hello/
│   ├── Dockerfile                       # Hello service container
│   └── src/                             # Spring Boot application
└── sample-microservice-messenger/
    ├── Dockerfile                       # Messenger service container
    └── src/                             # Spring Boot application
```

## Health Checks

All services include health checks to ensure proper startup order:

- **Zookeeper**: Port connectivity (2181)
- **Kafka**: Broker API version check
- **Eureka**: HTTP endpoint check (8761)
- **Hello Service**: HTTP endpoint check (8080/hello)
- **Messenger Service**: HTTP endpoint check (8081/health)

Services wait for their dependencies to be healthy before starting.