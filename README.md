
# 🎯 Live Event Status Service

This Spring Boot-based service manages live event statuses and scores in-memory, publishes real-time scores to Kafka, and periodically fetches scores from an external API.

---

## 🛠 Features

- ✅ In-memory tracking of live events and their status  
- ✅ Update and retrieve scores for events  
- ✅ Scheduled polling for live scores  
- ✅ Publishes scores to Kafka with retry logic  
- ✅ REST APIs to control and query events  



## 🔧 Configuration

Update `application.properties` with:

```properties
# External score API
external.api.base-url=http://external-api-url.com

# Kafka config
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
````

---

## 🐳 Running Kafka with Docker

This project includes a `docker-compose.yaml` to spin up **Kafka and Zookeeper**.

### ✅ Start Kafka

```bash
docker-compose up -d
```

This will:

* Start **Kafka** on `localhost:9092`
* Start **Zookeeper** on `localhost:2181`

### 🔄 Stop Kafka

```bash
docker-compose down
```

---

## 🔗 REST Endpoints

### 📌 Event Status APIs (`/api/v1/events`)

| Method | Endpoint             | Description              |
| ------ | -------------------- | ------------------------ |
| POST   | `/update-status`     | Update event live status |
| GET    | `/is-live/{eventId}` | Check if event is live   |
| GET    | `/live`              | Get all live event IDs   |
| DELETE | `/{eventId}`         | Remove event             |
| GET    | `/all-statuses`      | Get all event statuses   |

### 📌 Score APIs (`/api/v1/scores`)

| Method | Endpoint     | Description                |
| ------ | ------------ | -------------------------- |
| POST   | `/update`    | Update score for an event  |
| GET    | `/{eventId}` | Get current score of event |

---

## 🧪 Running Tests

```bash
./gradlew test --rerun-tasks
```

---

## 🚀 Run the Application

```bash
./gradlew bootRun
```

---

## 📬 Sample cURL Commands

### Update event status:

```bash
curl -X POST http://localhost:8080/api/v1/events/update-status \
     -H "Content-Type: application/json" \
     -d '{"eventId": "match101", "eventStatus": "LIVE"}'
```

### Get event score:

```bash
curl http://localhost:8080/api/v1/scores/match101
```

### Update score:

```bash
curl -X POST http://localhost:8080/api/v1/scores/update \
     -H "Content-Type: application/json" \
     -d '{"eventId": "match101", "score": "2-0"}'
```

---

## ✍️ Authors

*  Saswat Sagar — Java Developer

---


