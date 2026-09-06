# SACS
The Sanitizable Access Control System (SACS) enhances cloud storage security by protecting data from malicious Data Publishers. It utilizes AES encryption and a sanitization process to validate data, ensuring that only authorized users with valid keys can access the decrypted content.

# Sanitizable Access Control System

A Java EE web application for secure, sanitizable access control, using JSP/Servlets, MySQL, and Apache Tomcat. This guide provides step-by-step deployment instructions for macOS and Linux (Ubuntu), including MySQL and Tomcat setup.

---

## Features
- User authentication and access control
- Data publishing and sanitization workflows
- MySQL backend
- Deployable on any Java EE-compatible server (Tomcat recommended)

---

## Prerequisites
- Java JDK 8 or 11
- Apache Ant
- Apache Tomcat 9
- MariaDB 10.5+ or MySQL 8+

---

## 1. Clone the Repository
```sh
git clone https://github.com/mirdujanah/SACS.git
cd SACS/SOURCE CODE/SanitizableAccessControlSystem
```

---

## 2. MySQL Setup

### a. Install MySQL
- **macOS:**
  ```sh
  brew install mysql
  brew services start mysql
  ```
- **Ubuntu:**
  ```sh
  sudo apt update
  sudo apt install mysql-server
  sudo systemctl start mysql
  ```

### b. Secure MySQL & Set Root Password
```sh
mysql_secure_installation
```

### c. Import Database
```sh
mysql -u root -p < ../../DATABASE/New\ Project\ 20230127\ 2212.sql
```

---

## 3. Configure Database Connection
- Configure the connection without editing source code:
  ```sh
  export SACS_DB_URL="jdbc:mariadb://localhost:3306/sacs"
  export SACS_DB_USER="root"
  export SACS_DB_PASSWORD="your-mysql-password"
  ```
- System properties with the same names can be used instead when starting Tomcat.
- Passwords are stored using PBKDF2 hashes that fit the existing `VARCHAR(45)` schema.
  Existing plaintext accounts are upgraded after their next successful login.

## 4. Configure File Storage
- Set `SACS_STORAGE_DIR` as an environment variable or JVM system property to choose
  the upload/key storage directory.
- If it is not set, SACS creates `sacs-storage` under the application temp directory
  (or the user home directory when no temp directory is available).

---

## 5. Install Java & Ant
- **macOS:**
  ```sh
  brew install openjdk@8 ant
  export JAVA_HOME="/usr/local/opt/openjdk@8"
  export PATH="$JAVA_HOME/bin:$PATH"
  ```
- **Ubuntu:**
  ```sh
  sudo apt install openjdk-8-jdk ant
  export JAVA_HOME="/usr/lib/jvm/java-8-openjdk-amd64"
  export PATH="$JAVA_HOME/bin:$PATH"
  ```

---

## 6. Install Tomcat 9
- **macOS:**
  ```sh
  brew install tomcat@9
  export CATALINA_HOME="/opt/homebrew/Cellar/tomcat@9/$(brew list --versions tomcat@9 | awk '{print $2}')/libexec"
  ```
- **Ubuntu:**
  ```sh
  sudo apt install tomcat9
  export CATALINA_HOME="/usr/share/tomcat9"
  ```

---

## 7. Build the Project
```sh
ant clean dist -Dj2ee.server.home="$CATALINA_HOME"
```
- The WAR file will be at `dist/SanitizableAccessControlSystem.war`

---

## 8. Deploy to Tomcat
```sh
cp dist/SanitizableAccessControlSystem.war $CATALINA_HOME/webapps/
# Start Tomcat
$CATALINA_HOME/bin/startup.sh   # or sudo systemctl restart tomcat9 (Ubuntu)
```

---

## 9. Access the Application
- Open: `http://localhost:8080/SanitizableAccessControlSystem/`
- For remote access, use your server's IP or domain.

---

## 10. Troubleshooting
- Check Tomcat logs: `$CATALINA_HOME/logs/`
- Check MySQL connection in `SQLconnection.java`
- Ensure all ports (8080 for Tomcat, 3306 for MySQL) are open

---

## 10. License
Copyright © 2023 Sanitizable Access Control System. All Rights Reserved.

---

## Credits
Developed by MIR DUJANAH ALI HUSSAINI.
