# Master PRD: InvoiceMe — Project Overview

## Document Purpose

This master PRD provides a high-level overview of the InvoiceMe project, including overall architecture, timeline, dependencies, and cross-cutting concerns. For detailed implementation requirements, refer to the domain-specific PRDs:

- **PRD-01-Backend.md**: Backend implementation (Domain, CQRS, API)
- **PRD-02-Frontend.md**: Frontend implementation (UI, MVVM)
- **PRD-03-Testing.md**: Testing strategy and quality assurance
- **PRD-04-Infrastructure.md**: Infrastructure, deployment, and DevOps

---

## Executive Summary

InvoiceMe is an AI-assisted full-stack ERP invoicing system designed to demonstrate mastery of modern software architecture principles (Domain-Driven Design, CQRS, Vertical Slice Architecture) alongside intelligent use of AI-assisted development tools.

The project implements core business domains: **Customers**, **Invoices**, and **Payments**, with a focus on architectural clarity, separation of concerns, and production-quality code.

---

## Recommended Tech Stack

### Backend
- **Framework**: Spring Boot 3.2+ (Java 17+)
- **Build Tool**: Maven or Gradle (Gradle recommended)
- **Database**: 
  - **Development/Testing**: H2 Database (in-memory)
  - **Production**: PostgreSQL 15+
- **ORM**: Spring Data JPA with Hibernate
- **Validation**: Jakarta Bean Validation
- **Testing**: JUnit 5, Mockito, Testcontainers

### Frontend
- **Framework**: Next.js 14+ (App Router) with TypeScript
- **State Management**: React Context API + React Query (TanStack Query)
- **UI Library**: Tailwind CSS + shadcn/ui
- **Form Handling**: React Hook Form + Zod validation
- **HTTP Client**: Axios or native fetch with typed API client
- **Testing**: Vitest + React Testing Library

### Infrastructure & DevOps
- **Containerization**: Docker + Docker Compose
- **Cloud Platform**: AWS (EC2/ECS, RDS, S3) or Azure (App Service, Azure SQL, Blob Storage)
- **CI/CD**: GitHub Actions
- **API Documentation**: OpenAPI/Swagger (SpringDoc OpenAPI)
- **API Mocking**: MSW (Mock Service Worker) or json-server for frontend development

### Development Tools
- **AI Tools**: Cursor AI, GitHub Copilot, or ChatGPT
- **Code Quality**: SonarLint, ESLint, Prettier
- **Version Control**: Git with conventional commits

---

## Implementation Phases Overview

The project is organized into 10 phases that can be executed in parallel where dependencies allow:

| Phase | Name | Duration | PRD Reference | Can Parallel? |
|-------|------|----------|---------------|---------------|
| 1 | Project Foundation & Infrastructure Setup | 1-2 days | PRD-04-Infrastructure.md | No (foundation) |
| 2 | Domain Model & Core Entities (DDD) | 2-3 days | PRD-01-Backend.md | No (backend foundation) |
| 3 | CQRS Commands Implementation | 3-4 days | PRD-01-Backend.md | No (depends on Phase 2) |
| 4 | CQRS Queries Implementation | 2-3 days | PRD-01-Backend.md | No (depends on Phase 2) |
| 4.5 | **API Contract Definition** | 0.5-1 day | PRD-01-Backend.md | **Yes** (after Phase 4) |
| 5 | REST API Layer & Integration | 2-3 days | PRD-01-Backend.md | **Yes** (with Phase 6-7) |
| 6 | Frontend Foundation & MVVM | 2-3 days | PRD-02-Frontend.md | **Yes** (after Phase 1, with mocks) |
| 7 | Frontend Feature Implementation | 4-5 days | PRD-02-Frontend.md | **Yes** (after Phase 4.5, with mocks) |
| 8 | Authentication & Authorization | 2-3 days | PRD-01-Backend.md, PRD-02-Frontend.md | **Yes** (can parallel backend/frontend) |
| 9 | Integration Testing & QA | 3-4 days | PRD-03-Testing.md | No (requires integration) |
| 10 | Performance Optimization & Deployment | 2-3 days | PRD-04-Infrastructure.md | No (final phase) |

