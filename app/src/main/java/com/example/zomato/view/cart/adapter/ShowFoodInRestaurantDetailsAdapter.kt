package com.test.zomato.view.cart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.test.zomato.R
import com.test.zomato.databinding.OneFoodItemCardBinding
import com.test.zomato.view.cart.interfaces.AddFoodClickListener
import com.test.zomato.view.main.home.models.FoodItem

class ShowFoodInRestaurantDetailsAdapter(
    private var foodItems: List<FoodItem>,
    private val addFoodClickListener: AddFoodClickListener
) : RecyclerView.Adapter<ShowFoodInRestaurantDetailsAdapter.ViewHolder>() {

    class ViewHolder(val binding: OneFoodItemCardBinding) : RecyclerView.ViewHolder(binding.root)


    fun updateList(newFoodList: List<FoodItem>) {
        foodItems = newFoodList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = OneFoodItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return foodItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val foodItem = foodItems[position]

        holder.binding.foodNameIs.text = foodItem.foodName
        holder.binding.foodPriceIs.text = "₹${foodItem.foodPrice}"
        holder.binding.foodDiscriptionIs.text = foodItem.foodDescription
        holder.binding.foodRating.rating = foodItem.foodRating.toFloat()
        holder.binding.ratingCount.text = "(${foodItem.totalRatingCount})"

        Glide.with(holder.itemView.context)
            .load(foodItem.foodImage)
            .into(holder.binding.foodImage)

        // Check food type (Veg/Non-Veg)
        if (foodItem.foodType == "Veg") {
            holder.binding.veg.setImageResource(R.drawable.pure_veg_icon)
        } else {
            holder.binding.veg.setImageResource(R.drawable.non_veg_icon)
        }

        // visibility of counter and add food card based on the quantity
        updateFoodVisibility(holder, foodItem)

        holder.binding.addFood.setOnClickListener {
            addFoodClickListener.onFoodClick(foodItem)
        }

        //  increment and decrement actions for the counter
        holder.binding.incrementCount.setOnClickListener {
            foodItem.foodQuantity++
            // Update food quantity in the database
            addFoodClickListener.onFoodQuantityChange(foodItem)

        }

        holder.binding.decrementCount.setOnClickListener {
            if (foodItem.foodQuantity > 0) {
                foodItem.foodQuantity--
                // Update food quantity in the database
                addFoodClickListener.onFoodQuantityChange(foodItem)

            }
        }

        if (position==foodItems.size-1){
            holder.binding.dottedLine.visibility = View.GONE
        }

    }

    private fun updateFoodVisibility(holder: ViewHolder, foodItem: FoodItem) {
        if (foodItem.foodQuantity > 0) {
            holder.binding.counterLayout.visibility = View.VISIBLE
            holder.binding.addFood.visibility = View.GONE
            holder.binding.totalFoodCount.text = foodItem.foodQuantity.toString()
        } else {
            holder.binding.counterLayout.visibility = View.GONE
            holder.binding.addFood.visibility = View.VISIBLE
        }
    }
}
