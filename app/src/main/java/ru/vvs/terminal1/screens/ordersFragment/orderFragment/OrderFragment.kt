package ru.vvs.terminal1.screens.ordersFragment.orderFragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
//import android.net.Uri
import android.os.Bundle
//import android.os.Handler
//import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
//import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
//import com.xcheng.scanner.BarcodeType
//import com.xcheng.scanner.XcBarcodeScanner
import ru.vvs.terminal1.mainActivity
import ru.vvs.terminal1.R
import ru.vvs.terminal1.SwipeHelper
import ru.vvs.terminal1.databinding.FragmentOrderBinding
import ru.vvs.terminal1.model.Order
import androidx.core.net.toUri
import ru.vvs.terminal1.Translit
import androidx.core.content.edit
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import ru.vvs.terminal1.model.CartItem

class OrderFragment : Fragment() {

    private var mBinding: FragmentOrderBinding? = null
    private val binding get() = mBinding!!

    private lateinit var viewModel: OrderViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderAdapter

    private lateinit var menuHost: MenuHost
    private lateinit var provider: MenuProvider

    private lateinit var sharedpreferences: SharedPreferences

    lateinit var currentOrder: Order

    private var allowManualInput = false
    private var enableAutoZoom = false //true
    private var barcodeResultView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentOrderBinding.inflate(layoutInflater, container, false)

        currentOrder = arguments?.getSerializable("order") as Order

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    //private fun toast(text: String) = Toast.makeText(mainActivity, text, Toast.LENGTH_SHORT).show()

