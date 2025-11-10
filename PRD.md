# Product Requirements Document (PRD): InvoiceMe — Index

## Document Status

⚠️ **This document has been split into focused PRDs for better organization.**

Please refer to the following domain-specific PRDs for detailed requirements:

---

## PRD Structure

### [PRD-00-Master.md](./PRD-00-Master.md)
**Master PRD** - Project overview, timeline, dependencies, tech stack summary, and cross-cutting concerns.

### [PRD-01-Backend.md](./PRD-01-Backend.md)
**Backend PRD** - Domain modeling, CQRS implementation, REST API development, and authentication (Phases 2-5, 8 backend).

### [PRD-02-Frontend.md](./PRD-02-Frontend.md)
**Frontend PRD** - UI architecture, component development, and user experience (Phases 6-7, 8 frontend).

### [PRD-03-Testing.md](./PRD-03-Testing.md)
**Testing PRD** - Testing strategy, unit tests, integration tests, and quality assurance (Phase 9).

### [PRD-04-Infrastructure.md](./PRD-04-Infrastructure.md)
**Infrastructure PRD** - Infrastructure setup, deployment configuration, and DevOps (Phases 1, 10).

---

## Quick Reference

**Total Duration**: 20-30 days (1 developer, full-time)

**10 Implementation Phases**:
1. Project Foundation & Infrastructure Setup → **PRD-04**
2. Domain Model & Core Entities (DDD) → **PRD-01**
3. CQRS Commands Implementation → **PRD-01**
4. CQRS Queries Implementation → **PRD-01**
5. REST API Layer & Integration → **PRD-01**
6. Frontend Foundation & MVVM → **PRD-02**
7. Frontend Feature Implementation → **PRD-02**
8. Authentication & Authorization → **PRD-01** (backend) + **PRD-02** (frontend)
9. Integration Testing & QA → **PRD-03**
10. Performance Optimization & Deployment → **PRD-04**

---

## Legacy Content

The original monolithic PRD content has been preserved below for reference, but **please use the split PRDs above for implementation**.

---

## Recommended Tech Stack

### Backend
- **Framework**: Spring Boot 3.2+ (Java 17+)
- **Build Tool**: Maven or Gradle (Gradle recommended for flexibility)
- **Database**: 
  - **Development/Testing**: H2 Database (in-memory, fast iteration)
  - **Production**: PostgreSQL 15+
- **ORM**: Spring Data JPA with Hibernate
- **Validation**: Jakarta Bean Validation
- **Testing**: JUnit 5, Mockito, Testcontainers (for integration tests)

### Frontend
- **Framework**: Next.js 14+ (App Router) with TypeScript
- **State Management**: React Context API + React Query (TanStack Query) for server state
- **UI Library**: Tailwind CSS + shadcn/ui (or Radix UI primitives)
- **Form Handling**: React Hook Form + Zod validation
- **HTTP Client**: Axios or native fetch with typed API client
- **Testing**: Vitest + React Testing Library

### Infrastructure & DevOps
- **Containerization**: Docker + Docker Compose (for local development)
- **Cloud Platform**: AWS (EC2/ECS, RDS, S3) or Azure (App Service, Azure SQL, Blob Storage)
- **CI/CD**: GitHub Actions
- **API Documentation**: OpenAPI/Swagger (SpringDoc OpenAPI)

### Development Tools
- **AI Tools**: Cursor AI, GitHub Copilot, or ChatGPT for code generation assistance
- **Code Quality**: SonarLint, ESLint, Prettier
- **Version Control**: Git with conventional commits

---

## Implementation Phases

### Phase 1: Project Foundation & Infrastructure Setup
**Duration**: 1-2 days  
**Goal**: Establish project structure, build configuration, and development environment

**Deliverables**:
- Initialize Spring Boot project with Maven/Gradle
- Initialize Next.js project with TypeScript
- Configure Docker Compose for PostgreSQL (and H2 for quick dev)
- Set up project structure following Clean Architecture layers:
  ```
  backend/
    ├── src/main/java/
    │   └── com/invoiceme/
    │       ├── domain/          # Domain entities and value objects
    │       ├── application/     # Use cases (Commands/Queries)
    │       ├── infrastructure/  # Persistence, external services
    │       └── api/             # REST controllers
  frontend/
    ├── app/                     # Next.js App Router
    ├── components/              # React components
    ├── lib/                     # Utilities, API client
    └── types/                   # TypeScript types
  ```
- Configure database connection (H2 for dev, PostgreSQL for prod)
- Set up basic logging and error handling
- Create README with setup instructions

**Success Criteria**:
- Both backend and frontend projects start successfully
- Database connection established
- Docker Compose environment runs locally

---

