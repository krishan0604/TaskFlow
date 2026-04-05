# 🚀 TaskFlow — Microservices Task Management System

![Java](https://img.shields.io/badge/Java-21-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-green) ![MySQL](https://img.shields.io/badge/MySQL-8.0-blue) ![JWT](https://img.shields.io/badge/JWT-Auth-red)

## 🏗️ Architecture
Client → API Gateway (8080) → Auth Service (8081)
→ Task Service (8082)
↕
Eureka Server (8761)

## ⚙️ Tech Stack
| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 3.3 |
| Security | Spring Security 6 + JWT |
| Gateway | Spring Cloud Gateway |
| Discovery | Netflix Eureka |
| Database | MySQL 8 + JPA/Hibernate |
| Build | Maven |

## 🔐 API Endpoints

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register new user |
| POST | `/auth/login` | Login → get tokens |
| POST | `/auth/refresh` | Refresh access token |
| POST | `/auth/logout` | Revoke refresh token |

### Tasks (🔒 Bearer Token Required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/tasks` | Create task |
| GET | `/tasks` | Get tasks (USER=own, ADMIN=all) |
| GET | `/tasks/{id}` | Get task by ID |
| PUT | `/tasks/{id}` | Update task |
| DELETE | `/tasks/{id}` | Delete task |

## ▶️ Quick Start
```bash
# 1. Start MySQL and create databases
mysql -u root -p -e "CREATE DATABASE taskapp_auth; CREATE DATABASE taskapp_tasks;"

# 2. Start in order
cd eureka-server  && mvn spring-boot:run
cd auth-service   && mvn spring-boot:run
cd task-service   && mvn spring-boot:run
cd api-gateway    && mvn spring-boot:run
```

## ✅ Features
- 🔑 JWT Access Token (15 min) + Refresh Token (7 days)
- 👥 Role-Based Access Control (USER / ADMIN)
- 🔄 Optimistic Locking for concurrent updates (`@Version`)
- 🛡️ Global Exception Handling
- ✔️ Input Validation
- 🌐 Service Discovery via Eureka
- 🚪 Centralized JWT validation at Gateway

## 📁 Project Structure
TaskFlow/
├── 📦 eureka-server/     Service Registry
├── 🌐 api-gateway/       Gateway + JWT Filter
├── 🔐 auth-service/      Auth + Token Management
└── ✅ task-service/       Task CRUD + RBAC

---
⭐ Built with Spring Boot 3.3 + Java 21 | Krishan Kumar
