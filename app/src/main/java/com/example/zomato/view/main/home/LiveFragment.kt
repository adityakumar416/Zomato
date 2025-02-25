package com.test.zomato.view.main.home

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.test.zomato.R
import com.test.zomato.databinding.FragmentLiveBinding
import com.test.zomato.utils.AppSharedPreferences
import com.test.zomato.utils.MyHelper
import com.test.zomato.utils.PrefKeys
import com.test.zomato.view.login.repository.UserViewModel
import com.test.zomato.view.profile.ProfileActivity
import java.util.Locale

class LiveFragment : Fragment() {

    private lateinit var binding: FragmentLiveBinding
    private val myHelper by lazy { MyHelper(requireActivity()) }
    private lateinit var userViewModel: UserViewModel
    private val appSharedPreferences by lazy { activity?.let { AppSharedPreferences.getInstance(it) } }

    private val REQUEST_CODE_SPEECH_INPUT = 10

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLiveBinding.inflate(inflater, container, false)

        myHelper.setStatusBarIconColor(requireActivity(),true)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        fetchUserData(myHelper.numberIs())

        val isSkipBtnClick = appSharedPreferences?.getBoolean(PrefKeys.SKIP_BTN_CLICK)

        if (isSkipBtnClick == true) {
            binding.profile.visibility = View.GONE
            binding.menuIcon.visibility = View.VISIBLE

            binding.menuIcon.setOnClickListener {
                val intent = Intent(context, ProfileActivity::class.java)
                activity?.startActivity(intent)
            }

        }else{
            binding.profile.visibility = View.VISIBLE
            binding.menuIcon.visibility = View.GONE
        }


        val videoUri = Uri.parse("android.resource://" + requireContext().packageName + "/" + R.raw.zomato_event)
        binding.videoView.setVideoURI(videoUri)

        binding.videoView.start()

        binding.videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.setVolume(0f, 0f)
        }

        binding.videoView.setOnCompletionListener {
            binding.videoView.start()
        }

        binding.profile.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            activity?.startActivity(intent)
        }


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
                Toast.makeText(activity, "Speech recognition is not supported on this device.", Toast.LENGTH_SHORT).show()
            }
        }


        return binding.root
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
                    Toast.makeText(activity, "No speech input recognized", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // fetch user data for showing the user change image
        fetchUserData(myHelper.numberIs())
    }


    private fun fetchUserData(userPhoneNumber: String) {
        if (userPhoneNumber.isNotEmpty()) {
            userViewModel.getUserByPhoneNumber(userPhoneNumber)

            activity?.let {
                userViewModel.userLiveData.observe(it) { user ->
                    user?.let {
                        Log.d("userDataProfile", "fetchUserData: $user")

                        if (!user.imageUrl.isNullOrEmpty()) {
                            Glide.with(this).load(user.imageUrl).into(binding.userImage)
                            binding.userFirstCharacter.visibility = View.GONE
                            binding.userImage.visibility = View.VISIBLE
                        } else {
                            binding.userFirstCharacter.visibility = View.VISIBLE
                            binding.userImage.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

}
