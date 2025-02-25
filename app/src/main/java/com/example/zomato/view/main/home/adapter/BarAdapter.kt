package com.test.zomato.view.main.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.test.zomato.databinding.HotelBarItemsBinding
import com.test.zomato.view.main.home.models.BarData

class BarAdapter(private var barList: List<BarData>) :
    RecyclerView.Adapter<BarAdapter.BarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarViewHolder {
        val binding = HotelBarItemsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BarViewHolder(binding)
    }

    fun updateList(list: List<BarData>) {
        barList = list
    }

    override fun onBindViewHolder(holder: BarViewHolder, position: Int) {
        val barItem = barList[position]


        holder.binding.restaurantsName.text = barItem.restaurantName
        holder.binding.ratingText.text = barItem.rating.toString()
        holder.binding.barAddress.text = barItem.address
        holder.binding.barDistance.text = barItem.distance
        holder.binding.foodType1.text = barItem.cuisines1
        holder.binding.foodType2.text = barItem.cuisines2
        holder.binding.cost.text = barItem.costForTwo

        val imageList = barItem.sliderImages.map { SlideModel(it) }
        holder.binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)

    }

    override fun getItemCount(): Int = barList.size



    class BarViewHolder(val binding: HotelBarItemsBinding) : RecyclerView.ViewHolder(binding.root)
}
