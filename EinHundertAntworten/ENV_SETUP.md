# Environment Configuration Guide

## Overview

The EinHundertAntworten frontend uses environment-based configuration to manage different API URLs for development, staging, and production environments.

## Environment Files

### `.env.development`

Used during local development (`npm run dev`).

```
VITE_API_URL=http://localhost:3000
```

- Points to your local Spring Boot backend running on port 3000

### `.env.production`

Used when building for production (`npm run build`).

```
VITE_API_URL=https://your-heroku-app.herokuapp.com
```

- Points to your Heroku-deployed backend

### `.env.example`

Template file that documents available environment variables. Commit this to git.

## How It Works

1. **Development**: When you run `npm run dev`, Vite automatically loads `.env.development`
2. **Production Build**: When you run `npm run build`, Vite automatically loads `.env.production`
3. **Fallback**: If no environment file is found, the app defaults to `http://localhost:3000`

## Using Environment Variables in Code

The `VITE_` prefix is required for environment variables to be exposed to the frontend:

```typescript
// In auth.ts and other stores
serverIP: import.meta.env.VITE_API_URL || 'http://localhost:3000';
```

## Deployment to Cloudflare Pages

When deploying to Cloudflare Pages:

1. **Build Command**: `npm run build`
2. **Build Output Directory**: `dist`
3. **Environment Variables** in Cloudflare Dashboard:
   - Set `VITE_API_URL` to your Heroku backend URL (e.g., `https://my-backend.herokuapp.com`)

## Deployment to Heroku (Backend)

No environment changes needed for the backend on Heroku. The Spring Boot app will:

- Accept requests from your Cloudflare frontend
- Connect to MongoDB Atlas automatically

## Local Development Setup

For local development:

1. Ensure `.env.development` has `VITE_API_URL=http://localhost:3000`
2. Start backend: `./mvnw spring-boot:run` (from root)
3. Start frontend: `npm run dev` (from `EinHundertAntworten/`)
4. Open http://localhost:5173

## Testing Different Environments

To test with a different backend URL locally:

1. Create `.env.local` with your custom URL:
   ```
   VITE_API_URL=https://some-other-backend.com
   ```
2. Run `npm run dev` - it will use `.env.local` instead of `.env.development`
3. Don't commit `.env.local` to git (it's in `.gitignore`)