### Phase 2: Domain Model & Core Entities (DDD Foundation)
**Duration**: 2-3 days  
**Goal**: Implement rich domain models with business logic

**Deliverables**:
- **Customer Domain Entity**:
  - Properties: id, name, email, address, createdAt, updatedAt
  - Domain methods: validate(), updateDetails()
- **Invoice Domain Entity**:
  - Properties: id, customerId, status (Draft/Sent/Paid), issueDate, dueDate, lineItems, payments
  - Domain methods: addLineItem(), removeLineItem(), calculateTotal(), markAsSent(), applyPayment(), calculateBalance()
  - Value Objects: LineItem (description, quantity, unitPrice, total)
- **Payment Domain Entity**:
  - Properties: id, invoiceId, amount, paymentDate, paymentMethod
  - Domain methods: validate()
- Implement domain events (optional but encouraged):
  - InvoiceCreatedEvent, InvoiceSentEvent, PaymentRecordedEvent
- Create domain exceptions (e.g., InvalidInvoiceStateException, InsufficientPaymentException)

**Success Criteria**:
- All domain entities are pure Java objects (no framework dependencies)
- Business logic is encapsulated within domain entities
- Unit tests for domain logic pass

---

### Phase 3: CQRS Commands Implementation (Write Operations)
**Duration**: 3-4 days  
**Goal**: Implement all write operations following CQRS and Vertical Slice Architecture

**Deliverables**:
- **Customer Commands** (Vertical Slices):
  - `CreateCustomerCommand` + `CreateCustomerHandler`
  - `UpdateCustomerCommand` + `UpdateCustomerHandler`
  - `DeleteCustomerCommand` + `DeleteCustomerHandler`
- **Invoice Commands**:
  - `CreateInvoiceCommand` + `CreateInvoiceHandler` (creates Draft invoice)
  - `UpdateInvoiceCommand` + `UpdateInvoiceHandler`
  - `MarkInvoiceAsSentCommand` + `MarkInvoiceAsSentHandler`
  - `AddLineItemCommand` + `AddLineItemHandler`
  - `RemoveLineItemCommand` + `RemoveLineItemHandler`
- **Payment Commands**:
  - `RecordPaymentCommand` + `RecordPaymentHandler` (applies payment to invoice, updates invoice balance)
- Implement Command DTOs and mappers
- Create repository interfaces in domain layer, implementations in infrastructure
- Implement transaction management and validation

**Success Criteria**:
- All commands execute successfully
- Invoice balance calculation is correct
- State transitions (Draft → Sent → Paid) are enforced
- Integration tests for command handlers pass

---

### Phase 4: CQRS Queries Implementation (Read Operations)
**Duration**: 2-3 days  
**Goal**: Implement all read operations with optimized query models

**Deliverables**:
- **Customer Queries**:
  - `GetCustomerByIdQuery` + `GetCustomerByIdHandler`
  - `ListAllCustomersQuery` + `ListAllCustomersHandler`
- **Invoice Queries**:
  - `GetInvoiceByIdQuery` + `GetInvoiceByIdHandler`
  - `ListInvoicesByStatusQuery` + `ListInvoicesByStatusHandler`
  - `ListInvoicesByCustomerQuery` + `ListInvoicesByCustomerHandler`
- **Payment Queries**:
  - `GetPaymentByIdQuery` + `GetPaymentByIdHandler`
  - `ListPaymentsByInvoiceQuery` + `ListPaymentsByInvoiceHandler`
- Create Query DTOs (read models) optimized for UI needs
- Implement query repositories (can use Spring Data JPA projections or custom queries)
- Add pagination support for list queries

**Success Criteria**:
- All queries return correct data
- Query response times < 200ms (local environment)
- Integration tests for query handlers pass

---

### Phase 5: REST API Layer & Integration
**Duration**: 2-3 days  
**Goal**: Expose CQRS operations via RESTful APIs

**Deliverables**:
- **Customer API Endpoints**:
  - `POST /api/customers` (CreateCustomerCommand)
  - `PUT /api/customers/{id}` (UpdateCustomerCommand)
  - `DELETE /api/customers/{id}` (DeleteCustomerCommand)
  - `GET /api/customers/{id}` (GetCustomerByIdQuery)
  - `GET /api/customers` (ListAllCustomersQuery)
- **Invoice API Endpoints**:
  - `POST /api/invoices` (CreateInvoiceCommand)
  - `PUT /api/invoices/{id}` (UpdateInvoiceCommand)
  - `POST /api/invoices/{id}/send` (MarkInvoiceAsSentCommand)
  - `POST /api/invoices/{id}/line-items` (AddLineItemCommand)
  - `DELETE /api/invoices/{id}/line-items/{lineItemId}` (RemoveLineItemCommand)
  - `GET /api/invoices/{id}` (GetInvoiceByIdQuery)
  - `GET /api/invoices?status={status}` (ListInvoicesByStatusQuery)
  - `GET /api/invoices?customerId={customerId}` (ListInvoicesByCustomerQuery)
