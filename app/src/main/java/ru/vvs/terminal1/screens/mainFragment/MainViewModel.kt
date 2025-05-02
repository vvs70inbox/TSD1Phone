package ru.vvs.terminal1.screens.mainFragment

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.vvs.terminal1.MainActivity
import ru.vvs.terminal1.data.DataRepository
import ru.vvs.terminal1.data.room.CartsDatabase
import ru.vvs.terminal1.mainActivity
import ru.vvs.terminal1.model.CartItem

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DataRepository //= RetrofitRepository()

    private var _myCartList: MutableLiveData<List<CartItem>> = MutableLiveData()
    val myCartList: LiveData<List<CartItem>> = _myCartList

    private val _isProgress = MutableLiveData<Boolean>(false)
    val isProgress: LiveData<Boolean> = _isProgress

    var cartItem: MutableLiveData<CartItem> = MutableLiveData()

    init {
        val cartDao = CartsDatabase.getInstance(application).getCartsDao()
        repository = DataRepository(cartDao)
    }

/*    fun fetchPosts() {
        viewModelScope.launch {
            _postList.postValue(repository.getPosts())
        }
    }*/

    fun getCarts(newList: Boolean) {
            viewModelScope.launch(Dispatchers.IO) {
                _isProgress.postValue(true)
                _myCartList.postValue(repository.getCarts(newList))
                _isProgress.postValue(false)
            }
    }

    fun getCartByBarcode(barcode: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val foundCart: CartItem? = repository.getCartByBarcode(barcode)
            if (foundCart != null) cartItem.postValue(foundCart!!)
        }

    }
}