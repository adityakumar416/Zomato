package com.test.zomato.view.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.test.zomato.databinding.ActivityJoinGoldBinding
import com.test.zomato.utils.MyHelper

class JoinGoldActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinGoldBinding
    private val myHelper by lazy { MyHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityJoinGoldBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myHelper.setStatusBarIconColor(this,true)

        binding.backButton.setOnClickListener {
            finish()
        }

    }
}
