package com.test.zomato.utils

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.test.zomato.databinding.FragmentEnableAppLocationPermissionDialogBinding

class EnableAppLocationPermissionDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentEnableAppLocationPermissionDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEnableAppLocationPermissionDialogBinding.inflate(layoutInflater, container, false)

        /*// Determine the caller based on the tag
        updateTextColor(tag)*/

        binding.goToSetting.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireContext().packageName, null)
            intent.data = uri
            startActivity(intent)
            dismiss()
        }

        return binding.root
    }

   /* private fun updateTextColor(callerTag: String?) {
       if (callerTag == "requestLocationPermissionDialog") {
            binding.text1.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.redTextColor
                )
            )
            binding.text2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.redTextColor
                )
            )
        } else {
            binding.text1.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.light_green
                )
            )
            binding.text2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.light_green
                )
            )
        }

    }*/

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

    }
}