**Total Duration**:
- **Sequential (1 developer)**: 20-30 days
- **Parallel (2 developers)**: 15-20 days (backend + frontend teams)
- **Buffer**: Add 20% for unexpected issues

**Note**: Phase 4.5 (API Contract Definition) is a critical milestone that enables parallel development.

---

## Phase Dependencies & Critical Path

### Sequential Path (Single Developer)
```
Phase 1 (Foundation)
    ↓
Phase 2 (Domain Model)
    ↓
Phase 3 (Commands) ──┐
    ↓                │
Phase 4 (Queries)    │
    ↓                │
Phase 4.5 (API Contracts) ← Critical milestone
    ↓                │
Phase 5 (API) ───────┘
    ↓
Phase 6 (Frontend Foundation)
    ↓
Phase 7 (Frontend Features)
    ↓
Phase 8 (Authentication)
    ↓
Phase 9 (Testing)
    ↓
Phase 10 (Optimization & Deployment)
```

### Parallel Development Path (Recommended for Teams)
```
Phase 1 (Foundation) - Both teams
    ↓
┌─────────────────────────────────────────────┐
│                                             │
│  BACKEND TRACK                              │
│  Phase 2 (Domain Model)                     │
│    ↓                                        │
│  Phase 3 (Commands)                         │
│    ↓                                        │
│  Phase 4 (Queries)                          │
│    ↓                                        │
│  Phase 4.5 (API Contracts) ←───────────────┼─── API Spec Ready
│    ↓                                        │
│  Phase 5 (API Implementation)               │
│                                             │
└─────────────────────────────────────────────┘
    │
    │ Phase 4.5 (API Contracts)
    │
┌─────────────────────────────────────────────┐
│                                             │
│  FRONTEND TRACK                             │
│  Phase 6 (Foundation) ← Can start after    │
│    Phase 1 with mocks                       │
│    ↓                                        │
│  Phase 7 (Features) ← Can start after      │
│    Phase 4.5 with API mocks                 │
│                                             │
└─────────────────────────────────────────────┘
    │
    ↓
Phase 8 (Authentication) - Both teams in parallel
    ↓
Phase 9 (Integration Testing)
    ↓
Phase 10 (Optimization & Deployment)
```

**Parallel Development Strategy**:
1. **Phase 6 (Frontend Foundation)** can start immediately after Phase 1 using mock data
2. **Phase 4.5 (API Contract Definition)** is a critical milestone that enables frontend work
3. **Phase 7 (Frontend Features)** can run in parallel with Phase 5 (API Implementation) using API mocks
4. **Phase 8 (Authentication)** can have backend and frontend work in parallel
5. **Weekly integration checkpoints** ensure alignment between teams

---

## Architectural Principles (Mandatory)

### Domain-Driven Design (DDD)
- Model core entities (Customer, Invoice, Payment) as rich Domain Objects with business logic
- Use Value Objects for complex concepts (e.g., LineItem, Money)
- Implement Domain Events for cross-aggregate communication (optional but encouraged)
- Maintain clear Bounded Contexts

### Command Query Responsibility Segregation (CQRS)
- Separate write operations (Commands) from read operations (Queries)
- Use different models for commands and queries
- Commands modify state; Queries return data without side effects

### Vertical Slice Architecture (VSA)
- Organize code around features/use cases rather than technical layers
- Each vertical slice contains all layers (Domain, Application, Infrastructure, API)
- Example: `customers/create/` contains CreateCustomerCommand, Handler, Controller, Repository

### Clean Architecture
- Maintain clear boundaries between layers:
  - **Domain**: Pure business logic, no framework dependencies
  - **Application**: Use cases, orchestration (Commands/Queries)
  - **Infrastructure**: Persistence, external services
  - **API**: REST controllers, DTOs

---

## Success Metrics

### Functional Requirements
- ✅ All CRUD operations work for Customers, Invoices, and Payments
- ✅ Invoice lifecycle (Draft → Sent → Paid) is correctly implemented
- ✅ Invoice balance calculation is accurate
- ✅ Payment application logic works correctly
- ✅ Authentication secures the application