- **Payment API Endpoints**:
  - `POST /api/payments` (RecordPaymentCommand)
  - `GET /api/payments/{id}` (GetPaymentByIdQuery)
  - `GET /api/invoices/{invoiceId}/payments` (ListPaymentsByInvoiceQuery)
- Implement global exception handling (@ControllerAdvice)
- Add API request/response DTOs with validation
- Configure CORS for frontend integration
- Generate OpenAPI/Swagger documentation
- Add API versioning (optional but recommended)

**Success Criteria**:
- All endpoints return correct HTTP status codes
- Request validation works (400 for invalid input)
- API documentation is accessible via Swagger UI
- Integration tests for all endpoints pass

---

### Phase 6: Frontend Foundation & MVVM Architecture
**Duration**: 2-3 days  
**Goal**: Set up frontend architecture following MVVM principles

**Deliverables**:
- Configure Next.js App Router structure
- Set up Tailwind CSS and UI component library (shadcn/ui)
- Create typed API client (using Axios or fetch) with error handling
- Implement ViewModels/Service layer:
  - `CustomerService` (API calls for customer operations)
  - `InvoiceService` (API calls for invoice operations)
  - `PaymentService` (API calls for payment operations)
- Set up React Query for server state management
- Create reusable UI components:
  - Button, Input, Select, Table, Modal/Dialog
  - Loading states, Error boundaries
- Implement routing structure:
  - `/` (Dashboard/Landing)
  - `/customers` (Customer list)
  - `/invoices` (Invoice list)
  - `/payments` (Payment list)

**Success Criteria**:
- Frontend connects to backend API successfully
- Basic UI components render correctly
- Navigation between routes works
- Error handling displays user-friendly messages

---

### Phase 7: Frontend Feature Implementation
**Duration**: 4-5 days  
**Goal**: Build complete UI for all business operations

**Deliverables**:
- **Customer Management UI**:
  - Customer list page with table view
  - Create customer form (modal or page)
  - Edit customer form
  - Delete confirmation dialog
- **Invoice Management UI**:
  - Invoice list page with filtering by status/customer
  - Create invoice page (Draft state):
    - Customer selection
    - Dynamic line items table (add/remove rows)
    - Real-time total calculation
  - Invoice detail/view page:
    - Display invoice information
    - Show line items table
    - Show payment history
    - "Mark as Sent" button (if Draft)
    - Current balance display
  - Edit invoice page (if Draft)
- **Payment Management UI**:
  - Record payment form (linked to invoice)
  - Payment list/history view
- Implement form validation (client-side with Zod)
- Add loading states and optimistic updates
- Implement responsive design (mobile-friendly)

**Success Criteria**:
- All CRUD operations work from UI
- Invoice creation with multiple line items works
- Payment recording updates invoice balance correctly
- UI is responsive and user-friendly
- No noticeable lag in interactions

---

### Phase 8: Authentication & Authorization
**Duration**: 2-3 days  
**Goal**: Secure the application with basic authentication

**Deliverables**:
- **Backend**:
  - Implement Spring Security with JWT or session-based authentication
  - Create `User` domain entity and repository
  - Add login endpoint: `POST /api/auth/login`
  - Add registration endpoint: `POST /api/auth/register` (optional)
  - Secure all API endpoints (require authentication)
  - Implement password hashing (BCrypt)
- **Frontend**:
  - Create login page (`/login`)
  - Implement authentication context/state management
  - Add protected route wrapper
  - Store auth token (localStorage or httpOnly cookie)
  - Add logout functionality
  - Redirect unauthenticated users to login

**Success Criteria**:
- Unauthenticated users cannot access protected pages
- Login/logout flow works correctly
- API calls include authentication tokens
- Session persists across page refreshes

---

### Phase 9: Integration Testing & Quality Assurance
**Duration**: 3-4 days  
**Goal**: Ensure system reliability and correctness

**Deliverables**:
- **Backend Integration Tests**:
  - Test complete Customer lifecycle (create → update → delete)
  - Test complete Invoice lifecycle (create Draft → add line items → mark as Sent → record Payment → Paid)
  - Test invoice balance calculation accuracy
  - Test payment application logic
  - Test state transition validations
  - Use Testcontainers for database integration tests
- **Frontend Integration Tests**:
  - Test customer creation flow (E2E or component integration)
  - Test invoice creation with line items
  - Test payment recording flow
  - Test authentication flow
- **API Integration Tests**:
  - Test all endpoints with various scenarios (success, validation errors, not found, etc.)
