# SMS Portal – Backend

## Overview
The backend of SMS Portal is a Spring Boot application responsible for managing SMS operations, contact and group management, delivery tracking, CSV processing, and payment integrations. It supports a single-admin model focused on simplicity and reliability.

## Features
- Admin authentication and authorization
- SMS management:
  - Individual SMS
  - Bulk SMS
  - Group-based SMS
- Contact management
- Group creation and management
- CSV import for contacts
- CSV export for:
  - Delivery reports
  - Contacts
- SMS delivery status tracking
- eSewa and Khalti payment integration
- RESTful API architecture

## Tech Stack
- Java
- Spring Boot
- Spring Security
- JPA / Hibernate
- MySQL
- Maven

## Modules
- Authentication and Admin Management
- SMS Processing
- Contact and Group Management
- Delivery Report Management
- CSV Import/Export
- Payment Integration

## Configuration
Update `application.yml` or `application.properties` with:
- Database configuration
- SMS gateway credentials
- eSewa credentials
- Khalti credentials

## Build and Run
```bash
mvn clean install
mvn spring-boot:run
