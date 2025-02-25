package com.test.zomato.view.cart

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.test.zomato.R
import com.test.zomato.cartDB.CartAndOrderViewModel
import com.test.zomato.databinding.ActivityShowOrderFoodDetailsBinding
import com.test.zomato.utils.AppSharedPreferences
import com.test.zomato.utils.MyHelper
import com.test.zomato.utils.PrefKeys
import com.test.zomato.view.cart.adapter.FoodItemInCartAdapter
import com.test.zomato.view.cart.bottomsheets.OrderPlaceOrNotBottomSheetFragment
import com.test.zomato.view.cart.bottomsheets.SelectAddressToDeliverFoodBottomSheetFragment
import com.test.zomato.view.cart.interfaces.AddFoodClickListener
import com.test.zomato.view.location.AddLocationFromMapActivity
import com.test.zomato.view.location.models.UserSavedAddress
import com.test.zomato.view.login.UserSignUpActivity
import com.test.zomato.view.login.cartSkipUserLogin.MobileNumberLoginWithSkipActivity
import com.test.zomato.view.login.repository.UserViewModel
import com.test.zomato.view.main.MainActivity
import com.test.zomato.view.main.home.interfaces.SelectAddressClickListener
import com.test.zomato.view.main.home.models.FoodItem
import com.test.zomato.view.main.home.models.RestaurantDetails
import com.test.zomato.view.orders.interfaces.OrderPlcaeClickListener
import com.test.zomato.view.orders.orderModels.FoodItemInOrder
import com.test.zomato.view.orders.orderModels.OrderDetails
import com.test.zomato.viewModels.MainViewModel

