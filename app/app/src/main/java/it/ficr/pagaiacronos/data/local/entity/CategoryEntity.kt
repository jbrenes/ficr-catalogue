package it.ficr.pagaiacronos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "categories",
    indices = [Index("category_code", unique = true)]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,


    @ColumnInfo(name = "category_code") val categoryCode: String?,
    @ColumnInfo("category_name") val categoryName: String

)
