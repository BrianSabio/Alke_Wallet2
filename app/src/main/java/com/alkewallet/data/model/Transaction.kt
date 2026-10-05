package com.alkewallet.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,
    @ColumnInfo(name = "remote_id")
    val remoteId: Int? = null,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "concept")
    val concept: String,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "user_id")
    val userId: Int
)