package ru.vvs.terminal1.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.vvs.terminal1.data.room.dao.CartsDao
import ru.vvs.terminal1.data.room.dao.DaoItemsOrder
import ru.vvs.terminal1.data.room.dao.DaoItemsSale
import ru.vvs.terminal1.data.room.dao.OrdersDao
import ru.vvs.terminal1.data.room.dao.OrdersItemDao
import ru.vvs.terminal1.data.room.dao.SalesDao
import ru.vvs.terminal1.data.room.dao.SalesItemDao
import ru.vvs.terminal1.model.CartItem
import ru.vvs.terminal1.model.ItemsOrder
import ru.vvs.terminal1.model.Order
import ru.vvs.terminal1.model.OrderItem
import ru.vvs.terminal1.model.Sale
import ru.vvs.terminal1.model.SaleItem

@Database(entities = [CartItem::class,Order::class,OrderItem::class, Sale::class, SaleItem::class], version = 9)
abstract class CartsDatabase: RoomDatabase() {

    abstract fun getCartsDao(): CartsDao
    abstract fun getOrdersDao(): OrdersDao
    abstract fun getSalesDao(): SalesDao
    abstract fun getSalesItemDao(): SalesItemDao
    abstract fun getAllOrdersItem(): OrdersItemDao
    abstract fun getAllItemsFromOrder(): DaoItemsOrder
    abstract fun getAllItemsFromSale(): DaoItemsSale

    companion object {
        @Volatile
        private var database: CartsDatabase ?= null

        fun getInstance(context: Context): CartsDatabase {
            return  if (database == null) {
                database = Room
                    .databaseBuilder(context, CartsDatabase::class.java, "db_terminal")
                    .fallbackToDestructiveMigration()
                    .build()
                database as CartsDatabase
            } else {
                database as CartsDatabase
            }
        }
    }
}