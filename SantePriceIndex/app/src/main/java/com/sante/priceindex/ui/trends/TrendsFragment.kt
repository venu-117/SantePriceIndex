package com.sante.priceindex.ui.trends

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.sante.priceindex.data.model.MandiPrice
import com.sante.priceindex.data.model.PriceTrend
import com.sante.priceindex.databinding.FragmentTrendsBinding
import com.sante.priceindex.ui.SharedViewModel
import com.sante.priceindex.util.PricingEngine

class TrendsFragment : Fragment() {

    private var _binding: FragmentTrendsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by activityViewModels()
    private var allPrices: List<MandiPrice> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChart()
        observeViewModel()
    }

    private fun setupChart() {
        binding.lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            setScaleEnabled(false)
            setPinchZoom(false)
            setDrawGridBackground(false)
            setBackgroundColor(Color.parseColor("#1A1A2E"))
            legend.textColor = Color.WHITE
        }

        binding.lineChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            textColor = Color.WHITE
            gridColor = Color.parseColor("#444444")
            axisLineColor = Color.parseColor("#666666")
        }

        binding.lineChart.axisLeft.apply {
            textColor = Color.WHITE
            gridColor = Color.parseColor("#333333")
            axisLineColor = Color.parseColor("#666666")
        }

        binding.lineChart.axisRight.isEnabled = false
    }

    private fun observeViewModel() {
        viewModel.prices.observe(viewLifecycleOwner) { prices ->
            allPrices = prices
            setupSpinner(prices)
        }

        viewModel.trendData.observe(viewLifecycleOwner) { trendPoints ->
            if (trendPoints.isEmpty()) return@observe

            val selectedVeg = allPrices.find { it.id == getSelectedVegId() }
            val entries = trendPoints.mapIndexed { index, point ->
                Entry(index.toFloat(), point.price.toFloat())
            }
            val labels = trendPoints.map { it.day }

            val lineColor = when (selectedVeg?.trend) {
                PriceTrend.RISING -> Color.parseColor("#FF6B6B")
                PriceTrend.FALLING -> Color.parseColor("#6BCB77")
                else -> Color.parseColor("#FFD93D")
            }

            val dataSet = LineDataSet(entries, "${selectedVeg?.vegetableName ?: "Price"} (₹/kg)").apply {
                color = lineColor
                valueTextColor = Color.WHITE
                lineWidth = 3f
                circleRadius = 5f
                setCircleColor(lineColor)
                circleHoleColor = Color.BLACK
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawFilled(true)
                fillColor = lineColor
                fillAlpha = 50
                valueTextSize = 10f
            }

            binding.lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            binding.lineChart.data = LineData(dataSet)
            binding.lineChart.invalidate()
            binding.lineChart.animateX(600)

            // Show analysis text
            if (selectedVeg != null) {
                val msg = PricingEngine.getTrendMessage(
                    selectedVeg.vegetableName, selectedVeg.trend.name
                )
                binding.tvTrendAnalysis.text = msg

                // Financial literacy section
                val lastPrice = trendPoints.last().price
                val firstPrice = trendPoints.first().price
                val change = lastPrice - firstPrice
                val pct = if (firstPrice > 0) (change / firstPrice) * 100 else 0.0
                binding.tvWeekChange.text = if (change >= 0) {
                    "📈 +${PricingEngine.formatPrice(change)} (+%.1f%%) this week".format(pct)
                } else {
                    "📉 ${PricingEngine.formatPrice(change)} (%.1f%%) this week".format(pct)
                }
            }
        }
    }

    private var selectedVegId = ""

    private fun getSelectedVegId() = selectedVegId

    private fun setupSpinner(prices: List<MandiPrice>) {
        val names = prices.map { "${it.emoji} ${it.vegetableName}" }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerVegetable.adapter = adapter

        binding.spinnerVegetable.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = prices[position]
                selectedVegId = selected.id
                viewModel.loadTrendData(selected.id)

                binding.tvCurrentPrice.text = "Today's Mandi Price: ₹${selected.mandiPrice}/${selected.unit}"
                binding.tvRrpSuggestion.text = "Suggested RRP: ₹${(selected.mandiPrice * 1.35).toInt()}/${selected.unit}"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Load first item's trend by default
        if (prices.isNotEmpty()) {
            selectedVegId = prices[0].id
            viewModel.loadTrendData(prices[0].id)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
