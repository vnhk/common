# common

Shared infrastructure library used by all `my-tools` modules. Contains base services, repositories, security, async task tracking, logging, entity config API, and encryption utilities. No UI framework dependency.

## Key Packages

| Package | Contents |
|---------|----------|
| `com.bervan.common` | `BaseService`, `BaseOwnedController`, `BervanDTOMapper`, `SearchService` |
| `com.bervan.common.user` | `User`, `UserRepository`, `UserService` |
| `com.bervan.asynctask` | Async task tracking entities + REST (`GET /api/async/async-tasks`) |
| `com.bervan.logging` | Logback-based log capture to DB + REST (`GET /api/logs`) |
| `com.bervan.encryption` | AES encryption (`EncryptionService`) for sensitive data |
| `com.bervan.entityconfig` | Reads `autoconfig/*.yml` and serves via `GET /api/config` |
| `com.bervan.lowcode` | Low-code generator entities |

## Entity Config YML

Each module defines field metadata in `src/main/resources/autoconfig/*.yml`:

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

The React `DynamicForm` and `buildColumnsFromConfig` consume `GET /api/config`.

## Important Notes

- `BervanBaseRepositoryImpl.save()` auto-generates UUID — entities persisted via JPA `CASCADE` must set `id` manually
- `BaseOwnedController` only works for `BervanOwnedBaseEntity`; plain entities need plain REST controllers
- Spring Boot parent: `3.0.4`
- Key dependencies: Spring Security, Spring Data JPA, Spring AMQP, Apache POI, Logback, Jackson YAML

## Build

```bash
mvn clean install -DskipTests
```

Must be built before all other modules.
