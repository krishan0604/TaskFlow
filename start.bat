@echo off
echo Starting Eureka Server...
start "Eureka Server" cmd /c "mvn -pl eureka-server spring-boot:run"
timeout /t 10

echo Starting API Gateway...
start "API Gateway" cmd /c "mvn -pl api-gateway spring-boot:run"

echo Starting Auth Service...
start "Auth Service" cmd /c "mvn -pl auth-service spring-boot:run"

echo Starting Task Service...
start "Task Service" cmd /c "mvn -pl task-service spring-boot:run"

echo All microservices are launching. Ensure PostgreSQL is running on port 5432 with databases 'taskapp_auth' and 'taskapp_tasks'.
