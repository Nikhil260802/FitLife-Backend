# 🏋️ FitLife – Online Fitness Course Platform (Backend)

FitLife is a production-ready backend application for an online fitness course platform.  
It supports secure user authentication, course enrollment, video streaming, and online payments.

---

## 🚀 Features

- 🔐 JWT-based Authentication & Authorization
- 👤 Role-based access (ADMIN / USER)
- 📚 Course & Enrollment Management
- 🎥 Secure Video Upload & Streaming
- 💳 Razorpay Payment Gateway Integration
- 🧾 Payment Verification & Enrollment Confirmation
- 🗄 PostgreSQL (Neon) Database
- 🐳 Dockerized Deployment
- ☁️ Deployed on Render (Free Tier)

---

## 🛠 Tech Stack

- **Backend:** Java 17, Spring Boot 3
- **Security:** Spring Security, JWT
- **Database:** PostgreSQL (Neon)
- **ORM:** Hibernate / JPA
- **Payment:** Razorpay
- **Email:** Brevo SMTP
- **Deployment:** Docker, Render
- **Build Tool:** Maven

---

## 🔑 Authentication Flow

1. User registers / logs in
2. JWT token is generated
3. Token is required for protected APIs
4. Role-based access enforced (`ADMIN`, `USER`)

---

## 💳 Payment Flow (Razorpay)

1. User creates order for a course
2. Razorpay order ID generated
3. Payment completed on Razorpay
4. Payment verified using signature
5. Enrollment created after successful verification

---

## 📁 Project Structure

com.FitLife
├── Config
├── Controller
├── Service
├── Repository
├── Entity
├── Dto
├── Helper


---

## 🔐 Environment Variables

All sensitive keys are stored securely using environment variables.

```properties
DB_URL=jdbc:postgresql://...
DB_USERNAME=xxxx
DB_PASSWORD=xxxx

RAZORPAY_KEY=xxxx
RAZORPAY_SECRET=xxxx

MAIL_USERNAME=xxxx
MAIL_PASSWORD=xxxx


## ▶️ Run Locally

1. Clone the repository
   git clone https://github.com/your-username/FitLife.git

2. Navigate to the project
   cd FitLife

3. Set environment variables in `.env`

4. Build the project
   mvn clean install

5. Run the application
   mvn spring-boot:run

Server will start at:
http://localhost:8080

## 📡 API Endpoints

### Auth
POST /api/auth/register  
POST /api/auth/login  

### Courses
GET /api/public/courses  
POST /api/admin/course  

### Enrollment
POST /api/public/enroll  
GET /api/user/enrollments  

### Payment
POST /api/public/createOrder  
POST /api/public/verify  

### Videos
POST /api/admin/addVideo  
GET /api/public/accessVideo/{fileName}

## 🚀 Deployment

Backend deployed on Render (Free Tier)

• Java 17
• Spring Boot
• PostgreSQL (Neon DB)
• Environment variables configured on Render

Build Command:
mvn clean install

Start Command:
java -jar target/FitLife.jar

## 🔐 Security

• JWT Authentication
• Role based access (ADMIN / USER)
• Secured REST APIs
• Encrypted passwords (BCrypt)

## 🔮 Future Enhancements

• Frontend integration (Angular / React)
• Video streaming optimization
• Admin analytics dashboard


## 👤 Author

Nikhil Sharma  
Java Backend Developer  
Spring Boot | REST APIs | PostgreSQL