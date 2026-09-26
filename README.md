# Pika

Pika is a chat workspace with a React and TypeScript frontend and Spring Boot API.

## Frontend

```sh
cd frontend
npm install
npm run dev
```

The Vite app runs at `http://localhost:5173`. Its default API address is `http://localhost:8080/api/v1`; override it with `VITE_API_BASE_URL` in `frontend/.env` if needed.

Run `npm run typecheck` from `frontend/` to check the TypeScript sources.

## Backend

Run PostgreSQL with pgvector from the project root:

```sh
docker compose -f backend/docker-compose.yml up -d
```

Then start Spring Boot from `backend/`:

```sh
cd backend
mvn spring-boot:run
```

The backend reads `OPENAI_API_KEY` from the environment for its chat and embeddings configuration.
