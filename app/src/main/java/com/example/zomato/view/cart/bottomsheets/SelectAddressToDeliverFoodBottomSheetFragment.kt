package com.test.zomato.view.cart.bottomsheets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.test.zomato.databinding.FragmentSelectAddressToDeliverFoodBottomSheetBinding
import com.test.zomato.utils.AppSharedPreferences
import com.test.zomato.utils.MyHelper
import com.test.zomato.utils.PrefKeys
import com.test.zomato.view.cart.ShowCartFoodDetailsActivity
import com.test.zomato.view.location.AddLocationFromMapActivity
import com.test.zomato.view.location.adapter.ShowAllSavedAddressAdapter
import com.test.zomato.view.location.interfaces.AddressMenuClickListener
import com.test.zomato.view.location.models.UserSavedAddress
import com.test.zomato.view.main.home.interfaces.SelectAddressClickListener
import com.test.zomato.viewModels.MainViewModel

class SelectAddressToDeliverFoodBottomSheetFragment : BottomSheetDialogFragment(),
    AddressMenuClickListener {

    private lateinit var binding: FragmentSelectAddressToDeliverFoodBottomSheetBinding
    private lateinit var myHelper:MyHelper

    private var listner :SelectAddressClickListener? = null

    private lateinit var mainViewModel: MainViewModel
    private lateinit var addressAdapter: ShowAllSavedAddressAdapter

    private val appSharedPreferences by lazy { activity?.let { AppSharedPreferences.getInstance(it) } }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listner = context as ShowCartFoodDetailsActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectAddressToDeliverFoodBottomSheetBinding.inflate(inflater, container, false)

        myHelper = MyHelper(requireActivity())

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val address = myHelper.extractAddressDetails(myHelper.getLatitude(),myHelper.getLongitude())

       // binding.location.text = address?.fullAddress

     /*   binding.addressCard.setOnClickListener {
            listner?.addressSelectedNowPlaceTheOrder()
            dismiss()
        }*/

        val isSkipBtnClick = appSharedPreferences?.getBoolean(PrefKeys.SKIP_BTN_CLICK)

        if (isSkipBtnClick == true) {
            binding.blinketCard.visibility = View.GONE
        }else{
            binding.blinketCard.visibility = View.GONE
        }

        binding.addLocationCard.setOnClickListener {
            startActivity(Intent(activity,AddLocationFromMapActivity::class.java))
        }

        showAllSavedAddresses()

        return binding.root
    }
    private fun showAllSavedAddresses() {
        addressAdapter = ShowAllSavedAddressAdapter(this,"selectedLocationForOrder")
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = addressAdapter

        mainViewModel.getAllAddresses(myHelper.numberIs())
        mainViewModel.addresses.observe(this) { addresses ->

            if (addresses.isEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.dividerView3.visibility = View.GONE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.dividerView3.visibility = View.VISIBLE
                addressAdapter.updateAddresses(addresses)
                addressAdapter.notifyDataSetChanged()
            }

        }
    }

    override fun menuClick(address: UserSavedAddress, action: String) {

    }

    override fun addressClick(userSavedAddress: UserSavedAddress) {
        listner?.addressSelectedNowPlaceTheOrder(userSavedAddress)
        dismiss()
    }

}
