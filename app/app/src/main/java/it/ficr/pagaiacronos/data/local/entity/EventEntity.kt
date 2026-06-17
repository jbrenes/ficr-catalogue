package it.ficr.pagaiacronos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "events",
    indices = [Index("fick_event_id", unique = true), Index("date")]
)
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "fick_event_id") val fickEventId: String,
    val date: String,
    val location: String? = null,
    @ColumnInfo(name = "field_name") val fieldName: String? = null,
    val organiser: String? = null
)
