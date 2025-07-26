# Survey Application Frontend

A React-based frontend for the Survey Application, built with TypeScript and Material-UI.

## Features

- **User Interface**: Browse and participate in surveys
- **Admin Portal**: Secure admin dashboard for survey management
- **Responsive Design**: Works on desktop and mobile devices
- **Real-time Updates**: Live updates for responses and upvotes
- **Analytics**: Interactive charts and statistics for admins

## Tech Stack

- **React 18** with TypeScript
- **Material-UI (MUI)** for UI components
- **React Router** for navigation
- **Axios** for HTTP requests
- **Recharts** for data visualization
- **React Context** for state management

## Getting Started

### Prerequisites

- Node.js 18 or higher
- npm or yarn

### Installation

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

The application will open at `http://localhost:3000`

### Build for Production

```bash
npm run build
```

## Project Structure

```
src/
├── components/          # Reusable UI components
│   ├── Header.tsx      # Navigation header
│   └── Footer.tsx      # Page footer
├── context/            # React Context providers
│   └── AuthContext.tsx # Authentication state
├── pages/              # Page components
│   ├── HomePage.tsx    # Landing page
│   ├── SurveyListPage.tsx      # Survey listing
│   ├── SurveyDetailPage.tsx    # Survey participation
│   ├── AdminLoginPage.tsx      # Admin login
│   ├── AdminDashboardPage.tsx  # Admin dashboard
│   ├── AdminSurveyCreatePage.tsx # Create survey
│   └── AdminSurveyEditPage.tsx   # Edit survey
├── services/           # API service functions
│   ├── authService.ts  # Authentication API
│   ├── surveyService.ts # Survey API
│   └── adminService.ts # Admin API
├── types/              # TypeScript type definitions
│   └── index.ts        # All type interfaces
├── App.tsx             # Main app component
└── index.tsx           # Application entry point
```

## Available Scripts

- `npm start` - Start development server
- `npm build` - Build for production
- `npm test` - Run tests
- `npm eject` - Eject from Create React App

## Environment Variables

Create a `.env` file in the frontend directory:

```env
# For local development (when running frontend and backend separately)
REACT_APP_API_URL=http://localhost:8080/api

# For Docker/production (when using nginx proxy)
REACT_APP_API_URL=/api
```

## Features in Detail

### User Features
- Browse active surveys
- Participate in surveys with various question types
- View top responses with upvotes
- Submit responses
- Upvote responses (one per user per response)

### Admin Features
- Secure login with JWT authentication
- Dashboard with survey analytics
- Create and edit surveys
- Manage questions and their order
- Export survey data to CSV
- View detailed response statistics

### Question Types Supported
- Text responses
- Multiple choice
- Rating scales (1-5)
- Yes/No questions
- Likert scales

## API Integration

The frontend communicates with the backend through RESTful APIs:

- **Authentication**: JWT-based login/logout
- **Surveys**: CRUD operations for surveys
- **Responses**: Submit and retrieve responses
- **Analytics**: Get statistics and reports

## Styling

The application uses Material-UI (MUI) for consistent styling and theming. The theme is configured in `App.tsx` and can be customized as needed.

## State Management

Authentication state is managed using React Context (`AuthContext`), providing:
- User authentication status
- Admin role verification
- Token management
- Login/logout functionality

## Error Handling

The application includes comprehensive error handling:
- API error responses
- Network connectivity issues
- Form validation errors
- User-friendly error messages

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Contributing

1. Follow the existing code structure and naming conventions
2. Use TypeScript for all new components
3. Add appropriate error handling
4. Test on multiple screen sizes
5. Update documentation as needed

## Troubleshooting

### Common Issues

1. **CORS errors**: Ensure the backend is running and CORS is properly configured
2. **API connection issues**: Check that the backend is running on the correct port
3. **Build errors**: Clear node_modules and reinstall dependencies

### Development Tips

- Use the React Developer Tools for debugging
- Check the browser console for error messages
- Use the Network tab to debug API calls
- Test responsive design using browser dev tools

## License

This project is for educational and demonstration purposes. 