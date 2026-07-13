# Food Delivery System

A complete food delivery application originally built with Spring MVC and JDBC, now modernized with **Spring Boot 3.5**, **Spring Data JPA**, **MapStruct**, and **JWT Authentication**. It ships with **two front-ends in the same application**:

1. **JSP web UI** — server-rendered pages (login, browse restaurants, cart, checkout, order tracking, owner dashboard).
2. **REST API** — JSON endpoints for programmatic access, mobile clients, or testing with curl/Postman, secured via JWT.

Built on the **Jakarta EE 10** stack — runs on an embedded Tomcat server or deployed to an external Tomcat 11.x.

## Tech Stack

| Layer            | Choice                                              |
|------------------|-----------------------------------------------------|
| Framework        | Spring Boot 3.5.0                                   |
| View Layer       | Jakarta Pages (JSP) 4.0 + JSTL 3.0                  |
| DB Access        | Spring Data JPA (Hibernate) + MySQL driver          |
| Mapping & Utils  | MapStruct + Lombok                                  |
| Database         | MySQL 8.x                                           |
| API Security     | Spring Security + JWT (JSON Web Tokens)             |
| JSON             | Jackson 2.18                                        |
| Build Tool       | Maven (war packaging)                               |
| Java Version     | **22** (or newer)                                   |

## Entities & DTOs

The system models 9 core entities, now backed by Spring Data JPA `Repository` interfaces and mapped to DTOs for the REST API using MapStruct:

1. **User** — customers, restaurant owners, delivery partners, admin
2. **Address** — multiple per user, one marked default
3. **Restaurant** — owned by a user, located at an address
4. **FoodItem** — menu items for a restaurant
5. **Cart** — one per user, single-restaurant rule enforced
6. **CartItem** — line items in a cart
7. **Order** — placed from a cart, holds delivery details and status
8. **OrderItem** — snapshot of items at order time (with price)
9. **Review** — user ratings for restaurants; restaurant rating auto-recomputed

## Project Structure

```
food-delivery-system/
├── pom.xml
├── src/main/
│   ├── java/com/fooddelivery/
│   │   ├── FoodDeliveryApplication.java # Spring Boot entry point
│   │   ├── config/        # Spring configuration 
│   │   ├── security/      # JWT, CustomUserDetails, SecurityConfig
│   │   ├── model/         # JPA Entities
│   │   ├── dto/           # Data Transfer Objects
│   │   ├── mapper/        # MapStruct interfaces
│   │   ├── dao/           # Spring Data JPA Repositories (*Repository)
│   │   ├── service/       # Business logic + @Transactional
│   │   ├── controller/    # REST API Controllers (@RestController, /api/**)
│   │   ├── web/           # JSP page controllers + SessionUtil
│   │   └── exception/     # Custom exceptions + @ControllerAdvice
│   ├── resources/
│   │   ├── application.yml    # Spring configuration
│   │   ├── schema.sql         # Legacy SQL (replaced by JPA + Seeder)
│   │   └── logback.xml
│   └── webapp/
│       ├── WEB-INF/views/      # All JSP files
│       └── resources/css/styles.css
```

## Web UI Pages

| URL                                | Description                                |
|------------------------------------|--------------------------------------------|
| `/`                                | Redirects to login or home                 |
| `/login`, `/register`              | Authentication                             |
| `/home`                            | Restaurant list with search + cuisine chips |
| `/restaurants/{id}`                | Menu, veg/non-veg + category filters, reviews |
| `/cart`                            | Cart, order summary, place order           |
| `/orders`, `/orders/{id}`          | Order history + status timeline            |
| `/profile`                         | User info + manage addresses               |
| `/owner/dashboard`                 | Restaurant owner home — accept/reject orders |
| `/owner/restaurants/{id}/menu`     | Manage menu items (add, edit, toggle, delete) |
| `/owner/menu/{itemId}/edit`        | Edit a single menu item with live preview  |
| `/delivery/dashboard`              | Driver — pickup queue + active deliveries  |
| `/delivery/history`                | Driver — completed trips + total earnings  |
| `/admin/dashboard`                 | Admin — platform analytics overview        |
| `/admin/users?role=`               | Admin — manage users, suspend/reactivate   |
| `/admin/restaurants?filter=`       | Admin — approve / suspend restaurants      |
| `/logout`                          | Invalidates session                        |

## Setup

### Prerequisites

- **JDK 22 or newer** 
- **Maven 3.8+**
- **MySQL 8.x** running locally (or any reachable MySQL host)

### 1. Create the database

Create an empty database named `food_delivery_db` in your MySQL server:
```sql
CREATE DATABASE food_delivery_db;
```
*Note: Hibernate will automatically create the tables on startup (`spring.jpa.hibernate.ddl-auto=update`), and `DatabaseSeeder.java` will automatically insert demo data.*

### 2. Configure DB credentials

Edit `src/main/resources/application.yml` to set your MySQL username and password, or pass them as environment variables:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/food_delivery_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:YOUR_PASSWORD}
```

### 3. Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

Alternatively, you can build the WAR file and deploy it to a standalone Tomcat 11 instance.

The app will be available at `http://localhost:8080/`.

### 4. Demo Accounts

The `DatabaseSeeder.java` automatically creates several demo accounts (password is `password` for all, except Admin which is `admin`):

| Email                      | Role               |
|----------------------------|--------------------|
| `john@example.com`         | Customer           |
| `raj@restaurant.com`       | Restaurant owner   |
| `maria@restaurant.com`     | Restaurant owner   |
| `li@restaurant.com`        | Restaurant owner   |
| `sophie@restaurant.com`    | Restaurant owner   |
| `anil@restaurant.com`      | Restaurant owner   |
| `elena@restaurant.com`     | Restaurant owner   |
| `kenji@restaurant.com`     | Restaurant owner   |
| `alex@delivery.com`        | Delivery partner   |
| `chen@delivery.com`        | Delivery partner   |
| `admin@example.com`        | Admin              |

Along with these accounts, it seeds dummy addresses, 7 restaurants, and 14 food items.

## API Reference

All endpoints return JSON and are available under `/api`.
*Note: The API now requires a JWT token for most operations. Use `/api/users/login` to obtain a token and include it in the `Authorization: Bearer <token>` header.*

**Key Endpoints:**
- **Users**: `/api/users/register`, `/api/users/login`
- **Restaurants**: `/api/restaurants`, `/api/restaurants/{id}`
- **Food Items**: `/api/food-items`, `/api/food-items/restaurant/{id}`
- **Cart**: `/api/cart/user/{userId}`
- **Orders**: `/api/orders/place`, `/api/orders/{id}`
- **Reviews**: `/api/reviews`

## Design Notes (Recent Refactoring)

- **Spring Boot & JPA:** The project has been migrated from raw Spring MVC and `JdbcTemplate` to **Spring Boot** and **Spring Data JPA**. This significantly reduced boilerplate code and simplified configuration.
- **DTOs and MapStruct:** To prevent over-fetching and infinite recursion in JSON responses, the REST API now uses **Data Transfer Objects (DTOs)**. **MapStruct** handles the automatic mapping between JPA Entities and DTOs.
- **JWT Security:** The REST API is secured with JSON Web Tokens (JWT). The web UI continues to use session-based authentication.
- **Lombok:** Used to eliminate boilerplate getters, setters, constructors, and builders across Entities and DTOs.
