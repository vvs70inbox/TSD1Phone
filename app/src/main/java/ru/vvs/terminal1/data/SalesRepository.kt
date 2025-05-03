package ru.vvs.terminal1.data

import ru.vvs.terminal1.data.room.dao.SalesDao
import ru.vvs.terminal1.data.room.dao.SalesItemDao
import ru.vvs.terminal1.model.Sale
import ru.vvs.terminal1.model.SaleItem

class SalesRepository(private val salesDao: SalesDao, private val salesItemDao: SalesItemDao) {

    suspend fun getSales(newList: Boolean): List<Sale> {
        return salesDao.getAllSales()
    }

    suspend fun getSaleByNumberAndDate(number: String, date: String): Boolean {
        val  sale = salesDao.getSale(number, date)
        return sale != null
    }

    suspend fun newSale(sale: Sale) {
        salesDao.insert(sale)
    }

    suspend fun newSaleItem(saleItem: SaleItem) {
        salesItemDao.insert(saleItem)
    }

    suspend fun getSaleItems() {

    }

    suspend fun deleteSale(sale: Sale) {
        salesDao.delete(sale)
    }

    suspend fun countSales() : Int {
        return salesDao.getCount()
    }
}