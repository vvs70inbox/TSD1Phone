package ru.vvs.terminal1.screens.salesFragment

import android.app.AlertDialog
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.vvs.terminal1.MainActivity
import ru.vvs.terminal1.R
import ru.vvs.terminal1.data.SalesRepository
import ru.vvs.terminal1.data.retrofit.api.RetrofitInstance
import ru.vvs.terminal1.data.room.CartsDatabase
import ru.vvs.terminal1.mainActivity
import ru.vvs.terminal1.model.Order
import ru.vvs.terminal1.model.Sale
import ru.vvs.terminal1.model.SaleImportItem

class SalesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SalesRepository //= RetrofitRepository()

    private var _mySalesList: MutableLiveData<List<Sale>> = MutableLiveData()
    val mySalesList: LiveData<List<Sale>> = _mySalesList

    private val _isProgress = MutableLiveData<Boolean>(false)
    val isProgress: LiveData<Boolean> = _isProgress

    init {
        val salesDao = CartsDatabase.getInstance(application).getSalesDao()
        val salesItemDao = CartsDatabase.getInstance(application).getSalesItemDao()
        repository = SalesRepository(salesDao, salesItemDao)
    }

    fun getSales(newList: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _isProgress.postValue(true)
            _mySalesList.postValue(repository.getSales(newList))
            _isProgress.postValue(false)
        }
    }

    fun choiceSale(format: String) {
        //Toast.makeText(mainActivity,format,Toast.LENGTH_LONG).show()
        viewModelScope.launch(Dispatchers.IO) {
            if (!MainActivity.isOnline(mainActivity)) {
                Toast.makeText(
                    mainActivity,
                    mainActivity.getString(R.string.error_internet),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                try {
                    val newSales = RetrofitInstance.api.getSales(format) ?: return@launch
                    if (newSales.isNotEmpty()) {
                        var newSalesSting: Array<String> = emptyArray()
                        for (item in newSales) {
                            newSalesSting += item.toString()
                        }
                        //Toast.makeText(mainActivity,format + newSales.size.toString(),Toast.LENGTH_LONG).show()

                        withContext(Dispatchers.Main) {
                            val builder = AlertDialog.Builder(mainActivity)
                            builder.setTitle("Выберите заказ")
                                .setItems(
                                    newSalesSting
                                ) { dialog, which ->
                                    //Toast.makeText(mainActivity, "Выбранный заказ: ${newSalesSting[which]}", Toast.LENGTH_SHORT).show()
                                    newSaleRecord(newSales[which])
                                }
                            builder.create().show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("choiceSale", "Error occurred: ${e.message}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            mainActivity,
                            "Error occurred: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun newSaleRecord(item: SaleImportItem) {
        //Toast.makeText(mainActivity, "Выбранный заказ: ${item.numberSale}", Toast.LENGTH_SHORT).show()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newSaleItems = RetrofitInstance.api.getSaleItems(
                    item.numberSale,
                    item.dateSale.substring(6..9)
                ) //.substring(0..3))
                if (newSaleItems.isNotEmpty()) {
                    // проверка на существование
                    if (repository.getSaleByNumberAndDate(item.numberSale, item.dateSale)) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                mainActivity,
                                "Выбран заказ, который есть в базе!!! Нужно сначала удалить.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        val counts = repository.countSales() + 1
                        val sale = Sale(
                            counts,
                            item.buyerSale,
                            item.numberSale,
                            item.dateSale,
                            0,
                            0,
                            item.amoutSale,
                            item.managerSale,
                            item.commentSale
                        )
                        repository.newSale(sale)
                        for (item in newSaleItems) {
                            item.orderId = counts
                            repository.newSaleItem(item)
                        }
                        _mySalesList.postValue(repository.getSales(false))
                        /*                withContext(Dispatchers.Main) {
                        Toast.makeText(mainActivity, "Выбран заказ - количество позиций ${newSaleItems.size}", Toast.LENGTH_SHORT).show()
                    }*/
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(mainActivity, "Выбран заказ без товара!!!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            catch (e:Exception)
            {
                Log.e("newSaleRecord", "Error occurred: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        mainActivity,
                        "Error occurred: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun swipeItem(sale: Sale) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSale(sale)
            getSales(false)
        }
    }
}