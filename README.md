# 🚗 ParkEase — Smart Parking Slot Booking System

ParkEase is a full-stack web application that allows users to find, book, and manage parking slots in real-time. It is designed to solve real-world parking challenges such as slot availability, time-based booking, and conflict prevention.

---

## 📌 Features

### 🔐 Authentication

* User registration & login
* JWT-based secure authentication
* Protected routes and APIs

### 🅿️ Parking Management

* View all parking slots
* Real-time slot availability
* Slot types (Car / Bike)

### 📅 Booking System

* Time-based booking
* Prevents double booking using overlap detection
* View user bookings
* Cancel bookings

### 💰 Pricing

* Fixed hourly pricing
* Automatic cost calculation based on booking duration

---

## 🧠 Core Logic

ParkEase prevents double booking conflicts using the condition:

```
existing.start < newEnd AND existing.end > newStart
```

---

## 🏗️ Tech Stack

### Backend

* Spring Boot
* Spring Security + JWT
* MongoDB

### Frontend

* React.js
* Axios

---

## 📁 Project Structure

```
backend/
 ├── config/
 ├── controller/
 ├── service/
 ├── repository/
 ├── entity/
 ├── dto/
 ├── security/

frontend/
 ├── components/
 ├── pages/
 ├── services/
```

---

## ⚙️ API Endpoints

### Auth

* POST /auth/register
* POST /auth/login

### Slots

* GET /slots
* GET /slots/available?start=&end=

### Booking

* POST /booking
* GET /booking/my
* DELETE /booking/{id}

---

## 🚀 Getting Started

### 1. Clone Repository

```
git clone https://github.com/your-username/parkease.git
cd parkease
```

### 2. Backend Setup

```
cd backend
```

Update application.properties:

```
spring.data.mongodb.uri=your_mongodb_uri
jwt.secret=your_secret_key
```

Run:

```
mvn spring-boot:run
```

### 3. Frontend Setup

```
cd frontend
npm install
npm start
```

---

## 📊 Sample Booking Request

```json
{
  "slotId": "123",
  "startTime": "2026-04-28T10:00:00",
  "endTime": "2026-04-28T12:00:00"
}
```

---

## 🎯 Future Enhancements

* Real-time slot locking
* Multi-location parking
* Admin dashboard
* Notifications system
* Mobile responsiveness

---

## 👥 Team

* Ram Lal
* Riya Khandelwal
* Remanshu Goyal
* Kushagra Saraf
* Rupendra Yadav

---

## ⭐ Support

If you like this project, give it a star on GitHub!
