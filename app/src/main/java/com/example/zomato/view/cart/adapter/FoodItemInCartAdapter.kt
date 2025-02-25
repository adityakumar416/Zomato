package com.test.zomato.view.cart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.test.zomato.R
import com.test.zomato.databinding.BuyFoodItemLayoutBinding
import com.test.zomato.view.cart.interfaces.AddFoodClickListener
import com.test.zomato.view.main.home.models.FoodItem

class FoodItemInCartAdapter(
    private val foodItems: List<FoodItem>,
    private val addFoodClickListener: AddFoodClickListener
) : RecyclerView.Adapter<FoodItemInCartAdapter.FoodItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        val binding = BuyFoodItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        val foodItem = foodItems[position]

        holder.binding.foodName.text = foodItem.foodName
        holder.binding.foodPriceIs.text = "₹${foodItem.foodPrice}"
        holder.binding.totalFoodCount.text = foodItem.foodQuantity.toString()

        val totalPrice = foodItem.foodQuantity.toInt() * foodItem.foodPrice.toInt()
        holder.binding.totalCountFoodPriceIs.text = "₹$totalPrice"

        // Check food type (Veg/Non-Veg)
        if (foodItem.foodType == "Veg") {
            holder.binding.veg.setImageResource(R.drawable.pure_veg_icon)
        } else {
            holder.binding.veg.setImageResource(R.drawable.non_veg_icon)
        }

        holder.binding.incrementCount.setOnClickListener {
            foodItem.foodQuantity++
            // Update food quantity in the database
            addFoodClickListener.onFoodQuantityChange(foodItem)
            // Update UI after DB update
            holder.binding.totalFoodCount.text = foodItem.foodQuantity.toString()
        }

        // decrement food
        holder.binding.decrementCount.setOnClickListener {
            if (foodItem.foodQuantity > 0) {
                foodItem.foodQuantity--
                // Update food quantity in the database
                addFoodClickListener.onFoodQuantityChange(foodItem)
                // Update UI after DB update
                holder.binding.totalFoodCount.text = foodItem.foodQuantity.toString()
                notifyItemChanged(position)
            }
        }

        holder.binding.edit.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Edit item clicked", Toast.LENGTH_SHORT).show()
        }

    }

    override fun getItemCount(): Int = foodItems.size

     class FoodItemViewHolder(val binding: BuyFoodItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
