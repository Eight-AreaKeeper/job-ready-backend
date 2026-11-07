# job-ready-backend

Spring Boot (Java) REST API for **job_ready** (Hack the Change 2026): auth, AI career profiles,
opportunity matching, resume generation and tailoring, deadline schedule, and mentor coffee chats.

The API follows the team's **API Docs Draft v0.1**: base path `/api/v1`, JSON everywhere,
`Authorization: Bearer <token>`, lists as `{ "items": [...], "total": n }`, errors as
`{ "error": { "code": "...", "message": "..." } }`.

## Run it

Needs Java 17+ (Maven is bundled via the wrapper).

```bash
./mvnw spring-boot:run          # http://localhost:8000/api/v1
./mvnw test                     # smoke tests for the main flows
```

On startup the app creates a demo student (`demo@jobready.dev` / `demo1234`) and loads 5 sample
opportunities from `src/main/resources/seed/opportunities.json`.

Front-end shortcut: `AUTH_MODE=demo ./mvnw spring-boot:run` lets requests without a token act as the
demo student, so you can call any endpoint without logging in.

Quick check:

```bash
curl -s -X POST localhost:8000/api/v1/auth/login -H 'Content-Type: application/json' \
  -d '{"email":"demo@jobready.dev","password":"demo1234"}'
```

H2 console (default in-memory DB): http://localhost:8000/h2-console, JDBC URL `jdbc:h2:mem:jobready`, user `sa`.

## Configuration

| Env var | Default | What it does |
| --- | --- | --- |
| `PORT` | `8000` | HTTP port (docs say 8000, still to be confirmed) |
| `AUTH_MODE` | `jwt` | `jwt` = token required; `demo` = no token acts as the demo user |
| `JWT_SECRET` | dev value | HMAC secret, at least 32 chars. **Set this anywhere shared.** |
| `CORS_ORIGINS` | `http://localhost:3000,http://localhost:5173` | Allowed front-end origins |
| `AI_PROVIDER` | `stub` | Which `AiClient` implementation to use |
| `SPRING_PROFILES_ACTIVE` | (none) | `postgres` switches to PostgreSQL (`DB_URL`, `DB_USER`, `DB_PASSWORD`) |

## Layout

```
src/main/java/com/jobready/
  common/        ApiException, error/list response shapes, global error handler, id generator
  security/      JWT issue/verify, Bearer filter, Spring Security config, app.* properties
  ai/            AiClient interface + StubAiClient (offline, rule-based)
  config/        demo user bootstrap
  auth/          POST /auth/register, /auth/login
  users/         GET /users/me
  profile/       GET /profile/questions, POST|GET|PATCH /profile
  opportunities/ GET /opportunities, /opportunities/matches, /opportunities/{id} (+ seed loader)
  schedule/      GET|POST /schedule, PATCH|DELETE /schedule/{id}
  resumes/       POST /resumes/generate, /{id}/tailor, /{id}/suggestions; GET /resumes, /resumes/{id}
  mentors/       POST|GET /mentors
  coffeechats/   POST /coffee-chats/icebreaker, POST|GET /coffee-chats, PATCH /coffee-chats/{id}
  interviews/    POST /interviews, /interviews/{id}/answers (TBN, stretch goal)
```

Each module is `Entity` + `Repository` + `Dtos` + `Service` + `Controller`. Controllers get the
logged-in user with `@AuthenticationPrincipal String userId`. Throw `ApiException.notFound(...)`,
`.badRequest(...)`, `.forbidden(...)`, `.aiFailed(...)` etc. to return the documented error body.

IDs are prefixed strings (`u_`, `p_`, `o_`, `s_`, `r_`, `m_`, `c_`, `i_`). List/JSON columns
(skills, requirements, resume `sections`, mentor `availability`, ...) are stored as JSON columns.
The schema is created by Hibernate (`ddl-auto: update`); add Flyway once the schema settles.

## Open decisions (kept pluggable)

- **AI provider**: undecided. Everything goes through `ai/AiClient`. `StubAiClient` returns
  deterministic results (skill/industry overlap for matching, template icebreakers, etc.).
  To add a provider, implement `AiClient` with
  `@ConditionalOnProperty(name = "app.ai.provider", havingValue = "<name>")` and set `AI_PROVIDER`.
  Map provider failures to `ApiException.aiFailed(...)` (HTTP 502).
- **Database**: H2 in-memory by default (zero setup); PostgreSQL ready via the `postgres` profile.
- **Real auth vs demo user**: real register/login with BCrypt + JWT is implemented;
  `AUTH_MODE=demo` gives the demo-user shortcut. Pick one for the demo.
- **Opportunity data**: 5 clearly marked sample rows; replace with the 20-30 real ones.
- **Profile questions**: placeholders in `profile/ProfileQuestions.java`.
- **Mentor points rules**: currently +10 per completed chat (`app.mentors.points-per-completed-chat`);
  `maxChatsPerWeek` is stored but not enforced yet.
- **Mock interview**: stretch goal; `interviews` table isn't in the draft table list yet.

## Notes vs. the API draft

- Duplicate email on register returns `409 CONFLICT`; saving the same opportunity twice to the schedule also returns 409.
- `coffee_chats.mentor_id` stores the **mentor id** (`m_...`), not the mentor's user id.
- Mentor items in `GET /mentors` also include `userId`.
