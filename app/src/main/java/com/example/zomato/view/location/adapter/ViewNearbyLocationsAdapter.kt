package com.test.zomato.view.location.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.zomato.databinding.NearbyLocationsItemLayoutBinding
import com.test.zomato.view.location.models.PlaceFeature
import com.test.zomato.view.location.models.PlacesResponse

class ViewNearbyLocationsAdapter:RecyclerView.Adapter<ViewNearbyLocationsAdapter.ViewHolder>() {

     var nearLocationList =  mutableListOf<PlaceFeature>()

    class ViewHolder( val binding:NearbyLocationsItemLayoutBinding):RecyclerView.ViewHolder(binding.root) {

    }

    fun updateList(nearLocationList: MutableList<PlaceFeature>){
        this.nearLocationList = nearLocationList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NearbyLocationsItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return nearLocationList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nearLocation = nearLocationList[position]
        
        holder.binding.PlaceName.text = nearLocation.properties.name
        holder.binding.location.text = nearLocation.properties.formatted
    }
}