package com.test.zomato.view.profile.bottomSheets

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.test.zomato.databinding.FragmentAddAndUpdateProfilePictureBottomSheetBinding
import com.test.zomato.utils.RemoveProfilePictureDialog
import com.test.zomato.view.login.repository.UserViewModel
import com.example.zomato.view.profile.UserProfileDetailsActivity
import com.test.zomato.view.profile.interfaces.OnProfileClickListener


class AddAndUpdateProfilePictureBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddAndUpdateProfilePictureBottomSheetBinding
    private lateinit var userViewModel: UserViewModel
    private var profileImage: String? = null
    private var number: String? = null

    private var listener : OnProfileClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as UserProfileDetailsActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddAndUpdateProfilePictureBottomSheetBinding.inflate(layoutInflater, container, false)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

         number = arguments?.getString("number") ?: "Number not found"

        // Close the bottom sheet when cancel is clicked
        binding.cancelButton.setOnClickListener { dismiss() }

        //select image picker
        binding.changePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)        // Final image size will be less than 1 MB (optional)
                .maxResultSize(1080, 1080)  // Final image resolution will be less than 1080 x 1080 (optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        // delete photo button click
        binding.deletePhoto.setOnClickListener {

            val dialog = RemoveProfilePictureDialog()
            dialog.show(requireActivity().supportFragmentManager, "RemoveProfilePictureDialog")

            dismiss()
        }


        number?.let { userViewModel.getUserByPhoneNumber(it) }


        return binding.root
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                profileImage = data?.data.toString()

                // Immediately update the user's profile image in the database
                userViewModel.userLiveData.value?.let { currentUser ->
                    profileImage?.let {
                        userViewModel.updateUserProfile(it, currentUser.id)
                        Toast.makeText(requireActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        listener?.uploadProfilePhoto()
                        dismiss()
                    }
                }

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireActivity(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
        }
}