    private fun init() {
        mainActivity.actionBar.title = "Работа с заказом"
        viewModel = ViewModelProvider(this)[OrderViewModel::class.java]

        recyclerView = binding.itemsOrderLayout
        adapter = OrderAdapter() { position -> onItemClick(position) }
        recyclerView.adapter = adapter

        val sales = resources.getStringArray(R.array.Sales)

        sharedpreferences =
            requireContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val salesPref = sharedpreferences.getString(SALES_KEY, null)

        // access the spinner
        val spinner = binding.spinner

        //val adapterSales = ArrayAdapter(mainActivity, android.R.layout.simple_spinner_item, sales)

        spinner.adapter = ItemAdapter(mainActivity, sales.toList()) //adapterSales

        if (currentOrder.sales != "")
            spinner.setSelection(sales.indexOf(currentOrder.sales))
        else
            if (salesPref != null)
                spinner.setSelection(sales.indexOf(salesPref))

        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?, position: Int, id: Long
            ) {
                currentOrder.sales = sales[position]
                // calling method to edit values in shared prefs.
                sharedpreferences.edit() {
                    putString(SALES_KEY, sales[position])
                    apply()
                }
                //Toast.makeText(mainActivity, sales[position], Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        binding.orderNumber.text = currentOrder.number
        binding.orderDate.text = currentOrder.date
        //binding.orderNote.text = currentOrder.name

        binding.orderPositions.text = currentOrder.positions.toString()
        binding.orderAmount.text = currentOrder.amount.toString()
        binding.orderProducts.text = currentOrder.products.toString()

        viewModel.itemOrder.observe(viewLifecycleOwner) { } // снимаем наблюдение
        viewModel.itemOrder = MutableLiveData()

        viewModel.getItems(currentOrder.id)
        viewModel.myItemsList.observe(viewLifecycleOwner) { list ->
            adapter.setList(list)
            currentOrder.products = list.sumOf { it.counts }
            currentOrder.amount = list.sumOf { it.counts * it.Price }
            currentOrder.positions = list.count()
            binding.orderPositions.text = currentOrder.positions.toString()
            binding.orderAmount.text = currentOrder.amount.toString()
            binding.orderProducts.text = currentOrder.products.toString()
        }

        val swipeHelper = object : SwipeHelper(recyclerView) {
            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                return listOf(
                    UnderlayButton(
                        mainActivity,
                        "Delete",
                        14f,
                        android.R.color.holo_red_light,
                        R.drawable.baseline_delete_24,
                        50,
                        object : UnderlayButtonClickListener {
                            override fun onClick(pos: Int) {
                                // Handle delete button click
                                val alertDialog = AlertDialog.Builder(mainActivity)

                                alertDialog.apply {
                                    setIcon(R.drawable.baseline_delete_24)
                                    setTitle("Удаление товара")
                                    setMessage("Вы уверены, что хотите удалить выбранный товар?")
                                    setCancelable(false)
                                    setPositiveButton("ДА") { _, _ ->
                                        //toast("clicked positive button")
                                        viewModel.swipeItem(
                                            pos,
                                            currentOrder.id
                                        )
                                    }
                                    setNegativeButton("НЕТ") { _, _ ->
                                        //toast("clicked negative button")
                                        viewModel.getItems(currentOrder.id)
                                    }
                                    //setNeutralButton("Neutral") { _, _ ->
                                    //    toast("clicked neutral button")
                                    //}
                                }.create().show()
                            }
                        }
                    ),
                    UnderlayButton(
                        mainActivity,
                        "Edit",
                        14f,
                        android.R.color.holo_blue_light,
                        R.drawable.baseline_qr_code_scanner_24,
                        50,
                        object : UnderlayButtonClickListener {
                            override fun onClick(pos: Int) {
                                viewModel.getCartItemByBarcode(viewModel.myItemsList.value!![pos].Barcode)

                                /*
                                val url = "https://yoly-paly.ru/catalog/" +
                                        Translit.Cyr2Lat(
                                            viewModel.myItemsList.value!![pos].GroupString.dropLast(
                                                1
                                            ).split('/').last()
                                        ) + "/" +
                                        Translit.Cyr2Lat(viewModel.myItemsList.value!![pos].Product) + "/"
                                val i = Intent(Intent.ACTION_VIEW)
                                i.data = url.toUri()
                                startActivity(i)

                                 */

                            }
                        }
                    )
                )
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHelper)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        menuHost = requireActivity()
        provider = object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_unload, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.actionUnload -> {
                        viewModel.createOrderIn1C(currentOrder.number.trim(), currentOrder.sales)
                    }
                    R.id.actionChoice -> {
                        // переход в картотеку для выбора товара
                        val bundle = Bundle()
                        bundle.putSerializable("order", currentOrder)
                        mainActivity.navController.navigate(R.id.action_orderFragment_to_cartsFragment,bundle)
                    }
                }
                return true
            }
        }
        menuHost.addMenuProvider(provider)

        binding.fabOrder.setOnClickListener {
            val optionsBuilder = GmsBarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_EAN_13)
            if (allowManualInput) {
                optionsBuilder.allowManualInput()
            }
            if (enableAutoZoom) {
                optionsBuilder.enableAutoZoom()
            }
            val gmsBarcodeScanner = GmsBarcodeScanning.getClient(mainActivity, optionsBuilder.build())
            gmsBarcodeScanner
                .startScan()
                .addOnSuccessListener { barcode: Barcode ->
                    when (barcode.rawValue!!.substring(0, 1)) {
                        "2" -> // изменяем кол-во
                        {
                            viewModel.getCartByBarcode(barcode.rawValue!!, currentOrder.id)
                            //if (cart.Barcode == barcode.rawValue) Toast.makeText(MAIN, "ВСЁ ОК!!!!!", Toast.LENGTH_LONG).show()
                        }
                        else -> Toast.makeText(mainActivity, "Штрихкод начинается не на 27!", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e: Exception -> getErrorMessage(e) } //barcodeResultView!!.text = getErrorMessage(e) }
                .addOnCanceledListener {
                    //barcodeResultView!!.text = getString(R.string.error_scanner_cancelled)
                    Toast.makeText(mainActivity, getString(R.string.error_scanner_cancelled), Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun getErrorMessage(e: Exception): String? {
        return if (e is MlKitException) {
            when (e.errorCode) {
                MlKitException.CODE_SCANNER_CAMERA_PERMISSION_NOT_GRANTED ->
                    getString(R.string.error_camera_permission_not_granted)
                MlKitException.CODE_SCANNER_APP_NAME_UNAVAILABLE ->
                    getString(R.string.error_app_name_unavailable)
                else -> getString(R.string.error_default_message, e)
            }
        } else {
            e.message
        }
    }
/*
    private fun connectScanService() {
        XcBarcodeScanner.init(mainActivity) { result ->
            mainActivity.runOnUiThread {
                //showAlertDialog("", result, false, "OK", null)
                Toast.makeText(mainActivity, result, Toast.LENGTH_SHORT).show()
                when (result.substring(0, 1)) {
                    "2" -> {
                        viewModel.getCartByBarcode(result, currentOrder.id)
                        //viewModel.updateItem(result, currentOrder.id)
                    }

                    else -> Toast.makeText(
                        mainActivity,
                        "Штрихкод начинается не на 27!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun onScanServiceStateChanged() {
        val isServiceSuspending = XcBarcodeScanner.isScanServiceSuspending()

        if (isServiceSuspending) {
            XcBarcodeScanner.resumeScanService()
        }
    }
 */
    /*
        override fun onResume() {
            super.onResume()

            // Connect with scan service
            Log.d(TAG, "Connect barcode service")
            connectScanService()

    //        viewModel.cartItem.observe(viewLifecycleOwner) { cart ->
    //            clickMovie(cart)
    //        }

            // Get SDK version and ScannerService version.
            // This need connection ready, simply delay 0.5 second after connect.
            Handler().postDelayed({
                mainActivity.runOnUiThread { // Get sdk version
                    val sdkVer = XcBarcodeScanner.getSdkVersion(mainActivity)

                    // Get service version
                    val serviceVer = XcBarcodeScanner.getServiceVersion()

                    onScanServiceStateChanged()

                    // Get license state
                    val licState = XcBarcodeScanner.getLicenseState()
                    var licMsg = ""
                }
            }, 500)

            XcBarcodeScanner.setTextPrefix("Empty")
            XcBarcodeScanner.setTextSuffix("Empty")
            XcBarcodeScanner.enableBarcodeType(BarcodeType.QRCODE, false)


    }

    override fun onPause() {
        super.onPause()

//        viewModel.cartItem.observe(viewLifecycleOwner) {}

//        XcBarcodeScanner.deInit(mainActivity)
    }
*/
    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.updateOrder(currentOrder)
        mBinding = null
        menuHost.removeMenuProvider(provider)
    }

    private fun onItemClick(position: Int) {
        //toast(adapter.listMain[position].Product)
        val itemsOrder = adapter.listMain[position]
        val builder = AlertDialog.Builder(mainActivity)
        val inflater = mainActivity.layoutInflater
        val oldCounts = itemsOrder.counts
        var newCounts: Int = 0


        builder.setTitle("Укажите нужное количество")
        val dialogLayout = inflater.inflate(R.layout.item_count_alert, null)
        dialogLayout.findViewById<TextView>(R.id.textViewAlert).text = itemsOrder.Product
        val editText = dialogLayout.findViewById<EditText>(R.id.editTextAlert)
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { _, _ ->

            newCounts = if (editText.text.toString().dropWhile { it == ' ' }.isEmpty()) {
                oldCounts
            } else {
                editText.text.toString().toInt()
            }

            if (oldCounts != newCounts && newCounts != 0) {
                viewModel.updateItemCount(itemsOrder, currentOrder.id, newCounts)
            }
        }
        builder.show()
    }

    companion object {
        //private const val TAG = "Mertech"
        const val SHARED_PREFS = "order_shared_prefs"
        const val SALES_KEY = "sales_key"

        fun clickCart(cart: CartItem) {
            val bundle = Bundle()
            bundle.putSerializable("cart", cart)
            mainActivity.navController.navigate(R.id.action_orderFragment_to_cartFragment, bundle)
        }
    }
}