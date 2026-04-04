# Finance Dashboard Backend

A backend system for managing financial records with role-based access control, built using Spring Boot, PostgreSQL, and JWT authentication.

---

##  Features

* JWT Authentication & Authorization
* Role-Based Access Control (Admin, Analyst, Viewer)
* Financial Records CRUD
* Pagination & Sorting
* Search & Filtering
* Soft Delete Support
* Dashboard APIs (summary, category, trends)
* Global Exception Handling
* API Documentation (Swagger)

---

## Roles & Permissions

| Role    | Permissions                                        |
| ------- | -------------------------------------------------- |
| Viewer  | Read-only access to own records                    |
| Analyst | Read all records + dashboard insights              |
| Admin   | Full access (create, update, delete, manage users) |

---

##  Tech Stack

* Java 17+
* Spring Boot
* Spring Security (JWT)
* Spring Data JPA
* PostgreSQL
* Gradle
* Swagger (OpenAPI)

---

##  Configuration

This project uses **environment-based configuration**.

Create a file named `.env.properties` in the root directory:

```properties
POSTGRES_URL=jdbc:postgresql://localhost:5432/<db_name>
POSTGRES_USER=<your_username>
POSTGRES_PASSWORD=<your_password>
JWT_SECRET=<your_secret_key>
```

---

##  Database Setup

* Ensure PostgreSQL is running
* Create a database manually
* Hibernate will auto-create/update tables

```properties
spring.jpa.hibernate.ddl-auto=update
```

---

##  Running the Application (Gradle)

```bash
./gradlew build
./gradlew bootRun
```

---

## Running with Docker

---

###  Step 1: Run PostgreSQL container

```bash
docker run -d \
  --name postgres-db \
  -e POSTGRES_DB=finance \
  -e POSTGRES_USER=FRM \
  -e POSTGRES_PASSWORD=FRM \
  -p 5432:5432 \
  postgres:16-alpine
```

---

###  Step 2: Build backend image

```bash
docker build -t finance-backend .
```

---

### Step 3: Run backend container

```bash
docker run -d \
  --name backend \
  -p 8080:8080 \
  -e POSTGRES_URL=jdbc:postgresql://host.docker.internal:5432/finance \
  -e POSTGRES_USER=FRM \
  -e POSTGRES_PASSWORD=FRM \
  -e JWT_SECRET=dev_secret_key \
  finance-backend
```

---

##  Security Note

* In the docker-compose file, the JWT secret is intentionally kept for development purpose only.
  Do not remove it from the docker-compose configuration.
  Use it only for local/development running.
* Do NOT use hardcoded or weak secrets in production
* In production, secrets should be managed using:

    * Environment variables
    * Secret managers (Vault, AWS Secrets Manager, etc.)

---

##  Authentication

* JWT-based authentication
* Login endpoint returns token
* Token must be passed in header:

```
Authorization: Bearer <your_token>
```

---

##  API Endpoints

###  Auth

* `POST /auth/register`
* `POST /auth/login`

---

###  Records

* `GET /records/all?search` → Get records (pagination, search, filters)
* `POST /records` → Create record (Admin only)
* `PUT /records/{id}` → Update record (Admin only)
* `DELETE /records/{id}` → Soft delete (Admin only)

---

### Dashboard

* `GET /dashboard/summary`
* `GET /dashboard/category`
* `GET /dashboard/trends`

---

###  Users

* `GET /user/**` → Admin only

---

##  Pagination & Sorting

```
GET /records/all?search?page=0&size=10&sort=category,asc
```

---

##  Filtering & Search

```
GET /records/all?search=food
GET /records/all?type=EXPENSE
GET /records/all?amount=500
```

---

## API Documentation

Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```

---

##  Notes

* Soft delete is implemented using `isDeleted` flag
* Role-based access is enforced using Spring Security
* Default pagination: page=0, size=10, sorted by category
* Ensure DB columns use `VARCHAR` (not `bytea`) for text fields

---

##  Future Improvements

* Rate limiting
* Unit & integration tests
* Refresh tokens
* Caching

---

##  Author

Jawad Khan

---