- Fix any bugs discovered during testing
- Achieve minimum 70% code coverage (aim for higher)

**Success Criteria**:
- All integration tests pass
- Critical business flows are fully tested
- No critical bugs remain
- Code coverage meets minimum threshold

---

### Phase 10: Performance Optimization & Deployment Preparation
**Duration**: 2-3 days  
**Goal**: Optimize performance and prepare for production deployment

**Deliverables**:
- **Performance Optimization**:
  - Profile API endpoints, optimize slow queries
  - Add database indexes (customerId, invoiceId, status, etc.)
  - Implement pagination for large datasets
  - Optimize frontend bundle size (code splitting, lazy loading)
  - Verify API latency < 200ms for standard operations
  - Optimize React rendering (memoization where needed)
- **Deployment Configuration**:
  - Create Dockerfiles for backend and frontend
  - Set up Docker Compose for production-like environment
  - Configure environment variables (dev, staging, prod)
  - Set up database migration strategy (Flyway or Liquibase)
  - Create deployment scripts/documentation
  - Configure logging and monitoring (optional but recommended)
- **Documentation**:
  - Update README with deployment instructions
  - Document API endpoints
  - Create architecture diagram
  - Document AI tool usage and prompts used

**Success Criteria**:
- All API endpoints meet < 200ms latency requirement
- Application runs successfully in Docker containers
- Deployment documentation is complete
- System is ready for cloud deployment (AWS/Azure)

---

## Phase Dependencies & Critical Path

```
Phase 1 (Foundation)
    ↓
Phase 2 (Domain Model)
    ↓
Phase 3 (Commands) ──┐
    ↓                │
Phase 4 (Queries)    │
    ↓                │
Phase 5 (API) ───────┘
    ↓
Phase 6 (Frontend Foundation)
    ↓
Phase 7 (Frontend Features) ← Phase 8 (Auth) can run in parallel
    ↓
Phase 9 (Testing)
    ↓
Phase 10 (Optimization & Deployment)
```

**Parallel Opportunities**:
- Phase 8 (Authentication) can be developed in parallel with Phase 7 (Frontend Features) if team resources allow
- Some frontend components can be built while backend API is being finalized (using mock data initially)

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

## Risk Mitigation

1. **Complexity Risk**: Start with simple implementations, refactor as needed. Use AI tools for boilerplate, but maintain architectural control.
2. **Performance Risk**: Profile early (Phase 5), optimize incrementally. Use database indexes from the start.
3. **Integration Risk**: Test API contracts early. Use OpenAPI/Swagger for frontend-backend contract.
4. **Time Risk**: Prioritize core functionality (Phases 1-7). Authentication and optimization can be simplified if time-constrained.

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

## Timeline Estimate

**Total Duration**: 20-30 days (assuming 1 developer, full-time)

- Phase 1: 1-2 days
- Phase 2: 2-3 days
- Phase 3: 3-4 days
- Phase 4: 2-3 days
- Phase 5: 2-3 days
- Phase 6: 2-3 days
- Phase 7: 4-5 days
- Phase 8: 2-3 days
- Phase 9: 3-4 days
- Phase 10: 2-3 days

**Buffer**: Add 20% buffer for unexpected issues and refinements.

---

## Next Steps

1. Review and approve this PRD
2. Set up development environment (Phase 1)
3. Begin implementation following the phased approach
4. Conduct regular checkpoints after each phase
5. Document AI tool usage and architectural decisions throughout

---

## Appendix: Database Schema (Proposed)

```sql
-- Customers
CREATE TABLE customers (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    address TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Invoices
CREATE TABLE invoices (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL REFERENCES customers(id),
    status VARCHAR(20) NOT NULL CHECK (status IN ('DRAFT', 'SENT', 'PAID')),
    issue_date DATE NOT NULL,
    due_date DATE,
    total_amount DECIMAL(10, 2) NOT NULL,
    balance DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Invoice Line Items
CREATE TABLE invoice_line_items (
    id UUID PRIMARY KEY,
    invoice_id UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    description TEXT NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    line_order INTEGER NOT NULL
);

-- Payments
CREATE TABLE payments (
    id UUID PRIMARY KEY,
    invoice_id UUID NOT NULL REFERENCES invoices(id),
    amount DECIMAL(10, 2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_method VARCHAR(50),
    created_at TIMESTAMP NOT NULL
);

-- Users (for authentication)
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- Indexes
CREATE INDEX idx_invoices_customer_id ON invoices(customer_id);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_payments_invoice_id ON payments(invoice_id);
CREATE INDEX idx_invoice_line_items_invoice_id ON invoice_line_items(invoice_id);
```

---

**Document Version**: 1.0  
**Last Updated**: [Current Date]  
**Author**: Development Team

