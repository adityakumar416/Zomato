package com.test.zomato.view.main.home.bottomSheets

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.test.zomato.R
import com.test.zomato.databinding.FragmentAddFoodInCartBottomSheetBinding
import com.test.zomato.cartDB.CartAndOrderViewModel
import com.test.zomato.view.cart.interfaces.OnBottomSheetActionListener
import com.test.zomato.view.main.home.models.FoodItem
import com.test.zomato.viewModels.MainViewModel

class AddFoodInCartBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAddFoodInCartBottomSheetBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var roomDbViewModel: CartAndOrderViewModel
    private var foodItem: FoodItem? = null

    private var listener: OnBottomSheetActionListener? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddFoodInCartBottomSheetBinding.inflate(inflater, container, false)

        // Initialize ViewModel
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        roomDbViewModel = ViewModelProvider(this)[CartAndOrderViewModel::class.java]

        foodItem = arguments?.getParcelable("foodItem")

        if (foodItem != null) {

            foodItem?.let {
                // Fetch the current quantity from the database if the food item already exists in the cart
                it.id.let { foodItemId ->
                    // Observe the food item with the given ID
                    roomDbViewModel.getFoodItemById(foodItemId).observe(this, Observer { existingFoodItem ->

                        // Get the current quantity of the food item
                      //  count = existingFoodItem?.foodQuantity ?: 1

                       /* if (count == 0) {
                            dismiss()
                        }*/

                        // If count is greater than zero, update the quantity in the ViewModel
                      /*  if (count > 0) {
                            mainViewModel.setInitialQuantity(count)
                        } else {
                            // If the count is zero or negative, delete the food item and dismiss the view
                            roomDbViewModel.deleteFoodItemById(foodItemId)
                            dismiss()
                        }*/

                    })
                }


                // Set the basic details of the food item
                binding.foodNameIs.text = it.foodName
                binding.foodDiscriptionIs.text = it.foodDescription
                binding.ratingCount.text = "(${it.totalRatingCount})"
                binding.foodRating.rating = it.foodRating.toFloat() ?: 0f

                // Check food type (Veg/Non-Veg)
                if (foodItem?.foodType == "Veg") {
                    binding.veg.setImageResource(R.drawable.pure_veg_icon)
                } else {
                    binding.veg.setImageResource(R.drawable.non_veg_icon)
                }

                Glide.with(requireActivity())
                    .load(foodItem?.foodImage)
                    .into(binding.foodImage)


                // Increment the food quantity
                binding.incrementCount.setOnClickListener {
                    mainViewModel.plus()
                }

                // Decrement the food quantity
                binding.decrementCount.setOnClickListener {
                    mainViewModel.minus()
                }

                // Observe count changes and update the UI
                mainViewModel.count.observe(viewLifecycleOwner, Observer { newCount ->

                    if (newCount == 0) {
                        dismiss()
                    }

                    binding.continueButton.text = "Add item ₹${newCount * it.foodPrice.toInt()}"
                    binding.totalFoodCount.text = newCount.toString()

                    // Update the food item quantity in the database when count changes
                    roomDbViewModel.changeFoodQuantity(newCount, it.id)

                })

                // When continue button is clicked, use the refactored method to add food item to the cart
                binding.continueButton.setOnClickListener {
                    onContinueButtonClick()
                }
            }
            }


        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // Notify parent activity
        listener?.onBottomSheetDismissed()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnBottomSheetActionListener) {
            listener = context
        }
    }

    // this method to handle the continue button click
    private fun onContinueButtonClick() {
        val updatedFoodItem = foodItem?.copy(foodQuantity = mainViewModel.count.value ?: 0)

        if (updatedFoodItem != null && updatedFoodItem.foodQuantity > 0) {
            roomDbViewModel.addFoodItemToCart(updatedFoodItem)
            // roomDbViewModel.addRestaurantToCart(restaurantDetails!!)
        }

        listener?.onFoodItemAdded()
        dismiss()
    }

}
