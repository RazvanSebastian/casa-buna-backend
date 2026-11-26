---
name: jpa-repository-and-associations
description: Implement Spring Data JPA queries, Specifications, and entity associations in Casa Buna Backend. Use for fetch plans, paginated queries, bulk DML, join entities, cascades, entity equality, and their database constraints.
---

# JPA repositories and associations

Java references are relative to `src/main/java/ro/casabuna/casabuna_backend`.

- `repository/specification/UserSpecifications#usersNotInTeam` shows filter-only criteria and a `NOT EXISTS` subquery.
- `repository/VolunteerChildAssignmentRepository#deleteByTeamAndUser` shows JPQL bulk deletion.
- `model/entity/team/VolunteerChildAssignmentEntity` shows the table and three lazy to-one mappings. Follow the ID generator and equality rules below.
- `src/main/resources/db/migration/V1__create_main_tables.sql` defines the assignment sequence, foreign keys, and `uq_volunteer_child_team`.

Transactions, mapping, constraint-violation handling, and idempotency behavior are covered in `service-implementation/SKILL.md`.

## 1. Queries and fetch plans

- Reuse existing Specifications under `repository/specification` first.
- Use a plain repository query for lookups with at most two simple filters. Use a Specification when there are more optional filters or the criteria are reused across endpoints.
- Use plain `JOIN` or a subquery for filter-only conditions. Use `JOIN FETCH` only for associations the caller reads.
- Use `exists` or `count` when checking a condition requires no entity data.

## 2. Pagination

- Use `PageQuery#toPageRequest()` with a deterministic sort and a unique tie-breaker such as `id`.
- Cap page size at the configured `hibernate.default_batch_fetch_size` so one batch can cover one page.
- For DTOs that need collections, page the root entities and access the lazy collections while mapping inside the transaction. With one collection and a sufficiently large batch, Hibernate uses one content query and one collection query, plus a count query when Spring Data needs it.
- Keep collection fetch joins and collection entity graphs out of paged queries. They can cause in-memory pagination or `MultipleBagFetchException`.
- To-one fetches are allowed in paged content queries. Omit fetches from the count query:

```java
if (query.getResultType() != Long.class && query.getResultType() != long.class) {
    root.fetch("association", JoinType.LEFT);
}
```

- Use the two-query fallback only when the Specification must join a to-many association or batch fetching cannot be used. Apply the filters and deterministic sort before paging, return one ID per root, and count matching roots. Prefer `EXISTS` for to-many filters when it avoids duplicate IDs. Skip the entity query if the ID page is empty. Otherwise load entities with `findWith...ByIdIn(ids)` and restore the order from the ID list.

## 3. Duplicates and `distinct`

- Use `query.distinct(true)` or JPQL `DISTINCT` when a to-many join can match multiple rows per root. To-one joins do not duplicate roots.
- Check the join predicates and filters together. At most one row matches when they collectively fix every column of a migration-backed unique key. For example, a join that fixes `team_id` from the root and a filter that fixes `user_id` cover `(user_id, team_id)`. For `VolunteerChildAssignmentEntity`, fixing a team and volunteer can still match several rows when the child is unconstrained.
- When skipping `distinct` based on uniqueness, name the constraint in a short code comment.
- Count distinct roots when the paged content query uses `distinct`. Spring Data handles this when the Specification sets `query.distinct(true)`. Otherwise supply a separate count query.

## 4. Association writes

- Use `getReferenceById` inside the transaction for ID-only to-one assignments. Reading target state initializes the proxy. The foreign key rejects invalid IDs.
- For inverse `@OneToMany(mappedBy = ...)` associations, write through the owning child and query children through their repository. Update the in-memory collection when the mapping requires it or the same transaction reads it again.
- When collection mutation is required, fetch the collection with its owner and load all targets together with `findAllById`. Deduplicate requested IDs before comparing sizes to detect missing targets. Use loaded targets when collection operations depend on non-ID equality.
- Use explicit join entities for runtime writes, large relationships, association attributes, and three-way relationships.
- Plain `@ManyToMany` is allowed for small, bounded reference data such as user roles. Use a `Set` on the managed owner fetched with the collection and keep both sides in sync when bidirectional. For new mappings, exclude `CascadeType.REMOVE` and `CascadeType.ALL` so owner deletion preserves shared targets.

### Cascades, `orphanRemoval`, and deletes require confirmation

