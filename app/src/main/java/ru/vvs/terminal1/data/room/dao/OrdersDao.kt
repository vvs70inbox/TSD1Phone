package ru.vvs.terminal1.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import ru.vvs.terminal1.model.CartItem
import ru.vvs.terminal1.model.Order

@Dao
interface OrdersDao: BaseDao<Order> {

    @Query("SELECT * from orders_table")
    suspend fun getAllOrders(): List<Order>

    @Query("SELECT MAX(id) from orders_table")
    suspend fun getCount(): Int

}