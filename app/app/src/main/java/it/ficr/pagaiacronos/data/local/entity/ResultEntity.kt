package it.ficr.pagaiacronos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "results",
    foreignKeys = [ForeignKey(
        entity = RaceEntity::class,
        parentColumns = ["id"],
        childColumns = ["race_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("fick_result_id", unique = true), Index("race_id")]
)
data class ResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "fick_result_id") val fickResultId: String,
    @ColumnInfo(name = "race_id") val raceId: Long,
    val lane: Int? = null,
    val rank: Int? = null,
    @ColumnInfo(name = "time_ms") val timeMs: Long? = null,
    @ColumnInfo(name = "gap_ms") val gapMs: Long? = null,
    val points: Int? = null,
    val dns: Boolean = false,
    val dnf: Boolean = false,
    val dsq: Boolean = false
)
