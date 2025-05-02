package ru.vvs.terminal1.screens.ordersFragment.orderFragment.cartsFragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.vvs.terminal1.R
import ru.vvs.terminal1.mainActivity
import ru.vvs.terminal1.model.CartItem

class CartsAdapter(private val onItemClick: (position: Int) -> Unit): RecyclerView.Adapter<CartsAdapter.CartsViewHolder>(), Filterable {

    class CartsViewHolder(view: View, private val onItemClick: (position: Int) -> Unit): RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            itemView.setOnClickListener { _ -> onItemClick(adapterPosition) }
        }

        override fun onClick(v: View?) {}
    }

    var listMainFilter = emptyList<CartItem>()
    private var listMain = emptyList<CartItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart_layout, parent, false)
        return CartsViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: CartsAdapter.CartsViewHolder, position: Int) {
        var characterTrim1: String = ""
        var characterTrim2: String = ""
        holder.itemView.findViewById<TextView>(R.id.item_name).text = listMainFilter[position].GroupString.substringAfter("/")
        holder.itemView.findViewById<TextView>(R.id.item_buy).text = listMainFilter[position].Product.substringBefore(",")
        holder.itemView.findViewById<TextView>(R.id.item_english).text = listMainFilter[position].Product.substringAfter(",").trim()
        characterTrim1 = listMainFilter[position].Character.substringAfter(",")
        characterTrim2 = characterTrim1.substringAfter(",").substringBefore(",")
        characterTrim1 = characterTrim1.substringBefore(",")
        holder.itemView.findViewById<TextView>(R.id.item_character).text =
            buildString {
                append(listMainFilter[position].Character.substringBefore(","))
                append(",")
                append(characterTrim1)
                append(",")
                append(characterTrim2)
            }
        holder.itemView.findViewById<TextView>(R.id.item_count).text = listMainFilter[position].Quantity.toString()
        holder.itemView.findViewById<TextView>(R.id.item_price).text = listMainFilter[position].Price.toString()
    }

    override fun getItemCount(): Int {
        return listMainFilter.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<CartItem>) {
        listMain = list
        listMainFilter = list
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterRemove() {
        // below line is to add our filtered
        // list in our course array list.
        listMainFilter = listMain
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }
/*
    override fun onViewAttachedToWindow(holder: CartsViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.itemView.setOnClickListener {
            CartsFragment.
            mainActivity.navController.navigate(R.id.action_cartsFragment_to_orderFragment)
            //CartsFragment.clickMovie(listMainFilter[holder.adapterPosition])
        }
    }

    override fun onViewDetachedFromWindow(holder: CartsViewHolder) {
        holder.itemView.setOnClickListener(null)
    }
*/
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString().split(' ')
                var charCount = 0
                if (charSearch[0].isEmpty()) {
                    listMainFilter = listMain
                } else {
                    val resultList = ArrayList<CartItem>()
                    for (row in listMainFilter) {
                        charCount = 0
                        for (ch in charSearch) {
                            if (row.Product.lowercase().contains(ch.lowercase())) {
                                charCount++
                            }
                        }
                        if (charCount == charSearch.size) {
                            resultList.add(row)
                        }
                    }
                    listMainFilter = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = listMainFilter
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listMainFilter = results?.values as List<CartItem>
                notifyDataSetChanged()
            }
        }
    }

}