package com.test.zomato.view.orders.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.test.zomato.databinding.YourOrderCartItemBinding
import com.test.zomato.view.orders.orderModels.OrderWithFoodItems
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    var ordersWithFoodItems: List<OrderWithFoodItems> = listOf()

    class OrderViewHolder(val binding: YourOrderCartItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding =
            YourOrderCartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    fun updateList(orderList: List<OrderWithFoodItems>) {
        ordersWithFoodItems = orderList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val orderWithFoodItems = ordersWithFoodItems[position]

        val order = orderWithFoodItems.order
        val foodItems = orderWithFoodItems.foodItems

        holder.binding.restaurantName.text = order.restaurantName
        holder.binding.deliveryLocation.text = order.deliveryAddress
        holder.binding.foodPriceIs.text = "₹${order.totalPrice}"
        holder.binding.orderStatus.text = order.orderStatus

        val formattedDate = formatTimestamp(order.timestamp)
        holder.binding.orderPlaceTime.text = "Order placed on $formattedDate"

        if (!foodItems[0].foodImage.isNullOrEmpty()) {
            Glide.with(holder.binding.root.context)
                .load(foodItems[0].foodImage)
                .into(holder.binding.foodImage)
        }


        val foodItemAdapter = ShowOrderdFoodInOrderAdapter(foodItems)
        holder.binding.foodDetails.layoutManager = LinearLayoutManager(holder.binding.root.context)
        holder.binding.foodDetails.adapter = foodItemAdapter

    }

    override fun getItemCount(): Int {
        return ordersWithFoodItems.size
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM, hh:mma", Locale.getDefault())
        val date = Date(timestamp)
        return sdf.format(date)
    }
}
