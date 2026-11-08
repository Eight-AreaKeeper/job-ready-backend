# job-ready-backend

Spring Boot (Java) REST API for **job_ready** (Hack the Change 2026): auth, AI career profiles,
opportunity matching, resume generation and tailoring, deadline schedule, and mentor coffee chats.

The API follows the team's **API Docs Draft v0.1**: base path `/api/v1`, JSON everywhere,
`Authorization: Bearer <token>`. Every response is wrapped as
`{ "code": 1, "msg": null, "data": ... }` (code 0 + msg on errors, with the HTTP status still set);
lists are `data: { "items": [...], "total": n }`.

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

### Database (MySQL)

The app runs on MySQL. Create the database and tables once, then put your connection details in
`src/main/resources/application-dev.yml` (copy it from `application-dev.yml.example`; it is git-ignored,
so everyone keeps their own password):

```bash
mysql -uroot -p < db/schema.sql                       # creates job_ready_db and all tables
cp src/main/resources/application-dev.yml.example src/main/resources/application-dev.yml   # then edit the password
./mvnw spring-boot:run
```

The app never creates or changes tables, so `db/schema.sql` is the source of truth.
`./mvnw test` also runs against this MySQL database. Each test runs in a transaction that is rolled back,
so test data is not left behind (the demo user and sample opportunities are created on startup as usual).

## Configuration

| Env var | Default | What it does |
| --- | --- | --- |
| `PORT` | `8000` | HTTP port (docs say 8000, still to be confirmed) |
| `AUTH_MODE` | `jwt` | `jwt` = token required; `demo` = no token acts as the demo user |
| `JWT_SECRET` | dev value | HMAC secret, at least 32 chars. **Set this anywhere shared.** |
| `CORS_ORIGINS` | `http://localhost:3000,http://localhost:5173` | Allowed front-end origins |
| `AI_PROVIDER` | `stub` | Which `AiClient` implementation to use |
| `SPRING_PROFILES_ACTIVE` | `dev` | Which `application-<profile>.yml` holds the database settings |

## Layout

Layered like the 苍穹外卖 project: controller -> service (interface + impl) -> mapper (MyBatis).

```
src/main/java/com/jobready/
  controller/      REST endpoints, one per module, all return Result<T>
  service/         service interfaces
  service/impl/    business logic
  mapper/          MyBatis mapper interfaces (SQL in resources/mapper/*.xml or @Select/@Insert)
  pojo/entity/     one class per table (@Data @Builder)
  pojo/dto/        request bodies (validated with jakarta.validation)
  pojo/vo/         response shapes that differ from an entity
  interceptor/     JwtTokenInterceptor: checks the Bearer token, puts the user id in BaseContext
  handler/         GlobalExceptionHandler: every error -> Result.error(msg) + HTTP status
  config/          WebMvcConfiguration (interceptor, CORS, BCrypt), DataInitializer (demo user, seed data)
  ai/              AiClient interface + StubAiClient (offline, rule-based)
  common/
    result/        Result{code,msg,data}, ListResult{items,total}
    context/       BaseContext (current user id, ThreadLocal)
    exception/     BaseException.notFound / badRequest / forbidden / conflict ...
    json/          MyBatis type handlers for JSON columns
    constant/      status values, profile questions
    utils/         JwtUtil, IdUtil
    properties/    app.* settings
src/main/resources/
  mapper/*.xml     SQL
db/schema.sql      MySQL tables
```

Every response is `{ "code": 1, "msg": null, "data": ... }` on success and
`{ "code": 0, "msg": "...", "data": null }` on failure, with the HTTP status (400, 401, 403, 404, 409, 502, 500)
still set. List endpoints put `{ "items": [...], "total": n }` in `data`.

In a service, get the logged-in user with `BaseContext.getCurrentId()`, and throw
`BaseException.notFound("resume")` etc. to return an error.

IDs are prefixed strings (`u_`, `p_`, `o_`, `s_`, `r_`, `m_`, `c_`, `i_`), generated in the service before insert.
List/object fields (skills, requirements, resume `sections`, mentor `availability`, ...) are JSON columns;
in mapper XML pass them with a type handler, e.g. `#{skills,typeHandler=com.jobready.common.json.StringListTypeHandler}`,
and read them through the `resultMap`. When you change a table, update `db/schema.sql`,
the entity and its mapper XML together.

## Open decisions (kept pluggable)

- **AI provider**: undecided. Everything goes through `ai/AiClient`. `StubAiClient` returns
  deterministic results (skill/industry overlap for matching, template icebreakers, etc.).
  To add a provider, implement `AiClient` with
  `@ConditionalOnProperty(name = "app.ai.provider", havingValue = "<name>")` and set `AI_PROVIDER`.
  Map provider failures to `BaseException.aiFailed(...)` (HTTP 502).
- **Database**: MySQL (`db/schema.sql`).
- **Real auth vs demo user**: real register/login with BCrypt + JWT is implemented;
  `AUTH_MODE=demo` gives the demo-user shortcut. Pick one for the demo.
- **Opportunity data**: 5 clearly marked sample rows; replace with the 20-30 real ones.
- **Profile questions**: placeholders in `common/constant/ProfileQuestionConstant.java`.
- **Mentor points rules**: currently +10 per completed chat (`app.mentors.points-per-completed-chat`);
  `maxChatsPerWeek` is stored but not enforced yet.
- **Mock interview**: stretch goal. Each graded answer is saved in `interviews.answers`.

## Database

`db/schema.sql` is the team's schema.sql with these fixes:

- `profiles`: fixed missing commas, renamed `educational information` to `education`, gave `location` a type.
  Kept `experience_level` (beginner | student | intermediate | senior), `preferred_work_type`, `industries`
  and `updated_at`, which were commented out or dropped but are used by the API.
- `profile_answers` is gone: `POST /profile` sends the answers to the AI without storing them.
- `idx_schedule_user_deadline` renamed to `idx_schedule_user_status` (it indexes status; deadline lives on opportunities).
- `schedule_items`: unique (user_id, opportunity_id).
- `coffee_chats.scheduled_at` is `DATETIME` (stored in UTC) instead of `TIMESTAMP`, which ends in 2038.
- `interviews`: added `role` so an interview started with only a role keeps it.

`GET/POST/PATCH /profile` now also return and accept `education`, `projects`, `experience` and
`preferredWorkType`, and `POST /resumes/generate` copies education, projects and experience into the resume.

## Notes vs. the API draft

- Duplicate email on register returns `409 CONFLICT`; saving the same opportunity twice to the schedule also returns 409.
- `coffee_chats.mentor_id` stores the **mentor id** (`m_...`), not the mentor's user id.
- Mentor items in `GET /mentors` also include `userId`.
