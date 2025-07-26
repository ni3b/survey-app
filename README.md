# Survey Application

A full-stack survey web application built with Spring Boot (backend) and React (frontend).

## Features

### User Features
- Browse and participate in active surveys
- View questions one at a time or as a list
- See top 5 responses with upvotes for each question
- Submit new responses
- Upvote responses (one per user per response)
- Real-time updates after interactions
- Responsive design for desktop and mobile

### Admin Features
- Secure admin login
- Dashboard with survey analytics
- Survey management (create, edit, delete, schedule)
- Question management within surveys
- Export survey data to CSV/Excel
- View all responses and upvotes
- Analytics and engagement trends

## Tech Stack

### Backend
- **Framework**: Spring Boot 3.x
- **Database**: SQLite
- **Security**: Spring Security with JWT
- **API Documentation**: OpenAPI/Swagger

### Frontend
- **Framework**: React 18 with TypeScript
- **UI Library**: Material-UI (MUI)
- **State Management**: React Context + Hooks
- **Charts**: Recharts for analytics
- **HTTP Client**: Axios

## Project Structure

```
survey-project/
├── backend/                 # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/survey/
│   │   │   │   ├── controller/
│   │   │   │   ├── model/
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   ├── security/
│   │   │   │   └── config/
│   │   │   └── resources/
│   │   └── test/
│   ├── pom.xml
│   └── README.md
├── frontend/                # React application
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── hooks/
│   │   ├── types/
│   │   └── utils/
│   ├── package.json
│   └── README.md
├── database/                # Database scripts
│   ├── schema.sql
│   └── seed.sql
└── README.md
```

## Quick Start

### Prerequisites
- Java 17 or higher
- Node.js 18 or higher
- npm or yarn

### Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Build the project:
   ```bash
   ./mvnw clean install
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

The frontend will start on `http://localhost:3000`

## Database Setup

The application uses SQLite with automatic schema creation. Sample data is loaded on startup.

### Default Admin Credentials
- **Username**: admin
- **Password**: admin123

## API Documentation

Once the backend is running, you can access the API documentation at:
- Swagger UI: `http://localhost:8080/swagger-ui.html` (direct backend)
- OpenAPI JSON: `http://localhost:8080/v3/api-docs` (direct backend)
- Swagger UI: `http://localhost/swagger-ui/` (via nginx proxy)
- OpenAPI JSON: `http://localhost/v3/api-docs` (via nginx proxy)

## Key Endpoints

### Public Endpoints
- `GET /api/surveys/active` - List active surveys
- `GET /api/surveys/{id}` - Get survey details
- `POST /api/responses` - Submit a response
- `POST /api/responses/{id}/upvote` - Upvote a response

### Admin Endpoints
- `POST /api/auth/login` - Admin login
- `GET /api/admin/surveys` - List all surveys (admin)
- `POST /api/admin/surveys` - Create survey
- `PUT /api/admin/surveys/{id}` - Update survey
- `DELETE /api/admin/surveys/{id}` - Delete survey
- `GET /api/admin/analytics` - Get analytics data
- `GET /api/admin/export/{surveyId}` - Export survey data

## Features in Detail

### Survey Management
- Create surveys with title, description, and date range
- Add multiple questions to surveys
- Schedule surveys to start/end at specific times
- Publish surveys to make them available to users

### Response System
- Users can submit responses to questions
- Top 5 responses with most upvotes are displayed
- Users can upvote responses (one per user per response)
- Real-time updates when responses or upvotes are added

### Analytics
- Total responses per survey
- Participation rates
- Top upvoted responses
- Engagement trends over time
- Export functionality for data analysis

## Security

- JWT-based authentication for admin users
- Input validation and sanitization
- CORS configuration for frontend integration
- Role-based access control

## Development

### Adding New Features
- Backend: Add controllers, services, and repositories as needed
- Frontend: Create new components and pages in the appropriate directories
- Database: Add new entities and update the schema

### Testing
- Backend tests: `./mvnw test`
- Frontend tests: `npm test`

## Troubleshooting

### Common Issues
1. **Port conflicts**: Change ports in application.properties or package.json
2. **Database issues**: Delete the survey.db file to reset the database
3. **CORS errors**: Ensure backend is running and CORS is properly configured

### Logs
- Backend logs: Check console output or application.log
- Frontend logs: Check browser console
- Docker logs: Use `docker-compose logs survey-app` to view all logs including nginx access logs
- Nginx access logs: Configured to output to Docker stdout/stderr for easy viewing with `docker logs`

#### Backend Logging Configuration
The backend uses comprehensive logging with configurable log levels via Docker Compose environment variables:

**Available Log Levels:**
- `LOG_LEVEL_ROOT` - Root logging level (default: INFO)
- `LOG_LEVEL_SURVEY` - Main application package (default: INFO)
- `LOG_LEVEL_CONTROLLER` - Controller layer (default: INFO)
- `LOG_LEVEL_SERVICE` - Service layer (default: INFO)
- `LOG_LEVEL_REPOSITORY` - Repository layer (default: INFO)
- `LOG_LEVEL_SECURITY` - Security components (default: INFO)
- `LOG_LEVEL_CONFIG` - Configuration classes (default: INFO)
- `LOG_LEVEL_SPRING_SECURITY` - Spring Security (default: WARN)
- `LOG_LEVEL_SPRING_WEB` - Spring Web (default: INFO)
- `LOG_LEVEL_HIBERNATE` - Hibernate SQL (default: WARN)

**Usage Examples:**
```bash
# Development (verbose logging)
docker compose up -d -e LOG_LEVEL_ROOT=DEBUG -e LOG_LEVEL_SURVEY=DEBUG

# Production (minimal logging)
docker compose up -d -e LOG_LEVEL_ROOT=WARN -e LOG_LEVEL_SURVEY=INFO

# Debug specific issues
docker compose up -d -e LOG_LEVEL_SECURITY=DEBUG -e LOG_LEVEL_HIBERNATE=DEBUG
```

For detailed logging configuration, see `LOGGING_CONFIGURATION.md`.

#### Docker Logging Configuration
The application is configured to send all logs (including nginx access logs) to Docker's logging system:
- Access logs: Sent to `/dev/stdout` (visible with `docker logs`)
- Error logs: Sent to `/dev/stderr` (visible with `docker logs`)
- Log rotation: Configured with max-size 10MB and max-file 3

To view logs:
```bash
# View all logs
docker-compose logs survey-app

# Follow logs in real-time
docker-compose logs -f survey-app

# View recent logs
docker logs --tail 50 <container-id>
```

## Contributing

1. Follow the existing code structure and naming conventions
2. Add appropriate tests for new features
3. Update documentation as needed
4. Ensure all endpoints are properly documented in Swagger

## License

This project is for educational and demonstration purposes. 