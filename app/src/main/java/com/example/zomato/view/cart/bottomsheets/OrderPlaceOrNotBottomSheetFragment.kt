package com.test.zomato.view.cart.bottomsheets

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.test.zomato.databinding.FragmentOrderPlaceBottomSheetBinding
import com.test.zomato.cartDB.CartAndOrderViewModel
import com.test.zomato.view.cart.ShowCartFoodDetailsActivity
import com.test.zomato.view.orders.interfaces.OrderPlcaeClickListener

class OrderPlaceOrNotBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentOrderPlaceBottomSheetBinding
    private lateinit var roomDbViewModel: CartAndOrderViewModel

    private var listner: OrderPlcaeClickListener? = null
    private var handler: Handler? = null
    private var progressRunnable: Runnable? = null
    private var progress = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listner = context as ShowCartFoodDetailsActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderPlaceBottomSheetBinding.inflate(inflater, container, false)

        roomDbViewModel = ViewModelProvider(this)[CartAndOrderViewModel::class.java]

        // Retrieve the location and total price from the arguments
        val location = arguments?.getString("location")
        val totalPrice = arguments?.getDouble("totalPrice")

        location?.let { binding.location.text = it }

        totalPrice?.let {
            binding.deliveryTime.text = "Pay ₹${it.toInt()} cash on delivery"
        }

        binding.closeButton.setOnClickListener {
            progressRunnable?.let { it1 -> handler?.removeCallbacks(it1) }
            Toast.makeText(context, "Order Canceled!", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        startProgressBar()

        return binding.root
    }

    private fun startProgressBar() {
        val progressBar = binding.progressBar
        handler = Handler(Looper.getMainLooper())
        progress = 0

        progressRunnable = object : Runnable {
            override fun run() {
                progress += 1
                progressBar.progress = progress

                if (progress < 100) {
                    handler?.postDelayed(this, 60)
                } else {
                    // Progress is complete
                    // Toast.makeText(context, "Progress Complete!", Toast.LENGTH_SHORT).show()
                    listner?.orderPlaceClick()
                    dismiss()
                }
            }
        }

        // Start the progress
        handler?.post(progressRunnable!!)
    }

    override fun onDetach() {
        super.onDetach()
        progressRunnable?.let { handler?.removeCallbacks(it) }
    }
}
