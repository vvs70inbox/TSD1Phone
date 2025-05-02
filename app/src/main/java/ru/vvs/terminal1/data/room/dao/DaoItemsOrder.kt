package ru.vvs.terminal1.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.vvs.terminal1.model.ItemsOrder
import ru.vvs.terminal1.model.Order
import ru.vvs.terminal1.model.OrderItem


@Dao
interface DaoItemsOrder {

        @Query("SELECT carts_table.GroupString, carts_table.Product, carts_table.Character, carts_table.Barcode, carts_table.Price, orders_item_table.counts FROM orders_item_table, carts_table WHERE (orders_item_table.Barcode == carts_table.Barcode) AND orders_item_table.order_id == :id")
        suspend fun getItemsOrder(id: Int): List<ItemsOrder>

        @Query("SELECT carts_table.GroupString,carts_table.Product, carts_table.Character, carts_table.Barcode, carts_table.Price, orders_item_table.counts FROM orders_item_table, carts_table WHERE (orders_item_table.Barcode == carts_table.Barcode) AND orders_item_table.barcode == :barcode AND orders_item_table.order_id == :orderId")
        suspend fun getItemByBarcode(barcode: String, orderId: Int): ItemsOrder

        @Query("SELECT * FROM orders_item_table WHERE barcode == :barcode AND order_id == :orderId")
        suspend fun getItemOrderByBarcode(barcode: String, orderId: Int): OrderItem?

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun InsertItem(orderItem: OrderItem)

        @Update
        suspend fun UpdateItem(orderItem: OrderItem)

        @Delete
        suspend fun DeleteItem(orderItem: OrderItem)

        @Query("SELECT MAX(id) from orders_item_table")
        suspend fun getCount(): Int

        @Update
        suspend fun UpdateOrder(order: Order)
}