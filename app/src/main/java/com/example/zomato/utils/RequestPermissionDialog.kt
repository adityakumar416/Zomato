package com.test.zomato.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.test.zomato.databinding.FragmentRequestPermissionDialogBinding

class RequestPermissionDialog : DialogFragment() {
    private lateinit var binding: FragmentRequestPermissionDialogBinding
  //  private val myHelper by lazy { MyHelper(requireContext()) }
    private lateinit var myHelper: MyHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRequestPermissionDialogBinding.inflate(layoutInflater, container, false)

        myHelper = MyHelper(requireActivity())

        binding.iAmSure.setOnClickListener {
            dismiss()
        }

        binding.retry.setOnClickListener {
            activity?.let { myHelper.requestLocationPermission(it) }
            dismiss()
            /*val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireContext().packageName, null)
            intent.data = uri
            startActivity(intent)
            dismiss()*/
        }


        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        isCancelable = false
    }

}
