package ru.vvs.terminal1.screens.ordersFragment.orderFragment.cartsFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.vvs.terminal1.data.DataRepository
import ru.vvs.terminal1.data.ItemsOrderRepository
import ru.vvs.terminal1.data.room.CartsDatabase
import ru.vvs.terminal1.model.CartItem
import ru.vvs.terminal1.model.OrderItem

class CartsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DataRepository
    private val repositoryOrder: ItemsOrderRepository

    private var _myCartList: MutableLiveData<List<CartItem>> = MutableLiveData()
    val myCartList: LiveData<List<CartItem>> = _myCartList

    init {
        val cartDao = CartsDatabase.getInstance(application).getCartsDao()
        val itemsDao = CartsDatabase.getInstance(application).getAllItemsFromOrder()
        repositoryOrder = ItemsOrderRepository(itemsDao)
        repository = DataRepository(cartDao)
    }

    fun getCarts(newList: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _myCartList.postValue(repository.getCarts(newList))
        }
    }

    fun addCartInOrder(cartItem: CartItem, orderId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val foundCart: OrderItem? = repositoryOrder.getItemOrderByBarcode(cartItem.Barcode,orderId)
            if (foundCart != null) repositoryOrder.updateItem(cartItem.Barcode, orderId) else repositoryOrder.newItem(cartItem.Barcode, orderId)
        }
    }
}