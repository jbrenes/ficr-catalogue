package it.ficr.pagaiacronos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "results_athletes",
    primaryKeys = ["result_id", "athlete_id"],
    foreignKeys = [
        ForeignKey(
            entity = ResultEntity::class,
            parentColumns = ["id"],
            childColumns = ["result_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AthleteEntity::class,
            parentColumns = ["id"],
            childColumns = ["athlete_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("result_id"), Index("athlete_id")]
)
data class ResultAthleteEntity(
    @ColumnInfo(name = "result_id") val resultId: Long,
    @ColumnInfo(name = "athlete_id") val athleteId: Long,
    @ColumnInfo(name = "seat_order") val seatOrder: Int = 0
)
