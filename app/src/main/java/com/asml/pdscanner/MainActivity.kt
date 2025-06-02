package com.asml.pdscanner

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asml.pdscanner.databinding.ActivityMainBinding
import com.asml.pdscanner.network.soap.SoapViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: SoapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        binding.btnCalculate.setOnClickListener {
            val a = binding.etNumber1.text.toString().toIntOrNull() ?: 0
            val b = binding.etNumber2.text.toString().toIntOrNull() ?: 0
            viewModel.calculateSum(a, b)
        }
    }


    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                        binding.btnCalculate.isEnabled = !isLoading
                    }
                }
                launch {
                    viewModel.result.collect { result ->
                        binding.tvResult.text = result
                    }
                }
            }
        }
    }
}