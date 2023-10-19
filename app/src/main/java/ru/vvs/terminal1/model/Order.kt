package ru.vvs.terminal1.model

import android.text.format.DateFormat
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "orders_table")
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val number: String,
    val date: String,
    var positions: Int,
    var products: Int,
    var amount: Int
) : Serializable