- Get explicit confirmation before choosing or changing `cascade`, `orphanRemoval`, or a foreign key's `ON DELETE` action. Preserve existing settings during unrelated work.
- Get explicit confirmation before introducing or changing a deletion path for an entity referenced by other entities, including through a join entity such as `VolunteerChildAssignmentEntity`, or choosing between hard delete, soft delete, and blocking the delete. This includes service or repository deletes and collection updates that newly trigger an existing cascade or orphan removal.
- Get explicit confirmation before introducing or changing an operation that directly deletes join rows, including `VolunteerChildAssignmentEntity` rows. An explicit user instruction specifying which records to delete and which to retain counts as confirmation for that behavior.
- Use the code and schema to establish current behavior. Get confirmation of the intended business and retention rules before deciding whether to delete, retain, or orphan related records.
- Ask all open questions in one message. Name the affected entities, explain which records each option would delete or retain, and identify what information could be lost.
- While confirmation is pending or unavailable, leave the affected behavior unchanged, continue independent work, and list the open decision in the summary. If an existing setting conflicts with another rule in this skill, preserve it and report the conflict.
- For an authorized child removal through an existing `orphanRemoval = true` mapping, remove the child from the managed parent collection so the relationship stays consistent through flush.

## 5. Join entities and schema

- Default to a generated surrogate `@Id` and a migration-backed unique constraint over the relationship columns. Extend `BaseEntity` when the link needs auditing.
- Link `@GeneratedValue(generator = "...")` to its `@SequenceGenerator` by name. Match `allocationSize` to the sequence's `INCREMENT BY` in the migration.
- A null generated ID allows Spring Data to detect a new entity and use `persist` through `save` or `saveAll`.
- Use a composite `@EmbeddedId` only for pure link tables with no audit fields, attributes, or incoming references. Use a `Serializable` key with getter-based `equals` and `hashCode`, `@MapsId` on each reference, and `Persistable<ID>` with a `@Transient isNew` flag set to false in `@PostPersist` and `@PostLoad` so `save()` can use `persist`.
- Map required references as `@ManyToOne(fetch = LAZY)` with `@JoinColumn(nullable = false)`. Insert through the join entity repository, using `getReferenceById` and reusing fixed-side references across rows.
- Query the join entity directly and keep a single writable mapping to its table.
- Check the table, sequence increment, foreign keys, and unique constraint in the migrations before changing a mapping. Follow the project migration rules when changing keys, sequences, or constraints.
- Use PostgreSQL `INSERT ... ON CONFLICT (...) DO NOTHING` for atomic duplicate no-ops on the relevant unique key. Follow the service skill for duplicate error behavior.

## 6. Batch persistence and bulk DML

- `saveAll` persists entity by entity. JDBC batching depends on Hibernate configuration and the ID generator.
- For direct join-row deletion, default to `@Modifying @Query("delete ...")` or `deleteAllInBatch` when bypassing entity callbacks, auditing, and ORM cascades is acceptable. Use managed entity deletion when the authorized behavior requires those mechanisms.
- When the same transaction reads affected entities afterwards, account for pending writes and stale managed state. For `@Modifying` queries, use `flushAutomatically = true` and `clearAutomatically = true`. Clearing detaches every managed entity in the persistence context. Reload any previously managed entities before making further changes that must be persisted, or schedule the bulk operation after the transaction's last use of those entities.
- Return the affected-row count when callers must distinguish absent rows, as in `TeamManagementServiceImpl#removeChildAssignment`.

## 7. Entity equality

- Default to identity equality. Check `BaseEntity` before overriding `equals` and `hashCode`. Override only when separately loaded instances must compare equal or the entity is used in a `Set` or as a `Map` key.
- Prefer ID-based equality. For an entity without mapped subclasses:

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TeamEntity other)) return false;
    return getId() != null && getId().equals(other.getId());
}

@Override
public int hashCode() {
    return TeamEntity.class.hashCode();
}
```

- Use `instanceof` and access fields through getters for proxy compatibility. The constant hash stays stable across persistence and agrees for entities and their proxies. It is suitable for small sets.
- Use natural-key equality only for stable keys and when `getReferenceById` proxies will stay out of sets. Hashing a natural key through a proxy can initialize it and run a `SELECT`.
- Keep fields used for set membership stable while the entity is in the set.

## 8. Code comments

- Treat comments beginning with `BUSINESS RULE:`, `INTENTIONAL:`, and `PERF:` as binding. Ask before changing the behavior they describe.
- For non-obvious choices, add a short comment explaining the reason and when it should change. Keep existing comments accurate when editing adjacent code.

## Before finishing

- [ ] Runtime association writes use the owning side. Collections change only when the mapping requires it or the same transaction needs the updated collection.
- [ ] Changes to deletion behavior and cascade settings have explicit confirmation. Open decisions appear in the summary.
- [ ] `distinct` is used only where roots can be duplicated. Comments name the unique constraints used to omit it.
- [ ] Paged content queries avoid collection fetch joins and collection entity graphs. Count queries omit fetches.
- [ ] Required unique constraints and sequences exist in the migrations.
- [ ] Join-row deletes use bulk DML where bypassing lifecycle behavior is acceptable.
- [ ] Entity equality overrides are necessary and proxy-safe.
- [ ] `BUSINESS RULE:`, `INTENTIONAL:`, and `PERF:` comments remain accurate.
