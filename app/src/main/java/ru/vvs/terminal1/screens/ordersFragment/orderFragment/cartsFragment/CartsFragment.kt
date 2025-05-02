package ru.vvs.terminal1.screens.ordersFragment.orderFragment.cartsFragment

import android.app.AlertDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import ru.vvs.terminal1.R
import ru.vvs.terminal1.databinding.FragmentCartsBinding
import ru.vvs.terminal1.databinding.FragmentMainBinding
import ru.vvs.terminal1.mainActivity
import ru.vvs.terminal1.model.CartItem
import ru.vvs.terminal1.model.Order
import ru.vvs.terminal1.screens.mainFragment.MainAdapter
import ru.vvs.terminal1.screens.mainFragment.MainViewModel

class CartsFragment : Fragment() {

    private lateinit var viewModel: CartsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CartsAdapter

    private lateinit var currentOrder: Order

    private lateinit var searchView: SearchView

    private lateinit var menuHost: MenuHost
    private lateinit var provider: MenuProvider

    private var mBinding: FragmentCartsBinding?= null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCartsBinding.inflate(layoutInflater, container, false)

        currentOrder = arguments?.getSerializable("order") as Order

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mainActivity.actionBar.title = "Выбор из картотеки"
        viewModel = ViewModelProvider(this)[CartsViewModel::class.java]

        if (viewModel.myCartList.value == null) {
            viewModel.getCarts(false)
        }

        recyclerView = binding.cartsFragment
        adapter = CartsAdapter() { position -> onItemClick(position) }
        recyclerView.adapter = adapter

        viewModel.myCartList.observe(viewLifecycleOwner) { list ->
            adapter.setList(list)
        }

        menuHost = requireActivity()
        provider = object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_search, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.actionSearch -> {
                        val searchView: SearchView = menuItem.actionView as SearchView
                        // below line is to call set on query text listener method.
                        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                            override fun onQueryTextSubmit(p0: String?): Boolean {
                                return false
                            }

                            override fun onQueryTextChange(msg: String): Boolean {
                                // inside on query text change method we are
                                // calling a method to filter our recycler view.
                                adapter.filter.filter(msg)
                                return false
                            }
                        })
                        // обрабатываем кнопку возврата из поиска / processing the return button from the search
                        menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                                return true
                            }
                            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                                adapter.filterRemove()
                                return true
                            }
                        })
                    }
                }
                return true
            }
        }
        menuHost.addMenuProvider(provider)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
        menuHost.removeMenuProvider(provider)
    }

    private fun onItemClick(position: Int) {
        //toast(adapter.listMain[position].Product)
        val itemsCart = adapter.listMainFilter[position]
        val bundle = Bundle()

        bundle.putSerializable("order", currentOrder)
        viewModel.addCartInOrder(itemsCart,currentOrder.id)

        mainActivity.navController.popBackStack()

    }
}