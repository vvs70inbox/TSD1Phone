package ru.vvs.terminal1.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import ru.vvs.terminal1.model.Sale

@Dao
interface SalesDao: BaseDao<Sale> {

    @Query("SELECT * from sales_table")
    suspend fun getAllSales(): List<Sale>

    @Query("SELECT * FROM sales_table WHERE number=:number AND date=:date LIMIT 1")
    suspend fun getSale(number: String, date: String): Sale?

    @Query("SELECT MAX(id) from sales_table")
    suspend fun getCount(): Int

}