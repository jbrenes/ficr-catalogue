package it.ficr.pagaiacronos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "athletes",
    indices = [Index("fick_id", unique = true), Index("club_code"), Index("last_name")]
)
data class AthleteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "fick_id") val fickId: String,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "birth_date") val birthDate: String? = null,
    val club: String? = null,
    @ColumnInfo(name = "club_code") val clubCode: String? = null,
    val nationality: String = "ITA"
)
