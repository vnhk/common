# common - Project Notes

> **IMPORTANT**: Keep this file updated when making significant changes to the codebase. This file serves as persistent memory between Claude Code sessions.

## Overview
Shared library used by all modules. Contains base infrastructure: services, repositories, security, search, async tasks, logging, entity config API, and shared utilities. No UI framework.

## Key Packages

### `com.bervan.common`
- `BervanBaseRepositoryImpl` — auto-generates UUID on save if `id == null`
- `BaseService<ID, Entity>` — base CRUD service with `@PostFilter` security
- `BaseOwnedController<Entity, ID>` — base REST controller with full CRUD; use for owned entities (`BervanOwnedBaseEntity`)
- `BervanDTOMapper` — automatic DTO ↔ Model mapping via reflection; supports `@FieldMapperConfig`, `@PreCustomMappers`, `@PostCustomMappers`
- `SearchService` — full-text search infrastructure

### `com.bervan.common.user`
- `User` — the user entity; `UserRepository`, `UserService`

### `com.bervan.asynctask`
- `AsyncTask`, `HistoryAsyncTask` — async task tracking entities
- `AsyncTaskService`, `HistoryAsyncTaskService`
- `AsyncTaskRestController` — `GET /api/async/async-tasks` (paginated), `GET /api/async/async-tasks/{id}`, `GET /api/async/async-tasks/{id}/history`

### `com.bervan.logging`
- `LogEntity`, `LogListener`, `QueueAppender` — logback-based log capture to DB
- `LogRestController` — `GET /api/logs`, `/api/logs/trackers`, `/api/logs/app-names` etc.

### `com.bervan.lowcode`
- `LowCodeClass`, `LowCodeClassDetails` — low-code generator entities

### `com.bervan.encryption`
- `EncryptionService` — AES encryption for pocket items and other sensitive data
- `DataCipherException`

### `com.bervan.entityconfig` (entity config API)
- Reads `src/main/resources/autoconfig/*.yml` files and serves them via `GET /api/config`
- React `DynamicForm` and `buildColumnsFromConfig` consume this API
- YML fields: `name`, `displayName`, `type`, `required`, `inSaveForm`, `inEditForm`, `strValues`, `extension`

## Entity Config YML (`autoconfig/*.yml`)
Each module defines column/field metadata used by both the entity config API and React DynamicForm:
```yaml
fields:
  - name: fieldName
    displayName: "Display Name"
    type: STRING          # STRING | NUMBER | DATE | BOOLEAN
    required: true
    inSaveForm: true
    inEditForm: true
    strValues: ["A", "B"] # for dropdowns
```

## Important Notes
1. `BervanBaseRepositoryImpl.save()` auto-generates UUID — entities persisted via JPA CASCADE must set `id` manually
2. `BaseOwnedController` uses `@PostFilter` — only works for `BervanOwnedBaseEntity`; entities extending `BervanBaseEntity` need plain REST controllers
3. `spring-boot-starter-parent` version: 3.2.8
4. Dependencies: spring-security, spring-data-jpa, spring-amqp, poi-ooxml, logback, jackson-dataformat-yaml, spring-validation
