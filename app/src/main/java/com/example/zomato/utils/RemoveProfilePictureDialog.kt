package com.test.zomato.utils

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.test.zomato.databinding.RemoveProfilePictureDialogBinding
import com.example.zomato.view.profile.UserProfileDetailsActivity
import com.test.zomato.view.profile.interfaces.OnProfileClickListener

class RemoveProfilePictureDialog : DialogFragment() {
    private lateinit var binding: RemoveProfilePictureDialogBinding
  //  private val myHelper by lazy { MyHelper(requireContext()) }
    private lateinit var myHelper: MyHelper

    private var listener : OnProfileClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as UserProfileDetailsActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RemoveProfilePictureDialogBinding.inflate(layoutInflater, container, false)

        myHelper = MyHelper(requireActivity())

        binding.cancel.setOnClickListener {
            dismiss()
        }

        binding.remove.setOnClickListener {
            listener?.deleteProfilePhoto()
            dismiss()
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
