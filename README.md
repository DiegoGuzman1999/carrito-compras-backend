# Shopping Cart Backend

Backend del sistema de carrito de compras desarrollado con:

- Java 17
- Spring Boot
- PostgreSQL
- Maven

## Branching strategy

- `main` -> producción
- `develop` -> integración de desarrollo
- `qa` -> pruebas funcionales
- `release/*` -> preparación de versiones
- `feature/*` -> desarrollo por historia de usuario

## Run locally

### 1. Create database

```sql
CREATE DATABASE shopping_cart_db;
CREATE USER shopping_user WITH ENCRYPTED PASSWORD 'shopping_pass';
GRANT ALL PRIVILEGES ON DATABASE shopping_cart_db TO shopping_user;