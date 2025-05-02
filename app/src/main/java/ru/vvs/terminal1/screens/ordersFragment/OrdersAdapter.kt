package ru.vvs.terminal1.screens.ordersFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.vvs.terminal1.R
import ru.vvs.terminal1.model.CartItem
import ru.vvs.terminal1.model.Order
import ru.vvs.terminal1.screens.mainFragment.MainAdapter
import ru.vvs.terminal1.screens.mainFragment.MainFragment

class OrdersAdapter: RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

    private var listMain = emptyList<Order>()

    class OrdersViewHolder(view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_orders_layout, parent, false)
        return OrdersViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listMain.size
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.item_number).text = listMain[position].id.toString()//.name
        holder.itemView.findViewById<TextView>(R.id.item_date).text = "от ${listMain[position].date}"
        holder.itemView.findViewById<TextView>(R.id.item_positions).text ="${listMain[position].positions} тов."
        holder.itemView.findViewById<TextView>(R.id.item_products).text = "${listMain[position].products} шт."
        holder.itemView.findViewById<TextView>(R.id.item_amount).text = listMain[position].amount.toString()
        holder.itemView.findViewById<TextView>(R.id.item_sales).text = listMain[position].sales.toString()
    }

    fun setList(list: List<Order>) {
        listMain = list
        notifyDataSetChanged()
    }

    override fun onViewAttachedToWindow(holder: OrdersAdapter.OrdersViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.itemView.setOnClickListener {
            OrdersFragment.clickOrder(listMain[holder.adapterPosition])
        }
    }

    override fun onViewDetachedFromWindow(holder: OrdersAdapter.OrdersViewHolder) {
        holder.itemView.setOnClickListener(null)
    }

}