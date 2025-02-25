package com.test.zomato.view.main.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.test.zomato.databinding.WhatsInYourMindItemBinding
import com.test.zomato.view.main.home.models.WhatsOnYourMindItemData

class WhatsOnYourMindItemAdapter(private val itemList: List<WhatsOnYourMindItemData>) : RecyclerView.Adapter<WhatsOnYourMindItemAdapter.WhatsOnYourMindItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WhatsOnYourMindItemViewHolder {
        val binding = WhatsInYourMindItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WhatsOnYourMindItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WhatsOnYourMindItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

     class WhatsOnYourMindItemViewHolder(private val binding: WhatsInYourMindItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WhatsOnYourMindItemData) {
            Glide.with(binding.itemImage1.context)
                .load(item.image1)
                .into(binding.itemImage1)

            Glide.with(binding.itemImage2.context)
                .load(item.image2)
                .into(binding.itemImage2)

            binding.itemText1.text = item.itemName1
            binding.itemText2.text = item.itemName2
        }
    }
}
