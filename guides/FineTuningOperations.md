# Fine-tuning job operations and checkpoint sharing

These additions support existing eligible fine-tuning workflows. Endpoint access and retirement plans are controlled by OpenAI. They do not start training automatically, and the SDK tests these operations offline.

## Pause, resume and checkpoints

```kotlin
val id = FineTuningId("ftjob-existing")
val pausing = openAI.pauseFineTuningJob(id)
val current = openAI.fineTuningJobDetails(id)
val resumed = openAI.resumeFineTuningJob(id)
val checkpoints = openAI.fineTuningJobCheckpoints(id, limit = 10)
```

Pause/resume return `FineTuningJobDetails`. This additive result supports `pausing`/`paused`, nullable or absent hyperparameters, automatic/numeric training values, integrations, metadata and current method configuration. `fineTuningJobDetails` retrieves the same result and returns null for HTTP 404. Other errors propagate.

Existing `FineTuningJob`, `FineTuningRequest`, `Hyperparameters`, builders and legacy methods retain their source signatures. New optional methods have default unsupported implementations so existing custom clients still compile.

`FineTuningPage` retains the object type, first/last cursors and `hasMore`. Checkpoint results include each training/validation metric, step and checkpoint model ID. Nested server method/grader configuration is retained as JSON; this is not an implementation of the alpha grader endpoints or a new training-request API.

## Share checkpoint models between projects

Checkpoint permission operations require an **Admin API key** and belong to the administration client:

```kotlin
val checkpoint = checkpoints.first().fineTunedModelCheckpoint
val granted = admin.createFineTuningCheckpointPermissions(
    checkpoint,
    FineTuningCheckpointPermissionRequest(projectIds = listOf("proj-recipient")),
)
val permissions = admin.fineTuningCheckpointPermissions(
    checkpoint,
    order = FineTuningPermissionOrder.Descending,
    projectId = "proj-recipient",
)
admin.deleteFineTuningCheckpointPermission(checkpoint, granted.first().id)
```

Use the **checkpoint model ID** (`fineTunedModelCheckpoint`), not the checkpoint resource's `id`. Permissions support create, filtered/paginated list, and delete. The current API documents no separate permission-by-ID retrieval route. Ordering uses `ascending` and `descending`, rather than the usual `asc` and `desc`.

See the official [pause](https://developers.openai.com/api/reference/resources/fine_tuning/subresources/jobs/methods/pause), [checkpoints](https://developers.openai.com/api/reference/resources/fine_tuning/subresources/jobs/subresources/checkpoints/methods/list) and [permission reference](https://developers.openai.com/api/reference/resources/fine_tuning/subresources/checkpoints/subresources/permissions/methods/list) for current availability and permissions.
