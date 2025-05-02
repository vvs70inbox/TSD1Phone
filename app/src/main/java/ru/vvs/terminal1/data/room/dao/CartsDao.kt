package ru.vvs.terminal1.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.vvs.terminal1.model.CartItem

@Dao
interface CartsDao: BaseDao<CartItem> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(carts: List<CartItem>)

    @Query("DELETE FROM carts_table")
    suspend fun clearTable()

    @Query("SELECT * FROM carts_table ORDER BY Product")
    suspend fun getAllCarts(): List<CartItem>

    @Query("SELECT * from carts_table WHERE Barcode = :barcode")
    suspend fun getCartByBarcode(barcode: String): CartItem?

    @Query("SELECT * from carts_table WHERE Product = :product")
    suspend fun getCartsByProduct(product: String): List<CartItem>

}