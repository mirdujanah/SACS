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
- MySQL Server 5.7+

---

## 1. Clone the Repository
```sh
git clone https://github.com/<your-username>/SACS.git
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
- Edit `src/java/SACS/SQLconnection.java` with your MySQL username, password, and database name if needed.

---

## 4. Install Java & Ant
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

## 5. Install Tomcat 9
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

## 6. Build the Project
```sh
ant clean dist -Dj2ee.server.home="$CATALINA_HOME"
```
- The WAR file will be at `dist/SanitizableAccessControlSystem.war`

---

## 7. Deploy to Tomcat
```sh
cp dist/SanitizableAccessControlSystem.war $CATALINA_HOME/webapps/
# Start Tomcat
$CATALINA_HOME/bin/startup.sh   # or sudo systemctl restart tomcat9 (Ubuntu)
```

---

## 8. Access the Application
- Open: `http://localhost:8080/SanitizableAccessControlSystem/`
- For remote access, use your server's IP or domain.

---

## 9. Troubleshooting
- Check Tomcat logs: `$CATALINA_HOME/logs/`
- Check MySQL connection in `SQLconnection.java`
- Ensure all ports (8080 for Tomcat, 3306 for MySQL) are open

---

## 10. License
Copyright © 2023 Sanitizable Access Control System. All Rights Reserved.

---

## Credits
Developed by MIR DUJANAH ALI HUSSAINI.
