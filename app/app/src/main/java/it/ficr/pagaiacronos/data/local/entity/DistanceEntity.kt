package it.ficr.pagaiacronos.data.local.entity

import android.R
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "distances",
    indices = [Index("distance", unique = true)]
)
data class DistanceEntity(
    @PrimaryKey() val distance: Int

)
