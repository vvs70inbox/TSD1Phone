package ru.vvs.terminal1.screens.mainFragment.cartFragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import ru.vvs.terminal1.Translit
import ru.vvs.terminal1.mainActivity
import ru.vvs.terminal1.databinding.FragmentCartBinding
import ru.vvs.terminal1.model.CartItem
import ru.vvs.terminal1.screens.ordersFragment.orderFragment.OrderAdapter

class CartFragment : Fragment() {

    private var mBinding: FragmentCartBinding?= null
    private val binding get() = mBinding!!
    private lateinit var currentCart: CartItem

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentCartBinding.inflate(layoutInflater, container, false)

        currentCart = arguments?.getSerializable("cart") as CartItem

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mainActivity.actionBar.title = "Карточка товара"

        val viewModel = ViewModelProvider(this).get(CartViewModel::class.java)

        recyclerView = binding.itemsCharacterLayout
        adapter = CartAdapter()
        recyclerView.adapter = adapter

        viewModel.getItems(currentCart.Product)
        viewModel.myItemsList.observe(viewLifecycleOwner) { list ->
            adapter.setList(list)
        }

        //binding.cartGroup.text = currentCart.GroupString.substringBeforeLast("/").substringAfterLast("/")
        binding.cartName.text = currentCart.Product.substringBefore(",")
        binding.cartNameEnglish.text = currentCart.Product.substringAfter(",").trim()
        binding.cartCharacter.text = currentCart.Character
        binding.cartBarcode.text = currentCart.Barcode
        binding.cartQuantity.text = currentCart.Quantity.toString()
        binding.cartProduction.text = currentCart.Production.toString()
        binding.cartReserve.text = currentCart.Reserve.toString()
        binding.cartPrice.text = currentCart.Price.toString()
        binding.cartDescription.text = currentCart.Description

        binding.cartButton.setOnClickListener {
            val url = "https://yoly-paly.ru/catalog/" +
                    Translit.Cyr2Lat(
                        currentCart.GroupString.dropLast(1).split('/').last()) + "/" + Translit.Cyr2Lat(currentCart.Product) + "/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = url.toUri()
            startActivity(i) }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}