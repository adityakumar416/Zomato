package com.test.zomato.view.main.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.test.zomato.databinding.ItemExploreBinding
import com.test.zomato.view.main.home.models.ExploreData

class ExploreItemAdapter(private val exploreList: List<ExploreData>) : RecyclerView.Adapter<ExploreItemAdapter.ExploreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreViewHolder {
        val binding = ItemExploreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExploreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExploreViewHolder, position: Int) {
        val exploreData = exploreList[position]
        holder.bind(exploreData)
    }

    override fun getItemCount(): Int = exploreList.size

    class ExploreViewHolder(private val binding: ItemExploreBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(exploreData: ExploreData) {

            Glide.with(binding.imageView.context)
                .load(exploreData.image)
                .into(binding.imageView)

            binding.textView.text = exploreData.name
        }
    }
}
