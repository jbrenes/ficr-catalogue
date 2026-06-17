package it.ficr.pagaiacronos.domain.model

import it.ficr.pagaiacronos.data.local.entity.SyncLogEntity

data class SyncLog(
    val id: Long,
    val syncedAt: String,
    val recordsUpdated: Int,
    val sourceUrl: String?,
    val status: String,
    val errorMessage: String?
) {
    val isSuccess: Boolean get() = status == "ok"
}

fun SyncLogEntity.toDomain() = SyncLog(
    id = id,
    syncedAt = syncedAt,
    recordsUpdated = recordsUpdated,
    sourceUrl = sourceUrl,
    status = status,
    errorMessage = errorMessage
)
