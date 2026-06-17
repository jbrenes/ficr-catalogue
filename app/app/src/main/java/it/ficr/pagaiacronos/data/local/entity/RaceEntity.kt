package it.ficr.pagaiacronos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "races",
    foreignKeys = [ForeignKey(
        entity = EventEntity::class,
        parentColumns = ["id"],
        childColumns = ["event_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index("fick_race_id", unique = true),
        Index("event_id"),
        Index("boat_class"),
        Index("distance_m")
    ]
)
data class RaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "fick_race_id") val fickRaceId: String,
    @ColumnInfo(name = "event_id") val eventId: Long,
    @ColumnInfo(name = "distance_m") val distanceM: Int? = null,
    @ColumnInfo(name = "boat_class") val boatClass: String? = null,
    val gender: String? = null,
    @ColumnInfo(name = "category_code") val categoryCode: String? = null,
    @ColumnInfo(name = "category_name") val categoryName: String? = null,
    @ColumnInfo(name = "round_name") val roundName: String? = null,
    @ColumnInfo(name = "round_code") val roundCode: String? = null,
    @ColumnInfo(name = "heat_number") val heatNumber: Int = 1
)
