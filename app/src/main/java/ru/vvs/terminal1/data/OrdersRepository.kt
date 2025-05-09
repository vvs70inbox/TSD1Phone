package ru.vvs.terminal1.data

import android.icu.util.Calendar
import android.icu.util.LocaleData
import android.os.Build
import androidx.annotation.RequiresApi
import ru.vvs.terminal1.data.room.dao.OrdersDao
import ru.vvs.terminal1.model.Order
import java.time.LocalDate

class OrdersRepository(private val ordersDao: OrdersDao) {

    suspend fun getOrders(newList: Boolean): List<Order> {
        return ordersDao.getAllOrders()
    }

    suspend fun newOrder() : Order {
        val count = ordersDao.getCount()+1
        val order = Order(count, "Заказ", count.toString(), LocalDate.now().toString(), "",0, 0, 0)

        ordersDao.insert(order)
        return order
    }

    suspend fun deleteOrder(order: Order) {
        ordersDao.delete(order)
    }

    suspend fun updateOrder(order: Order) {
        ordersDao.update(order)
    }
}