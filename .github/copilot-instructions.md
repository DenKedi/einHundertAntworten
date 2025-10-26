# EinHundertAntworten - AI Agent Instructions

## Project Overview

EinHundertAntworten is a "100 Questions" game application with a **dual-codebase architecture**:

- **Backend**: Spring Boot 3.1.1 (Java 17) REST API with JWT authentication
- **Frontend**: Vue 3 + TypeScript + Vite SPA
- **Database**: MongoDB (Atlas cloud + local Docker for development)

## Critical Architecture Patterns

### Dual Project Structure

```
/                           # Root = Spring Boot backend
├── src/main/java/          # Backend Java code
├── pom.xml                 # Maven backend deps
└── EinHundertAntworten/    # Nested Vue frontend
    ├── src/                # Frontend TypeScript/Vue code
    └── package.json        # Frontend npm deps
```

**WHY**: Legacy hybrid architecture where backend serves Mustache templates but frontend migrated to Vue SPA. Backend runs on port 3000, frontend dev server proxies to it.

### Authentication Flow

1. **JWT Generation**: RSA key pair (`src/main/resources/certs/private.pem` + `public.pem`) encodes tokens in `TokenService`
2. **Token Storage**: Frontend stores JWT in localStorage via Pinia `auth` store (`EinHundertAntworten/src/stores/auth.ts`)
3. **Request Pattern**: Vue store fetches use `serverIP` constant + JWT in Authorization header
4. **Route Guards**: Vue router (`EinHundertAntworten/src/router/index.ts`) checks `auth.user` before protected routes

**Security Config**: `SecurityConfig.java` permits `/user/register`, `/user/login`, `/game/getAllQuestions|Answers`, `/home` without auth. Everything else requires JWT.

### Data Layer Pattern

**Backend**: Spring Data MongoDB repositories (e.g., `UserRepository extends MongoRepository<User, String>`)

- User model split: `User` (credentials) + `UserProfile` (game stats) linked by `profileID`
- Game models: `Question` and `Answer` entities with category filtering

**Frontend State**: Pinia stores in `stores/` - `auth.ts` (user/token), `game.ts` (quiz state)

### CORS Configuration

Backend allows `*` origins with GET/POST/PUT/DELETE methods (`SecurityConfig.corsConfigurationSource()`). Frontend stores (`auth.ts` and `game.ts`) use environment variable `VITE_API_URL` to dynamically set the backend URL.

## Development Workflows

### Backend Development

```powershell
# Start MongoDB (required first)
docker-compose up -d

# Run Spring Boot (from project root)
./mvnw spring-boot:run
# Server runs on http://localhost:3000
```

**Config**: `application.properties` connects to MongoDB Atlas (`mongodb+srv://...`) with database `100FragenGame`

### Frontend Development

```powershell
cd EinHundertAntworten
npm install
npm run dev
# Vite dev server on http://localhost:5173
# Automatically uses .env.development (VITE_API_URL=http://localhost:3000)
```

**Important**: Frontend API calls use environment variable `VITE_API_URL` from `.env.development` during development.

### Building Frontend for Production

```powershell
cd EinHundertAntworten
npm run build
# Uses .env.production (VITE_API_URL set to backend URL)
# Outputs to EinHundertAntworten/dist/
```

### Environment Configuration

- **`.env.development`**: Dev environment (local backend at localhost:3000)
- **`.env.production`**: Production environment (Heroku/deployed backend URL)
- **`.env.example`**: Template for environment variables
- See `EinHundertAntworten/ENV_SETUP.md` for detailed environment setup guide

Backend serves static files from `src/main/resources/static/` (legacy Mustache templates still exist but unused by Vue app).

## Project-Specific Conventions

### Package Organization

```
com.project.einHundertAntworten/
├── Controller/          # Legacy controllers (some unused)
├── User/               # User domain (entities, repos, controller)
├── Game/               # Question/Answer domain
├── service/            # TokenService for JWT
└── Misc/               # SecurityConfig, CustomUserDetailsService, Utility
```

### Naming Conventions

- **Java**: PascalCase classes, camelCase methods/fields
- **Vue Components**: PascalCase files (e.g., `LoginForm.vue`)
- **Routes**: kebab-case paths (`/gameManager` but component is `GameManagerView`)

### MongoDB Document Structure

```javascript
// users collection
{ _id, profileID, username, password (bcrypt), email, role }

// userProfiles collection
{ _id, userID, username, firstName, lastName, email, gamesPlayed, createdOn, score }

// questions/answers collections
{ _id, category, /* question/answer specific fields */ }
```

## Common Pitfalls

1. **CORS Issues**: If frontend can't reach backend, check `serverIP` in `auth.ts` matches running backend
2. **MongoDB Connection**: Backend fails without MongoDB running - start `docker-compose up` first
3. **Token Expiration**: JWTs expire in 2 hours (`TokenService` - line 41), no refresh logic implemented
4. **Role-Based Access**: Backend checks roles but frontend only stores in localStorage (not validated on every request)
5. **Mustache Templates**: Files in `src/main/resources/templates/` are legacy - Vue components in `EinHundertAntworten/src/views/` are active UI

## Integration Points

### Backend REST Endpoints

- **Auth**: `POST /user/register|login` (public)
- **User**: `GET /user/profile/:id`, `PUT /user/changePassword` (authenticated)
- **Game**: `GET /game/getQuestions?category=X`, `GET /game/getAllAnswers` (public for answers/questions)

### Frontend API Calls

All in Pinia stores using `fetch()` with pattern:

```typescript
const response = await fetch(`${this.serverIP}/endpoint`, {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${this.token}`, // if authenticated
  },
  body: JSON.stringify(data),
});
```

## Testing Strategy

**Current State**: No test files in `src/test/` - project relies on manual testing. When adding tests:

- Backend: Use `@SpringBootTest` with embedded MongoDB or Testcontainers
- Frontend: Configure Vitest (already in deps) for component tests

## Key Files Reference

- **Backend Entry**: `src/main/java/.../EinHundertAntwortenApplication.java`
- **Security**: `src/main/java/.../Misc/SecurityConfig.java`
- **Auth Logic**: `src/main/java/.../User/UserController.java`, `EinHundertAntworten/src/stores/auth.ts`
- **Frontend Entry**: `EinHundertAntworten/src/main.ts`
- **Routing**: `EinHundertAntworten/src/router/index.ts`
- **Build Configs**: `pom.xml` (backend), `EinHundertAntworten/package.json` + `vite.config.ts` (frontend)
