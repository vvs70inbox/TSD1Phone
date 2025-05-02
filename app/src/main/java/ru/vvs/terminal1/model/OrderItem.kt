package ru.vvs.terminal1.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "orders_item_table", foreignKeys = [ForeignKey(entity = Order::class, parentColumns = ["id"], childColumns = ["order_id"], onDelete = CASCADE)])
data class OrderItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "order_id", index = true)
    val orderId: Int,
    val barcode: String,
    var counts: Int
)
