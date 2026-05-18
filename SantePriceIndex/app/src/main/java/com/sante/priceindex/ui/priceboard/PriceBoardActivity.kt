package com.sante.priceindex.ui.priceboard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.sante.priceindex.data.repository.MandiRepository
import com.sante.priceindex.databinding.ActivityPriceBoardBinding
import com.sante.priceindex.util.PricingEngine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PriceBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPriceBoardBinding
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var clockRunnable: Runnable
    private lateinit var refreshRunnable: Runnable
    private lateinit var adapter: PriceBoardAdapter
    private val repository = MandiRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPriceBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        setupRecyclerView()
        startLiveUpdates()
        startClock()
        
        binding.btnClose.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = PriceBoardAdapter(emptyList())
        binding.recyclerPriceBoard.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerPriceBoard.adapter = adapter
    }

    private fun startLiveUpdates() {
        refreshRunnable = object : Runnable {
            override fun run() {
                fetchLatestPrices()
                handler.postDelayed(this, 60000) // 1 minute interval
            }
        }
        handler.post(refreshRunnable)
    }

    private fun fetchLatestPrices() {
        lifecycleScope.launch {
            try {
                val prices = repository.fetchTodayPrices()
                val boardItems = prices.map { price ->
                    val fairPrice = price.mandiPrice * 1.35
                    PriceBoardItem(
                        emoji = price.emoji,
                        name = price.vegetableName,
                        nameHindi = price.vegetableNameHindi,
                        price = "₹${fairPrice.toInt()}",
                        unit = price.unit,
                        mandiPrice = price.mandiPrice
                    )
                }
                adapter.updateData(boardItems)
            } catch (e: Exception) {
                // Silently retry next minute
            }
        }
    }

    private fun startClock() {
        clockRunnable = object : Runnable {
            override fun run() {
                val sdf = SimpleDateFormat("hh:mm a | dd MMM yyyy", Locale.getDefault())
                binding.tvDateTime.text = "LIVE SYNC: ${sdf.format(Date())}"
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(clockRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(clockRunnable)
        handler.removeCallbacks(refreshRunnable)
    }
}

data class PriceBoardItem(
    val emoji: String,
    val name: String,
    val nameHindi: String,
    val price: String,
    val unit: String,
    val mandiPrice: Double
)
