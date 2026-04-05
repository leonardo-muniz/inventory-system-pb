# Inventory Management System (PB - TP5)

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3-brightgreen.svg)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg)
![Build Status](https://github.com/leonardo-muniz/inventory-system-pb/actions/workflows/ci-cd.yml/badge.svg)

A professional inventory management system developed as part of the Software Engineering curriculum. This project demonstrates a complete CI/CD lifecycle, automated testing, and cloud deployment strategies.

### **Relatório:** [Clique aqui para acessar o relatório](RELATORIO.md)

## 🌐 Live Demo & Environments

The project is deployed across multiple environments to simulate a real-world release cycle:

* **🚀 Production (Main Branch):** [Inventory | Products](https://inventory-system-pb-main.up.railway.app/products)
    * *Direct deployment from the main branch after successful CI.*
* **🧪 Staging (Staging Branch):** [Inventory | Products](https://inventory-system-pb-staging.up.railway.app/)
    * *Pre-production environment used for E2E validation and manual approval.*

## 🚀 DevOps & CI/CD Pipeline

This project follows a rigorous automated pipeline hosted on **GitHub Actions**:

1.  **Build & Unit Tests:** Automated compilation and execution of JUnit 5 suite (100% coverage).
2.  **SAST (Static Analysis):** Security scanning using **GitHub CodeQL** to identify vulnerabilities.
3.  **Code Quality:** Static analysis with **PMD** and coverage reporting with **JaCoCo**.
4.  **Staging Deployment:** Automatic deployment to the Staging environment on Railway.
5.  **E2E Testing:** Automated post-deploy validation (Simulated Selenium Suite).
6.  **Manual Approval:** Production deployment requires manual intervention/review in GitHub Actions.
7.  **Production Promotion:** Once approved, code is merged into the production flow.

## 🛠️ Tech Stack

* **Backend:** Java 21, Spring Boot 3.3, Spring Data JPA.
* **Frontend:** Thymeleaf, Bootstrap 5.
* **Database:** H2 (Local Development).
* **DevOps:** Docker, GitHub Actions, Railway.
* **Testing:** JUnit 5, Mockito, MockMvc, JaCoCo.

## 🐳 Running Locally

### Using Docker
1. Clone the repository.
2. Run the following command:
   ```bash
   docker-compose up -d --build
   ```
3. Access: http://localhost:8080 or http://localhost:8080/products

### Using Maven
1. Run this command:
   ```bash
   ./mvnw spring-boot:run
   ```
2. Access: http://localhost:8080 or http://localhost:8080/products

## 📖 API Documentation (Swagger/OpenAPI)

The system includes a fully documented REST API for programmatic access to the inventory. You can explore, test, and view the JSON schema through the **Swagger UI** interface.

* **Interactive Documentation:** [Inventory (Main Branch) | API Docs](https://inventory-system-pb-main.up.railway.app/products/swagger-ui.html)
* **Interactive Documentation:** [Inventory (Staging Branch) | API Docs](https://inventory-system-pb-staging.up.railway.app/products/swagger-ui.html)
* **OpenAPI Specification (JSON):** `/products/api-docs`

### 🔑 Available Endpoints:
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/products` | List all inventory products |
| `POST` | `/api/products` | Create a new product (Validation required) |
| `GET` | `/api/products/{id}` | Get specific product details |
| `PUT` | `/api/products/{id}` | Update an existing product |
| `DELETE` | `/api/products/{id}` | Remove a product from inventory |

> **Note:** The API and the Web UI share the same backend logic, ensuring data consistency across all interfaces.
