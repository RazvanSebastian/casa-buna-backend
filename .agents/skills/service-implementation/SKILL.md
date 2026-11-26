---
name: service-implementation
description: Implement Spring service methods in Casa Buna Backend. Use for transaction boundaries, DTO mapping, managed updates, business validation, authorization, and write failure handling.
---

# Service implementation

Source references are relative to `src/main/java/ro/casabuna/casabuna_backend`.

- `service/admin/TeamAdministrationServiceImpl#create` shows transactional creation and DTO mapping.
- `service/manager/TeamManagementServiceImpl#findUnassignedUsers` maps a repository `Page` in a read-only transaction and returns `PagedModel`.
- `service/manager/TeamManagementServiceImpl#assignChildToVolunteer` checks team membership before persisting references.

## Transactions and DTOs

- Use `@Transactional(readOnly = true)` for query entry points and `@Transactional` for writes. A class-level read-only default can have method-level write overrides. Keep repository access and DTO mapping within the boundary. In Spring proxy mode, self-invocation does not apply the callee's transaction annotation.
- Use record DTOs under `model/dto` and mappers under `model/mapper`. On creation, map, persist, then map the response. For partial updates, load the managed entity with the associations needed, apply an `@MappingTarget` mapper, preserve omitted-field and explicit-null semantics, and let dirty checking flush the changes.
- Delegate shared domain rules to the owning service. Let auditing populate audit fields and let optimistic locking failures propagate.
- For paged reads, map the `Page` inside the transaction and use `PagedModel` where the endpoint follows that convention. Consult [jpa-repository-and-associations](../jpa-repository-and-associations/SKILL.md) when changing the query or fetch plan.

## Validation and authorization

- Use `orElseThrow` with `ResourceNotFound` when the use case needs to load a resource. Use an existence query for yes/no business conditions such as team membership. For an ID-only foreign-key write, rely on the database for referential integrity.
- Reuse existing ownership rules. For team manager operations, inspect `@IsAssociatedManager` and `IsAssociatedManagerAspect`. Clarify material access rules when requirements and existing rules leave them unresolved.

## Write failures and repeated calls

- Follow the [project deletion-confirmation rule](../../AGENTS.md#deletes-cascades-and-orphan-removal-ask-first) before adding or changing deletion behavior in a service. A foreign-key failure shows what the database enforces. Use the confirmed deletion policy to decide how the service responds.

- Reuse domain exceptions under `exception` and preserve existing endpoint contracts. `GlobalExceptionHandler` currently maps `ResourceAlreadyExistsException` to 400 and has no handler for `DataIntegrityViolationException`.
- When a feature requires a constraint-backed HTTP response, add central translation using known constraint names or SQLState and operation context. Distinguish duplicate writes, invalid references, and deletes blocked by references.
- Constraint failures can surface during persistence, a query-triggered flush, explicit flush, or commit. Use `saveAndFlush` only when the failure must surface before the method returns. Let the failed transaction roll back.
- Preserve existing repeated-call behavior. For a new assignment operation with unspecified idempotency, reject duplicates through the unique constraint and state the assumption. When duplicates must be a no-op, use the atomic insert strategy in the repository skill.

Create or update test files only when explicitly requested. In the final summary, list tests worth adding for changed behavior and disclose material assumptions.
