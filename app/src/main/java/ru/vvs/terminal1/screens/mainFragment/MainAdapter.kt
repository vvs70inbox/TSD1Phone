package ru.vvs.terminal1.screens.mainFragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.vvs.terminal1.R
import ru.vvs.terminal1.model.CartItem
import java.util.Locale

class MainAdapter(): RecyclerView.Adapter<MainAdapter.MainViewHolder>(), Filterable {

    var listMain = emptyList<CartItem>()
    var listMainFilter = emptyList<CartItem>()
    private var listMainFull = emptyList<CartItem>()

    class MainViewHolder(view: View): RecyclerView.ViewHolder(view)

    init {
        listMainFilter = listMain
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart_layout, parent, false)
        return MainViewHolder(view)
    }

    // method for filtering our recyclerview items.
    fun filterList(filterlist: ArrayList<CartItem>) {
        // below line is to add our filtered
        // list in our course array list.
        listMain = filterlist
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }
    fun filterRemove() {
        // below line is to add our filtered
        // list in our course array list.
        listMain = listMainFull
        listMainFilter = listMainFull
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listMainFilter.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
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

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<CartItem>) {
        listMain = list
        listMainFull = list
        listMainFilter = list
        notifyDataSetChanged()
    }

    override fun onViewAttachedToWindow(holder: MainViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.itemView.setOnClickListener {
            MainFragment.clickMovie(listMainFilter[holder.adapterPosition])
        }
    }

    override fun onViewDetachedFromWindow(holder: MainViewHolder) {
        holder.itemView.setOnClickListener(null)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString().split(' ')
                var charCount = 0
                if (charSearch[0].isEmpty()) {
                    listMainFilter = listMain
                } else {
                    val resultList = ArrayList<CartItem>()
                    for (row in listMain) {
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