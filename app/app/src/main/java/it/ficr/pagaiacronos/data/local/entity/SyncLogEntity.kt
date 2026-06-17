package it.ficr.pagaiacronos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_log")
data class SyncLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "synced_at") val syncedAt: String,
    @ColumnInfo(name = "records_updated") val recordsUpdated: Int = 0,
    @ColumnInfo(name = "source_url") val sourceUrl: String? = null,
    val status: String = "ok",
    @ColumnInfo(name = "error_message") val errorMessage: String? = null
)
