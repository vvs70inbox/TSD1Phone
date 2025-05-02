package ru.vvs.terminal1.data

import ru.vvs.terminal1.data.room.dao.DaoItemsOrder
import ru.vvs.terminal1.model.ItemsOrder
import ru.vvs.terminal1.model.Order
import ru.vvs.terminal1.model.OrderItem

class ItemsOrderRepository(private val itemsDao: DaoItemsOrder) {

    suspend fun getItems(orderId: Int): List<ItemsOrder> {
        return itemsDao.getItemsOrder(orderId)
    }
// ищем в объединении
    suspend fun getItemByBarcode(barcode: String, orderId: Int): ItemsOrder {
        return itemsDao.getItemByBarcode(barcode, orderId)
    }
// ищем в таблице
    suspend fun getItemOrderByBarcode(barcode: String, orderId: Int): OrderItem? {
        return itemsDao.getItemOrderByBarcode(barcode, orderId)
    }

    suspend fun newItem(barcode: String, orderId: Int): ItemsOrder {
        val count = 0//itemsDao.getCount()+1
        val item = OrderItem(count, orderId, barcode, 1)

        itemsDao.InsertItem(item)

        return itemsDao.getItemByBarcode(barcode, orderId)
    }

    suspend fun updateItem(barcode: String, orderId: Int) : ItemsOrder {

        var itemOrder = getItemOrderByBarcode(barcode, orderId)
        itemOrder!!.counts +=1
        val item = OrderItem(itemOrder!!.id, orderId, barcode, itemOrder.counts)

        itemsDao.UpdateItem(item)
        val itemsOrder = itemsDao.getItemByBarcode(barcode, orderId)

        return itemsOrder
    }

    suspend fun updateItemCount(itemsOrder: ItemsOrder, orderId: Int) {

        var itemOrder = getItemOrderByBarcode(itemsOrder.Barcode, orderId)
        itemOrder!!.counts = itemsOrder.counts
        val item = OrderItem(itemOrder.id, orderId, itemsOrder.Barcode, itemOrder.counts)

        itemsDao.UpdateItem(item)

    }

    suspend fun deleteItem(barcode: String, orderId: Int) {

        var itemOrder = getItemOrderByBarcode(barcode, orderId)
        itemsDao.DeleteItem(itemOrder!!)
    }
    suspend fun updateOrder(order: Order) {
        itemsDao.UpdateOrder(order)
    }
}