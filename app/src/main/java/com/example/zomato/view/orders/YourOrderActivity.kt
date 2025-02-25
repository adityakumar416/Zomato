package com.test.zomato.view.orders

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.test.zomato.databinding.ActivityYourOrderBinding
import com.test.zomato.view.orders.adapters.OrderAdapter
import com.test.zomato.cartDB.CartAndOrderViewModel
import com.test.zomato.utils.AppSharedPreferences
import com.test.zomato.utils.MyHelper
import com.test.zomato.utils.PrefKeys
import com.test.zomato.view.orders.orderModels.OrderWithFoodItems
import java.util.Locale

class YourOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityYourOrderBinding
    private lateinit var roomDbViewModel: CartAndOrderViewModel
    private lateinit var orderAdapter: OrderAdapter
    private val myHelper by lazy { MyHelper(this) }
    private val appSharedPreferences by lazy { AppSharedPreferences.getInstance(this) }

    // Store the unfiltered orders list
    private var ordersList = listOf<OrderWithFoodItems>()
    private val REQUEST_CODE_SPEECH_INPUT = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYourOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.parseColor("#F3F4FA")

        roomDbViewModel = ViewModelProvider(this)[CartAndOrderViewModel::class.java]

        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                val query = p0.toString().trim().lowercase()
                filterOrders(query)
            }
        })

        binding.micIcon.setOnClickListener {
            // Create an intent for speech recognition
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
            }

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Speech recognition is not supported on this device.", Toast.LENGTH_SHORT).show()
            }
        }


        val userNumber = if (appSharedPreferences?.getBoolean(PrefKeys.SKIP_BTN_CLICK) == true) {
            appSharedPreferences?.getString(PrefKeys.SKIP_USER_NUMBER) ?: myHelper.numberIs()
        } else {
            myHelper.numberIs()
        }

        // Fetch orders based on user number
        roomDbViewModel.fetchOrdersFromDb(userNumber)

        orderAdapter = OrderAdapter()

        // Observe the orders in the database
        roomDbViewModel.fetchOrdersInDb.observe(this) { ordersWithFoodItems ->
            // Store the original orders list
            ordersList = ordersWithFoodItems

            // Update the list in the adapter
            orderAdapter.updateList(ordersWithFoodItems)
            orderAdapter.notifyDataSetChanged()

            // Check if the orders list is empty
            if (ordersWithFoodItems.isEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.emptyOrderList.visibility = View.VISIBLE
                binding.searchLayout.visibility = View.GONE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.emptyOrderList.visibility = View.GONE
                binding.searchLayout.visibility = View.VISIBLE
            }
        }

        // Set up RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = orderAdapter

        //  back button click
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    // Function to filter the orders list based on the restaurant name
    private fun filterOrders(query: String) {
        val filteredList = if (query.isEmpty()) {
            ordersList
        } else {
            ordersList.filter { order ->
                order.order.restaurantName.contains(query,ignoreCase = true)
            }
        }

        // Update the adapter with the filtered list
        orderAdapter.updateList(filteredList)
        orderAdapter.notifyDataSetChanged()

        // Hide "No orders" message if filtered list is not empty
        if (filteredList.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.emptyOrderList.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyOrderList.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                // Extract the result from the data
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!result.isNullOrEmpty()) {
                    // Set the recognized speech text to the search EditText
                    binding.search.setText(result[0])
                } else {
                    Toast.makeText(this, "No speech input recognized", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