class ShowCartFoodDetailsActivity : AppCompatActivity(), AddFoodClickListener,
    SelectAddressClickListener, OrderPlcaeClickListener {

    private lateinit var binding: ActivityShowOrderFoodDetailsBinding
    private var restaurantDetails: RestaurantDetails? = null
    private lateinit var roomDbViewModel: CartAndOrderViewModel
    private val myHelper by lazy { MyHelper(this) }
    private var isCouponApplied = false
    private lateinit var userViewModel: UserViewModel
    private lateinit var mainViewModel: MainViewModel
    private var userSavedAddress:UserSavedAddress?= null

    private var selectedAddress: String? = null
    private val appSharedPreferences by lazy { AppSharedPreferences.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityShowOrderFoodDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("Adityatag", "onCreate: ShowOrderFoodDetailsActivity")

        roomDbViewModel = ViewModelProvider(this)[CartAndOrderViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        window.statusBarColor = Color.WHITE
        // Get the restaurant details from the Intent
        restaurantDetails = intent.getParcelableExtra("restaurantDetails")


        val isSkipBtnClick = appSharedPreferences?.getBoolean(PrefKeys.SKIP_BTN_CLICK)

        if (isSkipBtnClick == true) {

            val skipUserNumber = appSharedPreferences?.getString(PrefKeys.SKIP_USER_NUMBER)
            val skipUserName = appSharedPreferences?.getString(PrefKeys.SKIP_USER_NAME)

            if (skipUserNumber.isNullOrEmpty() && skipUserName.isNullOrEmpty()) {

                binding.selectAddressForOrder.text = "Add personal details"

                binding.skipDeliveryAddressLayout.visibility = View.GONE
                binding.skipUserDetailsLayout.visibility = View.GONE

                binding.selectAddressForOrder.setOnClickListener {
                    startActivity(Intent(this, MobileNumberLoginWithSkipActivity::class.java))
                }

            } else {

                mainViewModel.getAllAddresses(myHelper.numberIs())
                mainViewModel.addresses.observe(this) { addresses ->
                    binding.selectAddressForOrder.setOnClickListener {
                        if (addresses.isNullOrEmpty()) {
                            // If no addresses are available, then navigato to add address activity
                            startActivity(
                                Intent(
                                    this,
                                    AddLocationFromMapActivity::class.java
                                )
                            )
                        } else {
                            // If addresses exist, show the bottom sheet fragment
                            val selectAddressToDeliverFoodBottomSheetFragment =
                                SelectAddressToDeliverFoodBottomSheetFragment()
                            selectAddressToDeliverFoodBottomSheetFragment.show(
                                supportFragmentManager,
                                "selectAddressToDeliverFoodBottomSheetFragment"
                            )
                        }
                    }
                }


                binding.skipUserDetailsLayout.visibility = View.VISIBLE

                binding.skipUserDetailsLayout.setOnClickListener {
                    startActivity(Intent(this, MobileNumberLoginWithSkipActivity::class.java))
                }


                binding.selectAddressForOrder.text = "Add Address at next step"

                binding.skipUserDetails.text = "${skipUserName},${skipUserNumber}"


            }


            binding.text8.text = "Log in to user coupons"
            binding.text11.visibility = View.GONE
            binding.dottedLine2.visibility = View.GONE
            binding.applyCouponLayout.visibility = View.GONE
            binding.discountLayout.visibility = View.GONE
            binding.share.visibility = View.GONE
            binding.zomatoMoneyBalanceLayout.visibility = View.GONE
            binding.youSavedText.visibility = View.GONE

            binding.offerAutoapplied.setOnClickListener {
                startActivity(Intent(this, UserSignUpActivity::class.java))
            }

        } else {

            binding.youSavedText.visibility = View.VISIBLE

            binding.skipUserDetailsLayout.visibility = View.GONE
            binding.skipDeliveryAddressLayout.visibility = View.GONE


            binding.text8.text =
                "You saved ₹${restaurantDetails?.recommendedFoodList?.get(0)?.foodOffer} on delivery"
            binding.selectAddressForOrder.text = "Select Address at next step"
            binding.text11.visibility = View.VISIBLE
            binding.applyCouponLayout.visibility = View.VISIBLE
            binding.discountLayout.visibility = View.VISIBLE
            binding.share.visibility = View.VISIBLE
            binding.zomatoMoneyBalanceLayout.visibility = View.VISIBLE

            binding.selectAddressForOrder.setOnClickListener {
                val selectAddressToDeliverFoodBottomSheetFragment =
                    SelectAddressToDeliverFoodBottomSheetFragment()
                selectAddressToDeliverFoodBottomSheetFragment.show(
                    supportFragmentManager,
                    "selectAddressToDeliverFoodBottomSheetFragment"
                )
            }

        }

        Glide.with(this)
            .load(R.drawable.party_emoji)
            .into(binding.emojiIcon)


        binding.backButton.setOnClickListener {
            finish()
        }

        binding.addItems.setOnClickListener {
            finish()
        }

        binding.applyCoupons.setOnClickListener {
            // Apply the coupon, reduce ₹62 from the total
            if (!isCouponApplied) {
                isCouponApplied = true
                updateTotalPriceWithCoupon(true)
                binding.applyCoupons.visibility = View.GONE
                binding.removeApplyCoupons.visibility = View.VISIBLE
            }
        }

        binding.removeApplyCoupons.setOnClickListener {
            // Remove the coupon, restore ₹62 to the total
            if (isCouponApplied) {
                isCouponApplied = false
                updateTotalPriceWithCoupon(false)
                binding.applyCoupons.visibility = View.VISIBLE
                binding.removeApplyCoupons.visibility = View.GONE
            }
        }


        // Set restaurant details
        restaurantDetails?.apply {

            binding.discountText.text =
                "You saved ₹${recommendedFoodList[0].foodOffer} on this order"

            binding.restaurantName.text = restaurantName
            binding.restaurantNameSelectedAddress.text = restaurantName

            binding.text11.text = "Auto-applied as your order is above ₹199"
            binding.deliveryTime.text = "Delivery in $timeToReach"
            binding.deliveryLocation.visibility = View.GONE
            binding.bottomLayout2.visibility = View.GONE
            binding.userDetailsCard.visibility = View.GONE
            binding.selectAddressForOrder.visibility = View.VISIBLE
            // Set total bill
            updateTotalPrice(recommendedFoodList)
        }

        fetchAllFoodAndUpdateAdapter()




        binding.afterSelectedAddressToolbar.setOnClickListener {
            val selectAddressToDeliverFoodBottomSheetFragment =
                SelectAddressToDeliverFoodBottomSheetFragment()
            selectAddressToDeliverFoodBottomSheetFragment.show(
                supportFragmentManager,
                "selectAddressToDeliverFoodBottomSheetFragment"
            )
        }

        binding.deliveryLocation.setOnClickListener {
            val selectAddressToDeliverFoodBottomSheetFragment =
                SelectAddressToDeliverFoodBottomSheetFragment()
            selectAddressToDeliverFoodBottomSheetFragment.show(
                supportFragmentManager,
                "selectAddressToDeliverFoodBottomSheetFragment"
            )
        }

        fetchUserData(myHelper.numberIs())

    }

    private fun fetchUserData(userPhoneNumber: String) {
        if (userPhoneNumber.isNotEmpty()) {
            userViewModel.getUserByPhoneNumber(userPhoneNumber)

            userViewModel.userLiveData.observe(this) { user ->
                user?.let {
                    Log.d("userDataProfile", "fetchUserData: $user")

                    if (user.username.isNullOrEmpty() && user.userNumber.isNullOrEmpty()) {
                        binding.userDetailsCard.visibility = View.GONE
                    } else {
                        if (user.username.isNullOrEmpty()) {
                            binding.userNumber.text = user.userNumber
                        } else {
                            binding.userName.text = "${user.username}, "
                            binding.userNumber.text = user.userNumber
                        }

                        binding.userDetailsCard.visibility = View.VISIBLE
                    }


                }
            }
        }

    }


    private fun updateTotalPriceWithCoupon(applyCoupon: Boolean) {
        val foodItems =
            roomDbViewModel.fetchFoodItemsInCart.value?.filter { it.restaurantId == restaurantDetails?.id }
                ?: emptyList()

        val totalPrice = foodItems.sumOf { it.foodPrice.toDouble() * it.foodQuantity }
        val totalSaved = foodItems.sumOf { it.foodOffer.toDouble() }

        // If the coupon is applied, reduce ₹62 from the total price
        val finalPrice = if (applyCoupon) {
            totalPrice - 62.0 // Apply coupon
        } else {
            totalPrice // No coupon applied
        }

        val remainingPrice = finalPrice - totalSaved

        binding.totalFoodPriceInButton.text = finalPrice.toInt().toString()
        binding.totalBill.text = "Total bill ₹${finalPrice.toInt()} - ₹${remainingPrice.toInt()}"
        binding.youSavedText.text = "You saved ₹${totalSaved}"
    }


    private fun fetchAllFoodAndUpdateAdapter() {
        // Observe changes in food items in cart
        roomDbViewModel.fetchAndUpdateCartItems()

        roomDbViewModel.fetchFoodItemsInCart.observe(this, Observer { foodItems ->
            val currentRestaurantFoodItems =
                foodItems.filter { it.restaurantId == restaurantDetails?.id }

            val foodList = currentRestaurantFoodItems.filter { it.foodQuantity > 0 }

            val totalQuantity = currentRestaurantFoodItems.sumOf { it.foodQuantity }

            Log.d(
                "FoodOrderDetailsQuantity",
                "Total items in cart for restaurant: ${totalQuantity}"
            )


            // If cart is empty, finish the activity
            if (totalQuantity == 0) {
                finish()
                // roomDbViewModel.fetchAndUpdateCartItems()
            }

            if (foodList.isNotEmpty()) {
                // Create the adapter for cart items
                val foodItemAdapter = FoodItemInCartAdapter(foodList, this)
                binding.cartFoodRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.cartFoodRecyclerView.adapter = foodItemAdapter

                // When the food items are updated (quantity changes), recalculate the total bill
                updateTotalPrice(currentRestaurantFoodItems)
            } else {
                Toast.makeText(this, "No items in the cart", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateTotalPrice(foodItems: List<FoodItem>) {
        val totalPrice = foodItems.sumOf { it.foodPrice.toDouble() * it.foodQuantity }
        val totalSaved = foodItems.sumOf { it.foodOffer.toDouble() }
        val remainingPrice = totalPrice - totalSaved

        binding.totalFoodPriceInButton.text = totalPrice.toInt().toString()

        // Directly updating the TextViews with simple string formatting
        binding.totalBill.text = "Total bill ₹${totalPrice.toInt()} - ₹${remainingPrice.toInt()}"
        binding.youSavedText.text = "You saved ₹${totalSaved}"
    }

    override fun onFoodClick(foodItem: FoodItem) {
    }

    override fun onFoodQuantityChange(foodItem: FoodItem) {
        // Check if the quantity of the food item is 0
        if (foodItem.foodQuantity == 0) {
            roomDbViewModel.changeFoodQuantity(foodItem.foodQuantity, foodItem.id)
            //  roomDbViewModel.deleteFoodItemById(foodItem.id)
            roomDbViewModel.fetchAndUpdateCartItems()
        } else {
            // Update food quantity in the database
            roomDbViewModel.changeFoodQuantity(foodItem.foodQuantity, foodItem.id)
            roomDbViewModel.fetchAndUpdateCartItems()
        }

    }


    override fun addressSelectedNowPlaceTheOrder(userSavedAddress: UserSavedAddress) {

        this.userSavedAddress = userSavedAddress

        val isSkipBtnClick = appSharedPreferences?.getBoolean(PrefKeys.SKIP_BTN_CLICK)

        if (isSkipBtnClick == true) {

            binding.share.visibility = View.VISIBLE
            binding.skipDeliveryAddressLayout.visibility = View.VISIBLE
            binding.afterSelectedAddressToolbar.visibility = View.VISIBLE
            binding.showAddress.visibility = View.VISIBLE
            binding.restaurantName.visibility = View.GONE

            selectedAddress =
                "${userSavedAddress.houseAddress},${userSavedAddress.selectedLocation},${userSavedAddress.nearbyLandmark}"

            binding.skipDeliveryType.text = "Delivery at ${userSavedAddress.saveAddressAs}"
            binding.addressType.text = "Delivery at ${userSavedAddress.saveAddressAs}"

            binding.skipAddress.text = selectedAddress
            binding.userSelectedLocation.text = selectedAddress

            binding.selectAddressForOrder.text = "Place Order"


            binding.selectAddressForOrder.setOnClickListener {
                // Fetch food items from the cart that have quantity > 0
                confermOrderPlace()

            }

            binding.skipDeliveryAddressLayout.setOnClickListener {
                val selectAddressToDeliverFoodBottomSheetFragment =
                    SelectAddressToDeliverFoodBottomSheetFragment()
                selectAddressToDeliverFoodBottomSheetFragment.show(
                    supportFragmentManager,
                    "selectAddressToDeliverFoodBottomSheetFragment"
                )
            }


        }else {


            binding.afterSelectedAddressToolbar.visibility = View.VISIBLE
            binding.showAddress.visibility = View.VISIBLE
            binding.restaurantName.visibility = View.GONE

            binding.deliveryLocation.visibility = View.VISIBLE
            // binding.userDetailsCard.visibility = View.VISIBLE
            binding.bottomLayout2.visibility = View.VISIBLE
            binding.selectAddressForOrder.visibility = View.GONE

            /*val address =
            myHelper.extractAddressDetails(myHelper.getLatitude(), myHelper.getLongitude())
        */

            selectedAddress =
                "${userSavedAddress.houseAddress},${userSavedAddress.selectedLocation},${userSavedAddress.nearbyLandmark}"

            binding.location.text = selectedAddress
            binding.userSelectedLocation.text = selectedAddress

            binding.addressType.text = "Delivery at ${userSavedAddress.saveAddressAs}"

            binding.restaurantNameSelectedAddress.text = restaurantDetails?.restaurantName


            binding.placeOrder.setOnClickListener {
                confermOrderPlace()
            }
        }
    }

    private fun confermOrderPlace() {

        if (selectedAddress == null) {
            Toast.makeText(this, "Address not available", Toast.LENGTH_SHORT).show()
        }

        // Fetch food items from the cart that have quantity > 0
        val currentRestaurantFoodItems =
            roomDbViewModel.fetchFoodItemsInCart.value?.filter { it.restaurantId == restaurantDetails?.id }
                ?: emptyList()

        val foodItems = currentRestaurantFoodItems.filter { it.foodQuantity > 0 }
        if (foodItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
            return
        }

        var totalPrice = foodItems.sumOf { it.foodPrice.toDouble() * it.foodQuantity }

        if (isCouponApplied) {
            totalPrice -= 62.0
        }

        val orderPlaceOrNotBottomSheetFragment =
            OrderPlaceOrNotBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString("location", selectedAddress)
                    putDouble("totalPrice", totalPrice)
                }
            }

        orderPlaceOrNotBottomSheetFragment.show(
            supportFragmentManager,
            "orderPlaceBottomSheetFragment"
        )
    }


    override fun orderPlaceClick() {
        placeOrder()

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("showOrderDialog", true)
        startActivity(intent)
        finishAffinity()
    }


    override fun orderPlacedDialogClick() {

    }

    private fun placeOrder() {

        val address =
            myHelper.extractAddressDetails(myHelper.getLatitude(), myHelper.getLongitude())

        binding.location.text = selectedAddress

        val currentRestaurantFoodItems =
            roomDbViewModel.fetchFoodItemsInCart.value?.filter { it.restaurantId == restaurantDetails?.id }
                ?: emptyList()

        val foodItems = currentRestaurantFoodItems.filter { it.foodQuantity > 0 }
        if (foodItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
            return
        }

        var totalPrice = foodItems.sumOf { it.foodPrice.toDouble() * it.foodQuantity }
        val totalSaved = foodItems.sumOf { it.foodOffer.toDouble() }

        if (isCouponApplied) {
            totalPrice -= 62.0
        }

        val userNumber = if (appSharedPreferences?.getBoolean(PrefKeys.SKIP_BTN_CLICK) == true) {
            appSharedPreferences?.getString(PrefKeys.SKIP_USER_NUMBER) ?: myHelper.numberIs()
        } else {
            myHelper.numberIs()
        }

        val userName = if (appSharedPreferences?.getBoolean(PrefKeys.SKIP_BTN_CLICK) == true) {
            appSharedPreferences?.getString(PrefKeys.SKIP_USER_NAME) ?: userSavedAddress?.receiverName
        } else {
            userSavedAddress?.receiverName
        }

        val orderDetails = OrderDetails(
            restaurantId = restaurantDetails?.id ?: 0,
            restaurantName = restaurantDetails?.restaurantName ?: "Unknown",
            totalPrice = totalPrice,
            totalSaved = totalSaved,
            deliveryAddress = (selectedAddress ?: address?.fullAddress).toString(),
            orderStatus = "Pending",
            timestamp = System.currentTimeMillis(),
            currentUserNumber = userNumber,
            receiverNumber = userNumber,
            receiverName = userName?:""
        )

        val foodItemsInOrder = foodItems.map { foodItem ->
            FoodItemInOrder(
                orderId = 0,
                foodId = foodItem.id,
                foodName = foodItem.foodName,
                foodPrice = foodItem.foodPrice,
                foodQuantity = foodItem.foodQuantity,
                foodOffer = foodItem.foodOffer,
                foodType = foodItem.foodType,
                foodSize = foodItem.foodSize,
                foodImage = foodItem.foodImage,
                restaurantId = foodItem.restaurantId
            )
        }

        // insert the order details in RoomDB
        roomDbViewModel.insertOrderDetails(orderDetails, foodItemsInOrder)

        // delete the orderd food from cart
        roomDbViewModel.deleteFoodItemsByRestaurantId(orderDetails.restaurantId)

    }

    override fun onRestart() {
        super.onRestart()
        // for skip mode enter user details
        recreate()
    }


}