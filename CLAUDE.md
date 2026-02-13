# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Family Finance Management System — a team-based financial management app for families. Features include family member login, asset & transaction tracking, a score-based decision system, and recurring financial events.

**Current status:** Project is scaffolded (directory structure with `.gitkeep` placeholders). No implementation code exists yet.

## Tech Stack

- **Backend:** Java, Spring Boot, Maven
- **Frontend:** JavaScript/TypeScript, Vue 3 (likely Vite)
- **Database:** MySQL
- **IDE:** IntelliJ IDEA

## Architecture

Layered monolithic application with separate frontend and backend modules communicating via REST API.

**Backend** (`backend/src/main/java/com/yourorg/finance/`):
- `controller/` — REST API endpoints
- `service/` — Business logic
- `repository/` — Data access (Spring Data)
- `model/` — Entity classes
- `config/` — Spring configuration
- `util/` — Helper classes
- `resources/db/` — Database scripts

**Frontend** (`frontend/src/`):
- `views/` — Page-level components
- `components/` — Reusable UI components
- `router/` — Vue Router configuration
- `store/` — State management (Vuex/Pinia)
- `api/` — Backend API client modules
- `utils/` — Shared utilities

## Expected Build Commands

Once implemented, the project should use:

**Backend (Maven):**
```
cd backend
mvn clean install        # build
mvn spring-boot:run      # run
mvn test                 # run all tests
mvn test -Dtest=ClassName#methodName  # run single test
```

**Frontend (npm):**
```
cd frontend
npm install              # install dependencies
npm run dev              # development server
npm run build            # production build
npm run lint             # lint
npm test                 # run tests
```

## Branch Strategy

- `main` — baseline/production branch
- `AW`, `MAC` — parallel development branches for different team members
