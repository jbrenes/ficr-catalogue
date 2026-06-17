package it.ficr.pagaiacronos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "clubs",
    indices = [Index("club_code", unique = true)]
)
data class ClubEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    @ColumnInfo(name = "club") val club: String? = null,

    @ColumnInfo(name = "club_code") val clubCode: String? = null,

)
/*
 val club: String,
    val club_code: String?
    val club: String? = null,
    @ColumnInfo(name = "club_code") val clubCode: String? = null,
)
 */