### Non-Functional Requirements
- ✅ API response times < 200ms (local environment)
- ✅ UI is responsive and smooth
- ✅ Integration tests cover critical flows
- ✅ Code follows DDD, CQRS, and VSA principles
- ✅ Clean Architecture layer separation is maintained

### Architectural Requirements
- ✅ Domain entities contain business logic (no anemic domain model)
- ✅ Commands and Queries are clearly separated
- ✅ Vertical slices are organized by feature/use case
- ✅ DTOs are used for API boundaries
- ✅ Repository pattern isolates persistence concerns

---

## Parallel Development Guidelines

### API Contract-First Development

**Phase 4.5: API Contract Definition** (Critical Milestone)

After Phase 4 (Queries), define API contracts before implementation:

1. **OpenAPI/Swagger Specification**:
   - Define all endpoints (request/response schemas)
   - Document error responses
   - Include authentication requirements
   - Version: `/api/v1/`

2. **Shared Type Definitions**:
   - Generate TypeScript types from OpenAPI spec
   - Share DTO definitions between backend and frontend
   - Ensure type safety across the stack

3. **Mock API Server**:
   - Set up MSW (Mock Service Worker) or json-server
   - Use OpenAPI spec to generate mock responses
   - Frontend can develop against mocks while backend implements

4. **Contract Validation**:
   - Use contract testing (Pact or similar) to ensure compliance
   - Validate API implementation matches contract
   - Catch breaking changes early

### Coordination Requirements

**Daily Standups** (if multiple developers):
- Share API contract changes
- Discuss integration blockers
- Coordinate breaking changes

**Weekly Integration Checkpoints**:
- Integrate frontend with real backend API
- Test end-to-end flows
- Identify and resolve integration issues early
- Update API contracts if needed

**Communication Protocol**:
- Version control API contracts (OpenAPI spec in repo)
- Document breaking changes in API contracts
- Use feature flags for incomplete features
- Maintain staging environment for integration testing

### Mock Data Strategy

**Frontend Development with Mocks**:
- Use MSW (Mock Service Worker) for API mocking
- Generate mocks from OpenAPI spec
- Test error scenarios (400, 404, 500 responses)
- Switch to real API when backend is ready

**Benefits**:
- Frontend development doesn't block on backend
- Early UI/UX validation
- Better API design (frontend needs inform API)
- Faster overall delivery

---

## Risk Mitigation

1. **Complexity Risk**: Start with simple implementations, refactor as needed. Use AI tools for boilerplate, but maintain architectural control.
2. **Performance Risk**: Profile early (Phase 5), optimize incrementally. Use database indexes from the start.
3. **Integration Risk**: 
   - Use API contract-first development (Phase 4.5)
   - Test API contracts early with OpenAPI/Swagger
   - Weekly integration checkpoints
   - Contract testing to ensure compliance
4. **Time Risk**: Prioritize core functionality (Phases 1-7). Authentication and optimization can be simplified if time-constrained.
5. **Parallel Development Risk**: 
   - Maintain clear API contracts
   - Regular communication between teams
   - Early integration testing
   - Version control for API specifications

---

## AI Tool Usage Guidelines

### Recommended AI Tools
- **Cursor AI** or **GitHub Copilot**: For code generation, refactoring, and boilerplate
- **ChatGPT/Claude**: For architectural guidance, debugging, and code review

### Best Practices
1. **Use AI for**: Boilerplate code, repetitive patterns, unit test generation, documentation
2. **Avoid AI for**: Architectural decisions, domain logic design, critical business rules (review carefully)
3. **Always**: Review AI-generated code, ensure it follows project architecture, test thoroughly
4. **Document**: Keep a log of AI prompts used and their effectiveness

### Example AI Prompts
- "Generate a Spring Boot REST controller for Customer following CQRS pattern with Command/Query handlers"
- "Create a React component for Invoice line items table with add/remove functionality using TypeScript"
- "Write integration tests for Invoice creation and payment application flow using Testcontainers"

---

## Project Structure

