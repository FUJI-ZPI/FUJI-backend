# FUJI - Japanese Learning App Backend

This repository contains the backend service for the FUJI application, a mobile app designed to help users learn
Japanese handwriting (Kanji) and vocabulary using a Spaced Repetition System (SRS).

This service provides the core REST API for the mobile client, managing user data, authentication, learning materials,
and handwriting recognition.

## Core Features

* **Authentication**: Secure user login and registration via Google OAuth, managed with JWT.
* **SRS (Spaced Repetition System)**: Manages user learning schedules (lessons and reviews) for kanji.
* **Kanji & Vocabulary**: Serves learning materials (kanji, vocabulary) based on user's level.
* **Handwriting Recognition**: Analyzes user-drawn kanji strokes to provide recognition results.
* **AI Tutor**: Integrates with OpenAI to provide a conversational chatbot ("Yuki-sensei") for language practice.
* **File Storage**: Uses MinIO for storing related files, such as audio.

## Tech Stack

* **Framework**: Spring Boot 3
* **Language**: Java 21
* **Database**: PostgreSQL
* **Data Persistence**: Spring Data JPA
* **Database Migrations**: Flyway
* **Authentication**: Spring Security, JWT (JSON Web Tokens), Google OAuth Client
* **API**: Spring Web (REST)
* **API Documentation**: Springdoc (Swagger UI)
* **File Storage**: MinIO
* **AI Integration**: OpenAI API
* **Containerization**: Docker & Docker Compose
* **Build Tool**: Maven
* **Utilities**: Lombok

## Authors

- **[Tomasz Jaskólski](https://github.com/Tomek4861)**
- **[Tomasz Milewski](https://github.com/tommilewski)**
- **[Tymoteusz Lango](https://github.com/tymek805)**
