package ru.vvs.terminal1.screens.salesFragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vvs.terminal1.R
import ru.vvs.terminal1.data.retrofit.api.RetrofitInstance
import ru.vvs.terminal1.databinding.FragmentSalesBinding
import ru.vvs.terminal1.mainActivity
import ru.vvs.terminal1.model.Order
import ru.vvs.terminal1.model.Sale
import ru.vvs.terminal1.model.SaleImportItem
import ru.vvs.terminal1.screens.ordersFragment.OrdersAdapter
import ru.vvs.terminal1.screens.ordersFragment.OrdersFragment
import ru.vvs.terminal1.screens.ordersFragment.OrdersViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class SalesFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private val calendar = Calendar.getInstance()
    private val formatter = SimpleDateFormat("yyyyMMdd", Locale.GERMANY)

    private var mBinding: FragmentSalesBinding?= null
    private val binding get() = mBinding!!

    private lateinit var viewModel: SalesViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SalesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentSalesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mainActivity.actionBar.title = "Работа с отгрузками"
        viewModel = ViewModelProvider(this).get(SalesViewModel::class.java)

        recyclerView = binding.salesFragment
        adapter = SalesAdapter()
        recyclerView.adapter = adapter

        viewModel.isProgress.observe(viewLifecycleOwner) { bool ->
            when(bool) {
                true -> {
                    binding.progressBarOrders.visibility = View.VISIBLE
                    binding.fabSales.visibility = View.INVISIBLE

                }
                else -> {
                    binding.progressBarOrders.visibility = View.GONE
                    binding.fabSales.visibility = View.VISIBLE
                }
            }
        }

        if (viewModel.mySalesList.value == null) {
            viewModel.getSales(false)
        }

        viewModel.mySalesList.observe(viewLifecycleOwner) { list ->
            adapter.setList(list)
        }

        binding.fabSales.setOnClickListener {
            DatePickerDialog(
                mainActivity,
                0,
                this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            ).show()

/*            viewModel.newSale()
            viewModel.order.observe(viewLifecycleOwner) {order ->
                OrdersFragment.clickOrder(order)
            }*/
        }

        ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //TODO Вынести в утилиты
                val alertDialog = AlertDialog.Builder(mainActivity)
                alertDialog.apply {
                    setIcon(R.drawable.baseline_delete_24)
                    setTitle("Удаление отгрузки")
                    setMessage("Вы уверены, что хотите удалить выбранную отгрузку?")
                    setCancelable(false)
                    setPositiveButton("ДА") { _, _ ->
                        //toast("clicked positive button")
                        viewModel.swipeItem(viewModel.mySalesList.value!!.get(viewHolder.adapterPosition) )
                    }
                    setNegativeButton("НЕТ") { _, _ ->
                        //toast("clicked negative button")
                        viewModel.getSales(false)
                    }
                }.create().show()
            }

        }).attachToRecyclerView(recyclerView)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)
        //Toast.makeText(mainActivity,formatter.format(calendar.timeInMillis),Toast.LENGTH_LONG).show()
        viewModel.choiceSale(formatter.format(calendar.timeInMillis)+"000000")
    }

    companion object {
        const val TAG = "Mertech"
        fun clickSale(order: Sale) {
            val bundle = Bundle()
            bundle.putSerializable("order", order)
            mainActivity.navController.navigate(R.id.action_salesFragment_to_saleFragment, bundle)
        }
    }

}