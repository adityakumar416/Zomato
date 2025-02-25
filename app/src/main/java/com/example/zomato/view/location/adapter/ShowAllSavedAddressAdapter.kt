package com.test.zomato.view.location.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.test.zomato.R
import com.test.zomato.databinding.SavedAddressItemBinding
import com.test.zomato.view.location.interfaces.AddressMenuClickListener
import com.test.zomato.view.location.models.UserSavedAddress

class ShowAllSavedAddressAdapter(
    private val addressMenuClickListener: AddressMenuClickListener,
    private val showAddressType: String
) :
    RecyclerView.Adapter<ShowAllSavedAddressAdapter.ViewHolder>() {

    private var addressList = listOf<UserSavedAddress>()

    class ViewHolder(val binding: SavedAddressItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            SavedAddressItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = addressList[position]

        if (showAddressType=="selectedLocationForOrder"){
            holder.binding.orderDeliversToText.visibility = View.VISIBLE
        }else{
            holder.binding.orderDeliversToText.visibility = View.GONE

        }

        holder.binding.PlaceName.text = address.saveAddressAs
        holder.binding.location.text = "${address.houseAddress}, ${address.selectedLocation}"
        
        holder.binding.receiverNumber.visibility = View.GONE

        if (!address.receiverNumber.isNullOrEmpty()){
            holder.binding.receiverNumber.visibility = View.VISIBLE
            holder.binding.receiverNumber.text = "Phone number: ${address.receiverNumber}"
        }

        holder.itemView.setOnClickListener {
            addressMenuClickListener.addressClick(address)
        }

        // show menu to delete and edit the address
        holder.binding.menuIcon.setOnClickListener {
            val popupMenu = PopupMenu(holder.itemView.context, holder.binding.menuIcon)
            popupMenu.inflate(R.menu.address_menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_edit -> {
                        addressMenuClickListener.menuClick(address, "edit")
                        true
                    }

                    R.id.menu_delete -> {
                        addressMenuClickListener.menuClick(address, "delete")
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }

        holder.binding.shareIcon.setOnClickListener {
            val addressDetails = "${address.houseAddress}, ${address.selectedLocation}"

            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                addressDetails
            )
            shareIntent.type = "text/plain"

            holder.itemView.context.startActivity(
                Intent.createChooser(
                    shareIntent,
                    "Share address via"
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    fun updateAddresses(newAddresses: List<UserSavedAddress>) {
        addressList = newAddresses
    }
}
