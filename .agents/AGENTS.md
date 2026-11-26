## Skills
Before starting any task, review all available skills and load every one whose description matches the task, including tasks that touch the topic only indirectly (for example, an endpoint that needs service or repository work). Do not start implementing until the matching skills are loaded. If no skill applies, proceed normally.

## Workflow: step-by-step with review gates
- If a task has multiple steps (given by me, or discovered while working), do not do them all at once. Work on one step at a time.
- After finishing a step, stop and present: what was changed (files and a short summary), assumptions made, and what the next step would be.
- Do not start the next step until I explicitly approve with a message such as "next", "go to next", or "approved". Silence, a question, or feedback is not approval.
- If I give feedback or request changes, apply them to the current step only, then stop and ask for review again.
- If you discover extra work while implementing (a refactor, a bug, a missing migration), do not do it. Add it to a "discovered issues" list in your summary and let me decide whether and when it becomes a step.
- At the start of a multi-step task, list all steps you plan to take and wait for my confirmation before starting step 1.
- Never combine steps to save time, and never make changes outside the current step's scope.

## Database migrations (Flyway)
- The project is in a pre-release phase where the schema is dropped and recreated freely. Do not create a new Flyway migration file for each change.
- Apply all schema changes by editing the latest existing migration file in place (the one with the highest version number).
- Create a new migration version only when I explicitly say so (for example "create a new migration"). Until then, keep editing the current one.
- Do not edit any migration other than the latest one, and never rename or renumber existing migration files.
- After changing a migration, remind me that the local database must be dropped and recreated (or `flyway clean` run), because the checksum of the applied version has changed.
- Keep entity annotations and the migration in sync: any new column, constraint, index, or sequence added to an entity must also be added to the latest migration (including unique constraints used for duplicate protection and sequences referenced by `@SequenceGenerator`).
- If I have said that a version is already deployed or released, treat it as frozen and ask before touching it.

## Deletes, cascades, and orphan removal: ask first
- Get explicit confirmation before choosing or changing `cascade`, `orphanRemoval`, or a foreign key's `ON DELETE` action. Preserve existing settings during unrelated work.
- Get explicit confirmation before introducing or changing a deletion path for an entity referenced by other entities, including through a join entity such as `VolunteerChildAssignmentEntity`, or choosing between hard delete, soft delete, and blocking the delete. This includes service or repository deletes and collection updates that newly trigger an existing cascade or orphan removal.
- Get explicit confirmation before introducing or changing an operation that directly deletes join rows, including `VolunteerChildAssignmentEntity` rows. An explicit user instruction specifying which records to delete and which to retain counts as confirmation for that behavior.
- Use the code and schema to establish current behavior. Get confirmation of the intended business and retention rules before deciding whether to delete, retain, or orphan related records.
- Ask all open questions in one message. Name the affected entities, explain which records each option would delete or retain, and identify what information could be lost.
- While confirmation is pending or unavailable, leave the affected behavior unchanged, continue independent work within the current approved step, and list the open decision in the summary.