```
InvoiceMe/
├── backend/
│   ├── src/main/java/com/invoiceme/
│   │   ├── domain/              # Domain entities and value objects
│   │   ├── application/         # Use cases (Commands/Queries)
│   │   │   ├── customers/
│   │   │   ├── invoices/
│   │   │   └── payments/
│   │   ├── infrastructure/      # Persistence, external services
│   │   └── api/                 # REST controllers
│   └── src/test/
├── frontend/
│   ├── app/                     # Next.js App Router
│   ├── components/              # React components
│   ├── lib/                     # Utilities, API client
│   └── types/                   # TypeScript types
├── docs/                        # Documentation
├── docker-compose.yml           # Local development environment
└── README.md
```

---

## Database Schema Overview

See **PRD-01-Backend.md** for detailed schema. Core tables:
- `customers` - Customer information
- `invoices` - Invoice headers with status
- `invoice_line_items` - Invoice line items
- `payments` - Payment records
- `users` - Authentication users

---

## Development Workflow

### For Single Developer (Sequential)
1. Follow phases 1-10 sequentially
2. Use API contract-first approach (Phase 4.5) even for solo development
3. Develop frontend with mocks, then integrate with real API

### For Team Development (Parallel)
1. **Week 1**: Phase 1 (both teams), Phase 2-4 (backend), Phase 6 (frontend with mocks)
2. **Week 2**: Phase 4.5 (API contracts), Phase 5 (backend), Phase 7 (frontend with mocks)
3. **Week 3**: Phase 8 (both teams), Phase 9 (integration testing)
4. **Week 4**: Phase 10 (deployment), buffer time

**Integration Schedule**:
- **End of Week 1**: Initial integration checkpoint
- **End of Week 2**: Full API integration
- **End of Week 3**: Final integration testing

---

## Next Steps

1. Review and approve this Master PRD
2. Review domain-specific PRDs (Backend, Frontend, Testing, Infrastructure)
3. Decide on development approach (sequential vs. parallel)
4. Set up development environment (Phase 1)
5. If parallel: Set up API contract workflow (OpenAPI spec, mock server)
6. Begin implementation following the phased approach
7. Conduct regular checkpoints (daily for teams, weekly for solo)
8. Document AI tool usage and architectural decisions throughout

---

## Document References

- **PRD-01-Backend.md**: Backend implementation details (Phases 2-5, 8 backend)
- **PRD-02-Frontend.md**: Frontend implementation details (Phases 6-7, 8 frontend)
- **PRD-03-Testing.md**: Testing strategy and QA (Phase 9)
- **PRD-04-Infrastructure.md**: Infrastructure and deployment (Phases 1, 10)

---

---

## Appendix: API Contract-First Workflow

### Step-by-Step Process

1. **After Phase 4 (Queries)**:
   - Backend team defines all API endpoints
   - Create OpenAPI/Swagger specification
   - Document request/response schemas
   - Define error responses

2. **Generate TypeScript Types**:
   ```bash
   # Use openapi-typescript or similar
   npx openapi-typescript api-spec.yaml -o frontend/types/api.ts
   ```

3. **Set Up Mock Server**:
   ```bash
   # Using MSW
   npm install msw --save-dev
   # Generate mocks from OpenAPI spec
   ```

4. **Frontend Development**:
   - Use generated types for type safety
   - Develop against mock API
   - Test error scenarios

5. **Backend Implementation**:
   - Implement API endpoints matching contract
   - Validate against OpenAPI spec
   - Update contract if changes needed (communicate!)

6. **Integration**:
   - Switch frontend from mocks to real API
   - Test end-to-end flows
   - Fix integration issues

### Tools for API Contract-First

- **OpenAPI Generator**: Generate client SDKs, server stubs
- **MSW**: Mock Service Worker for frontend mocking
- **Pact**: Contract testing framework
- **Swagger UI**: Interactive API documentation
- **Postman/Insomnia**: API testing and documentation

---

**Document Version**: 1.1  
**Last Updated**: [Current Date]  
**Author**: Development Team  
**Changes**: Added parallel development strategy and API contract-first workflow

