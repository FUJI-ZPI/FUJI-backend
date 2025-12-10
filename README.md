# FUJI - Japanese Learning App Backend

<p>
Backend service for the FUJI mobile app, enabling Japanese handwriting mastery through algorithm-based verification.
</p>

<div align="center">
<img width="600" alt="FUJI poster" src="https://github.com/user-attachments/assets/c2219f08-5b16-48de-8006-a0fd76f899ac" />

</div>


[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker)](https://www.docker.com/)
[![AWS](https://img.shields.io/badge/AWS-Elastic%20Beanstalk-FF9900?style=flat-square&logo=amazonaws)](https://aws.amazon.com/)


## Core Features

* **Authentication**: Secure user login and registration via Google OAuth, managed with JWT.
* **SRS (Spaced Repetition System)**: Adapts learning schedules and reviews based on user progress.
* **Learning Content**: Serves kanji and vocabulary materials adapted to proficiency level.
* **Handwriting Recognition**: Verifies stroke order and shape correctness using custom algorithms.
* **AI Tutor**: Integrates with OpenAI to provide a conversational chatbot ("Yuki-sensei") for language practice.
* **File Storage**: Uses MinIO/AWS S3 for storing related files, such as audio.

## Tech Stack

- **Core**: Java 21, Spring Boot 3
* **Database**: PostgreSQL, Spring Data JPA, Flyway
* **Security**: Spring Security, JWT, Google OAuth Client
* **API**: Spring Web (REST), Springdoc (Swagger UI)
* **Integrations**: OpenAI API, MinIO (Local), Firebase Cloud Messaging
* **Cloud & Deployment**: AWS Elastic Beanstalk, RDS, S3, ECR
* **DevOps**: Docker, Docker Compose, GitHub Actions
  
## Authors

- **[Tomasz Jaskólski](https://github.com/Tomek4861)**
- **[Tomasz Milewski](https://github.com/tommilewski)**
- **[Tymoteusz Lango](https://github.com/tymek805)**
- **[Michał Górniak](https://github.com/przyjaciel-placek)**

