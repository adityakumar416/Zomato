package com.test.zomato.view.main.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.test.zomato.databinding.RestaurantsItemsBinding
import com.test.zomato.view.cart.interfaces.RestaurantsClickListener
import com.test.zomato.view.main.home.models.RestaurantDetails

class AllRestaurantsAdapter(
    private var restaurantList: List<RestaurantDetails>,
    private val restaurantsClickListener: RestaurantsClickListener
) : RecyclerView.Adapter<AllRestaurantsAdapter.RestaurantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding =
            RestaurantsItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RestaurantViewHolder(binding)
    }


    fun updateList(restaurantList: List<RestaurantDetails>){
        this.restaurantList = restaurantList
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = restaurantList[position]

        val imageList =
            restaurant.imageSliderImages.map { SlideModel(it, scaleType = ScaleTypes.FIT) }
        holder.binding.imageSlider.setImageList(imageList)

        holder.binding.takenTime.text = restaurant.time
        holder.binding.distance.text = restaurant.distance
        holder.binding.restaurantsName.text = restaurant.restaurantName
        holder.binding.ratingText.text = restaurant.rating.toString()
        holder.binding.foodType1.text = restaurant.foodType1
        holder.binding.foodType2.text = restaurant.foodType2
        holder.binding.costForOne.text = ("₹${restaurant.costForOne}")
        holder.binding.discountText.text = restaurant.discountText

        holder.binding.imageSlider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {
                restaurantsClickListener.onRestaurantsClick(restaurant)
            }

            override fun doubleClick(position: Int) {
                // Do not use onItemSelected if you are using a double click listener at the same time.
                // Its just added for specific cases.
                // Listen for clicks under 250 milliseconds.
            }
        })

        holder.binding.root.setOnClickListener {
            restaurantsClickListener.onRestaurantsClick(restaurant)
        }


    }

    override fun getItemCount(): Int = restaurantList.size

    class RestaurantViewHolder(val binding: RestaurantsItemsBinding) :
        RecyclerView.ViewHolder(binding.root)
}
