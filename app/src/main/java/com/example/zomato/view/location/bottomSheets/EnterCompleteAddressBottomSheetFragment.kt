package com.test.zomato.view.location.bottomSheets

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.card.MaterialCardView
import com.test.zomato.R
import com.test.zomato.databinding.FragmentEnterCompleteAddressBottomSheetBinding
import com.test.zomato.utils.AppSharedPreferences
import com.test.zomato.utils.MyHelper
import com.test.zomato.utils.PrefKeys
import com.test.zomato.view.location.AddLocationFromMapActivity
import com.test.zomato.view.location.interfaces.SavedAddressClickListener
import com.test.zomato.view.login.repository.UserViewModel
import com.test.zomato.viewModels.MainViewModel

class EnterCompleteAddressBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentEnterCompleteAddressBottomSheetBinding
    private lateinit var mainViewModel: MainViewModel
    private var selectedAddressType: String = "Home" // Default to "Home"
    private var addressId: Int? = null
    private val myHelper by lazy { activity?.let { MyHelper(it) } }
    private lateinit var userViewModel: UserViewModel

    private var eventListener: SavedAddressClickListener? = null
    private val appSharedPreferences by lazy { activity?.let { AppSharedPreferences.getInstance(it) } }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventListener = context as AddLocationFromMapActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnterCompleteAddressBottomSheetBinding.inflate(inflater, container, false)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]


        val selectedAddress = arguments?.getString("address")

        addressId = arguments?.getInt("addressId", -1)

        //  Toast.makeText(requireActivity(), "$addressId", Toast.LENGTH_SHORT).show()

        // Set the selected address in the TextView
        binding.selectedAddress.text = selectedAddress ?: "Address not available"

        myHelper?.let { fetchUserData(it.numberIs()) }

        binding.selectedAddressLayout.setOnClickListener {
            dismiss()
        }

        binding.EnterCompleteAddressLayout.visibility = View.VISIBLE
        binding.bottomBtnCard.visibility = View.VISIBLE
        binding.confirmYourAddressLayout.visibility = View.GONE
        binding.bottomBtnCard2.visibility = View.GONE

        val isSkipBtnClick = appSharedPreferences?.getBoolean(PrefKeys.SKIP_BTN_CLICK)

        // if user click on skip btn
        if (isSkipBtnClick == true) {

            binding.areaSectorLocationEditText.setText(selectedAddress)

            binding.receiverDetailsLayout.visibility = View.GONE
            binding.selectedAddressLayout.visibility = View.GONE
            binding.text5.visibility = View.GONE
            binding.areaSectorLocationEditTextLayout.visibility = View.VISIBLE

            binding.confirmAddress.text = "Save address"

            binding.confirmAddress.setOnClickListener {
                val selectedLocation = binding.areaSectorLocationEditText.text.toString()
                val houseAddress = binding.flatHouseNoFloorBuilding.text.toString()
                val nearbyLandmark = binding.emailInput.text.toString()

                if (houseAddress.isNullOrEmpty()) {
                    binding.flatHouseNoFloorBuilding.requestFocus()
                    Toast.makeText(activity, "Enter correct House Address.", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                } else if (selectedLocation.isNullOrEmpty()) {
                    binding.areaSectorLocationEditText.requestFocus()
                    Toast.makeText(activity, "Enter correct address.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {
                    // If addressId is present, update the existing address, otherwise insert a new one
                    if (addressId != null && addressId != -1) {
                        myHelper?.numberIs()?.let { it1 ->
                            mainViewModel.updateAddress(
                                addressId!!, "", "", it1, selectedAddressType,
                                selectedLocation, houseAddress, nearbyLandmark
                            )
                        }
                    } else {
                        // Save the address using the ViewModel
                        myHelper?.numberIs()?.let { it1 ->
                            mainViewModel.saveAddress(
                                "", "", it1, selectedAddressType,
                                selectedLocation, houseAddress, nearbyLandmark
                            )
                        }
                    }
                }

                eventListener?.addressSave()
                dismiss()
            }

        } else {

            // if user is registered
            binding.text5.visibility = View.VISIBLE
            binding.selectedAddressLayout.visibility = View.VISIBLE
            binding.areaSectorLocationEditTextLayout.visibility = View.GONE
            binding.receiverDetailsLayout.visibility = View.VISIBLE


            // saving user details and address
            binding.saveAddressInDb.setOnClickListener {
                val receiverName = binding.receiverName.text.toString()
                val receiverNumber = binding.receiverNumber.text.toString()
                val selectedLocation = binding.selectedAddress.text.toString()
                val houseAddress = binding.flatHouseNoFloorBuilding.text.toString()
                val nearbyLandmark = binding.emailInput.text.toString()

                // If addressId is present, update the existing address, otherwise insert a new one
                if (addressId != null && addressId != -1) {
                    myHelper?.numberIs()?.let { it1 ->
                        mainViewModel.updateAddress(
                            addressId!!, receiverName, receiverNumber, it1, selectedAddressType,
                            selectedLocation, houseAddress, nearbyLandmark
                        )
                    }
                } else {
                    // Save the address using the ViewModel
                    myHelper?.numberIs()?.let { it1 ->
                        mainViewModel.saveAddress(
                            receiverName, receiverNumber, it1, selectedAddressType,
                            selectedLocation, houseAddress, nearbyLandmark
                        )
                    }


                }

                eventListener?.addressSave()
                dismiss()
            }

            binding.changeAddress.setOnClickListener {
                binding.EnterCompleteAddressLayout.visibility = View.VISIBLE
                binding.bottomBtnCard.visibility = View.VISIBLE
                binding.confirmYourAddressLayout.visibility = View.GONE
                binding.bottomBtnCard2.visibility = View.GONE
            }

            binding.confirmAddress.setOnClickListener {

                val receiverName = binding.receiverName.text.toString()
                val receiverNumber = binding.receiverNumber.text.toString()
                val selectedLocation = binding.selectedAddress.text.toString()
                val houseAddress = binding.flatHouseNoFloorBuilding.text.toString()
                val nearbyLandmark = binding.emailInput.text.toString()


                if (binding.receiverDetails.visibility == View.VISIBLE && receiverNumber.length != 10) {
                    binding.receiverNumber.requestFocus()
                    Toast.makeText(activity, "Enter correct number.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else if (houseAddress.isNullOrEmpty()) {
                    binding.flatHouseNoFloorBuilding.requestFocus()
                    Toast.makeText(activity, "Enter House Address.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {

                    binding.EnterCompleteAddressLayout.visibility = View.GONE
                    binding.bottomBtnCard.visibility = View.GONE
                    binding.bottomBtnCard2.visibility = View.VISIBLE
                    binding.confirmYourAddressLayout.visibility = View.VISIBLE

                    binding.confirmLocationText.text = "Confirm your Address"
                    binding.deliveryAt.text = "Delivery at $selectedAddressType"
                    binding.address.text = "$houseAddress, $selectedLocation, $nearbyLandmark"

                    if (binding.userDetailsCard.visibility == View.VISIBLE || binding.receiverDetails.visibility == View.VISIBLE) {
                        binding.confirmReceiverDetails.visibility = View.VISIBLE
                        binding.confirmUserName.text = "$receiverName,"
                        binding.confirmUserNumber.text = receiverNumber
                    } else {
                        binding.confirmReceiverDetails.visibility = View.GONE
                    }

                }
            }

        }

        // Set click listeners for each card
        setCardClickListeners()

        // Set the default selected card (Home card)
        onCardClick(
            card = binding.home,
            iconId = R.id.home_icon,
            textId = R.id.home_text
        )

        binding.closeButton.setOnClickListener {
            dismiss()
        }

        binding.userDetailsCard.setOnClickListener {
            binding.userDetailsCard.visibility = View.GONE
            binding.receiverDetails.visibility = View.VISIBLE
        }

        return binding.root
    }

    private fun fetchUserData(userPhoneNumber: String) {
        if (userPhoneNumber.isNotEmpty()) {
            userViewModel.getUserByPhoneNumber(userPhoneNumber)

            userViewModel.userLiveData.observe(this) { user ->

                user?.let {
                    Log.d("userDataProfile", "fetchUserData: $user")

                    if (!user.username.isNullOrEmpty() && !user.username.isNullOrEmpty()) {
                        binding.userDetailsCard.visibility = View.VISIBLE
                        binding.receiverNumber.setText(user.userNumber)
                        binding.receiverName.setText(user.username)

                        binding.userName.text = "${user.username} , "
                        binding.userNumber.text = user.userNumber
                    } else {
                        binding.userDetailsCard.visibility = View.GONE
                    }

                }
            }
        }
    }

    private fun setCardClickListeners() {
        binding.home.setOnClickListener {
            selectedAddressType = "Home"
            onCardClick(
                card = binding.home,
                iconId = R.id.home_icon,
                textId = R.id.home_text
            )
        }

        binding.work.setOnClickListener {
            selectedAddressType = "Work"
            onCardClick(
                card = binding.work,
                iconId = R.id.work_icon,
                textId = R.id.work_text
            )
        }

        binding.hotel.setOnClickListener {
            selectedAddressType = "Hotel"
            onCardClick(
                card = binding.hotel,
                iconId = R.id.hotel_icon,
                textId = R.id.hotel_text
            )
        }

        binding.other.setOnClickListener {
            selectedAddressType = "Other"
            onCardClick(
                card = binding.other,
                iconId = R.id.other_icon,
                textId = R.id.other_text
            )
        }
    }

    /*    private fun setBottomSheetSize() {
            // Fix the Bottom Sheet size (you can adjust these values as per your requirements)
            val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val layoutParams = it.layoutParams
                layoutParams.height = resources.getDimensionPixelSize("400dp")  // Set fixed height
                layoutParams.width = resources.getDimensionPixelSize(m)   // Set fixed width
                it.layoutParams = layoutParams
            }
        }*/

    /*  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
          return super.onCreateDialog(savedInstanceState).apply {
              (this as? BottomSheetDialog)
                  ?.behavior
                  ?.setPeekHeight(BottomSheetBehavior.STATE_COLLAPSED, true)
          }
      }*/

    private fun onCardClick(card: MaterialCardView, iconId: Int, textId: Int) {
        // First, reset all the cards to default state
        resetAllCards()

        // Now, update the clicked card's appearance
        card.setCardBackgroundColor(Color.parseColor("#FFF5F6"))
        card.strokeColor = resources.getColor(R.color.colorPrimary)

        // Change the icon and text color to the primary color
        val icon = card.findViewById<ImageView>(iconId)
        val text = card.findViewById<TextView>(textId)

        icon?.setColorFilter(resources.getColor(R.color.colorPrimary))
        text?.setTextColor(resources.getColor(R.color.colorPrimary))
    }

    private fun resetAllCards() {
        // Reset all cards to their default state
        resetCard(binding.home, R.id.home_icon, R.id.home_text)
        resetCard(binding.work, R.id.work_icon, R.id.work_text)
        resetCard(binding.hotel, R.id.hotel_icon, R.id.hotel_text)
        resetCard(binding.other, R.id.other_icon, R.id.other_text)
    }

    private fun resetCard(card: MaterialCardView, iconId: Int, textId: Int) {
        // Reset the background color, icon color, and text color of the card
        card.setCardBackgroundColor(Color.WHITE)
        card.strokeColor = Color.GRAY

        val icon = card.findViewById<ImageView>(iconId)
        val text = card.findViewById<TextView>(textId)

        icon?.setColorFilter(Color.BLACK)
        text?.setTextColor(Color.BLACK)
    }
}
