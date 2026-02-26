# SnapSpace 📸

![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=fff)
![Jakarta EE](https://img.shields.io/badge/Jakarta_EE-Servlets-4CAF50)
![Hibernate](https://img.shields.io/badge/Hibernate-ORM-59666C?logo=hibernate&logoColor=fff)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?logo=postgresql&logoColor=fff)
![Cloudinary](https://img.shields.io/badge/Cloudinary-Image_Storage-3448C5)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?logo=apachemaven&logoColor=fff)

**SnapSpace** is a personal project where I'm learning to build web applications with Java.


---

## Goals of the Project

- Learn Java web development from the ground up Servlets, Hibernate, JSP
- Understand how a layered MVC architecture works with Java
---

## How it's structured 
```
Controller (Servlets) → Service → DAO → Database
                           ↓
                       Cloudinary
```

- **Servlets** — handle HTTP only, nothing else
- **Services** — business logic, Cloudinary uploads, validation
- **DAOs** — Hibernate queries, nothing knows they exist except the services
- **Utils** — singleton managers for Hibernate and Cloudinary, config loading, password hashing
- **Filters** — authentication middleware, same idea as Express middleware

---

## Tech Stack

- **Java 21** + Jakarta Servlet API
- **Hibernate ORM** — PostgreSQL, auto DDL
- **Cloudinary** — cloud image storage and delivery
- **JSP + JSTL** — server-side templating
- **BCrypt** — password hashing
- **Maven** — dependency management

---

## Getting it running

### 1. Prerequisites
- Java 21
- Maven
- PostgreSQL — create a database and note the credentials
- Tomcat 11 — note your installation path
- A Cloudinary account — grab your cloud name, API key, and API secret from the dashboard

### 2. Config

Copy `config.example.properties` to `config.properties` and fill in your values:
```properties
# Cloudinary — get these from your Cloudinary dashboard
cloud_name=YOUR_CLOUD_NAME_HERE
api_key=YOUR_API_KEY_HERE
api_secret=YOUR_API_SECRET_HERE

# Database — your local PostgreSQL instance
db.url=jdbc:postgresql://localhost:5432/YOUR_DB_NAME_HERE
db.user=YOUR_DB_USER_HERE
db.password=YOUR_DB_PASSWORD_HERE
```

> `config.properties` is gitignored. Don't commit it — treat it like a password.

### 3. Deploy

Open `deploy.bat` and set `TOMCAT_HOME` to your Tomcat installation path:
```bat
set "TOMCAT_HOME=F:\apache-tomcat-11.0.15"
```

Then just run it:
```bash
deploy.bat
```

It will build the WAR with Maven, drop it into Tomcat's webapps folder, restart Tomcat, and you're live at:
```
http://localhost:8080/SnapSpace
```

---

## Notes

This is a learning project first. The goal isn't perfection, it's understanding.  
Every decision in the codebase is intentional and documented — if something looks unfamiliar, the comments explain the Node.js equivalent.