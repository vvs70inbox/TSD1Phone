package ru.vvs.terminal1.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "sale_items_table", foreignKeys = [ForeignKey(entity = Sale::class, parentColumns = ["id"], childColumns = ["order_id"], onDelete = CASCADE)])
data class SaleItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "order_id", index = true)
    var orderId: Int,
    val barcode: String,
    var counts: Int,
    var checks: Int,
    var price: Int
)
