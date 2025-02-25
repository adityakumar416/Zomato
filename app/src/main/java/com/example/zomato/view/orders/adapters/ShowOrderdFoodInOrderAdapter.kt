package com.test.zomato.view.orders.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.zomato.R
import com.test.zomato.databinding.FoodItemLayoutBinding
import com.test.zomato.view.orders.orderModels.FoodItemInOrder

class ShowOrderdFoodInOrderAdapter(
    private val foodItems: List<FoodItemInOrder>
) : RecyclerView.Adapter<ShowOrderdFoodInOrderAdapter.FoodItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        val binding = FoodItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        val foodItem = foodItems[position]

        holder.binding.foodQuantity.text = "${foodItem.foodQuantity} x"
        holder.binding.foodNameIs.text = foodItem.foodName


        if (foodItem.foodType == "Veg") {
            holder.binding.veg.setImageResource(R.drawable.pure_veg_icon)
        } else {
            holder.binding.veg.setImageResource(R.drawable.non_veg_icon)
        }
    }

    override fun getItemCount(): Int {
        return foodItems.size
    }

    class FoodItemViewHolder(val binding: FoodItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
