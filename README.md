# IntelliLearn — AI-Powered Learning & Assessment Platform

**Learn. Assess. Improve.**

IntelliLearn is a full-stack web-based learning platform that combines structured learning, PDF-based study material, AI-generated quizzes, and performance tracking.

## Live Demo

**Live Application:** https://intellilearn-production-e01e.up.railway.app

## Key Features

* Secure student and teacher authentication
* Course and subject management
* PDF-based study material upload and download
* AI-powered quiz generation from uploaded PDF content
* Automatically generated multiple-choice questions
* Quiz attempt and score tracking
* Student performance dashboard
* JWT-based authentication and authorization
* Role-based access for students and teachers
* Persistent MySQL database
* Cloud deployment using Railway

## AI Quiz Generation

The platform uses the Gemini API to generate quizzes from uploaded study material.

**Workflow:**

`PDF Upload → PDF Text Extraction → Gemini API → MCQ Generation → Quiz Storage → Student Attempt → Score`

Apache PDFBox is used to extract text from PDF documents before sending relevant content to the AI service.

## Technology Stack

### Backend

* Java
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* REST APIs
* JWT Authentication

### Frontend

* Thymeleaf
* HTML
* CSS
* JavaScript
* Bootstrap

### Database

* MySQL

### AI & Document Processing

* Google Gemini API
* Apache PDFBox

### Tools & Deployment

* Maven
* Git & GitHub
* Postman
* Thunder Client
* Railway

## Application Architecture

```text
Client
  ↓
Thymeleaf / JavaScript
  ↓
Spring Boot Controllers
  ↓
Service Layer
  ↓
Repository Layer
  ↓
MySQL Database
```

For AI-based quiz generation:

```text
Uploaded PDF
     ↓
PDFBox Text Extraction
     ↓
AI Service
     ↓
Gemini API
     ↓
Generated MCQs
     ↓
Quiz & Questions Stored in MySQL
```

## Main Modules

### Authentication

* Student and teacher registration/login
* JWT-based authentication
* Role-based authorization

### Course & Subject Management

Teachers can manage courses and subjects, while students can access available learning content.

### Notes Module

Teachers can upload PDF study material associated with subjects. Authenticated users can view and download the available notes.

### AI Quiz Module

Students can generate quizzes from uploaded PDF notes. The system extracts the document content and sends it to Gemini for MCQ generation.

### Quiz & Assessment

Students can attempt generated quizzes and submit their answers. Scores are stored for performance tracking.

### Dashboard

Students can view their quiz performance and learning progress.

## REST API Highlights

| Method | Endpoint                         | Purpose               |
| ------ | -------------------------------- | --------------------- |
| POST   | `/notes/upload/{subjectId}`      | Upload PDF notes      |
| GET    | `/notes/subject/{subjectId}`     | Get subject notes     |
| GET    | `/notes/{noteId}/download`       | Download PDF          |
| POST   | `/quiz/generate/note/{noteId}`   | Generate AI quiz      |
| GET    | `/quiz/{quizId}`                 | Get quiz              |
| GET    | `/quiz/subject/{subjectId}`      | Get subject quizzes   |
| GET    | `/quiz/note/{noteId}`            | Get note quizzes      |
| POST   | `/quiz-attempt/submit`           | Submit quiz attempt   |
| GET    | `/dashboard/student/{studentId}` | Get student dashboard |

## Project Structure

```text
IntelliLearn/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ...
│   │   └── resources/
│   │       ├── templates/
│   │       ├── static/
│   │       └── application.properties
│   └── test/
├── uploads/
├── pom.xml
└── README.md
```

## Database

The application uses MySQL with Spring Data JPA/Hibernate for persistent data management.

Major entities include:

* User
* Course
* Subject
* Note
* Quiz
* Question
* QuizAttempt
* StudentAnswer

## Security

The application implements:

* JWT authentication
* Password-based authentication
* Role-based authorization
* Protected REST endpoints
* Student/Teacher access control

## Deployment

The application is deployed using **Railway**.

Deployment architecture:

```text
GitHub Repository
       ↓
Railway
       ↓
Spring Boot Application
       ↓
Railway MySQL
       ↓
Live Web Application
```

Environment variables are used for sensitive configuration such as:

* Database credentials
* Gemini API key
* JWT secret

Sensitive credentials are not stored directly in the source code.

## Future Enhancements

* Personalized learning recommendations
* Advanced analytics and visualizations
* Question difficulty adaptation
* Additional AI-powered learning features
* Improved quiz question validation
* Cloud-based file storage
  
## Authors

**Diksha Sharma**
B.Tech Mechanical Engineering | PGCP-AC, C-DAC Noida
Java | Spring Boot | REST APIs | SQL | Backend Development

**Tanishtha**
Co-Developer — IntelliLearn

---

**IntelliLearn — Learn. Assess. Improve.**

**Live Application:** https://intellilearn-production-e01e.up.railway.app
