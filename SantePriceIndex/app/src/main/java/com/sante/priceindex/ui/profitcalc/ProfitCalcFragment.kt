package com.sante.priceindex.ui.profitcalc

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sante.priceindex.data.model.VendorCalculation
import com.sante.priceindex.databinding.FragmentProfitCalcBinding
import com.sante.priceindex.ui.SharedViewModel
import com.sante.priceindex.util.PricingEngine

class ProfitCalcFragment : Fragment() {

    private var _binding: FragmentProfitCalcBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfitCalcBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSelectedVegetable()
        setupInputListeners()
        setupSliders()
        calculateAndDisplay()
    }

    private fun observeSelectedVegetable() {
        viewModel.selectedVegetable.observe(viewLifecycleOwner) { veg ->
            if (veg != null) {
                binding.tvSelectedVeg.text = "${veg.emoji} ${veg.vegetableName} (${veg.vegetableNameHindi})"
                binding.etMandiPrice.setText(veg.mandiPrice.toString())
                binding.tvMandiPriceLabel.text = "Mandi Price (₹/${veg.unit})"
                calculateAndDisplay()
            }
        }
    }

    private fun setupInputListeners() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { calculateAndDisplay() }
        }

        binding.etMandiPrice.addTextChangedListener(watcher)
        binding.etTransportCost.addTextChangedListener(watcher)
        binding.etQuantity.addTextChangedListener(watcher)
    }

    private fun setupSliders() {
        binding.sliderWastage.addOnChangeListener { _, _, _ -> calculateAndDisplay() }
        binding.sliderProfit.addOnChangeListener { _, _, _ -> calculateAndDisplay() }

        binding.sliderWastage.addOnChangeListener { slider, value, _ ->
            binding.tvWastageValue.text = "${value.toInt()}%"
        }
        binding.sliderProfit.addOnChangeListener { slider, value, _ ->
            binding.tvProfitValue.text = "${value.toInt()}%"
        }
    }

    private fun calculateAndDisplay() {
        val mandiPrice = binding.etMandiPrice.text.toString().toDoubleOrNull() ?: return
        val transport = binding.etTransportCost.text.toString().toDoubleOrNull() ?: 2.0
        val quantity = binding.etQuantity.text.toString().toDoubleOrNull() ?: 10.0
        val wastage = binding.sliderWastage.value.toDouble()
        val profit = binding.sliderProfit.value.toDouble()

        val calc = PricingEngine.calculate(
            mandiPrice = mandiPrice,
            transportCostPerKg = transport,
            wastagePercent = wastage,
            profitMarginPercent = profit,
            quantityKg = quantity
        )

        displayResults(calc)
    }

    private fun displayResults(calc: VendorCalculation) {
        binding.tvCostPerKg.text = PricingEngine.formatPrice(calc.totalCostPerKg)
        binding.tvRrp.text = PricingEngine.formatPrice(calc.recommendedRetailPrice)

        val marketPrice = PricingEngine.suggestMarketPrice(calc.recommendedRetailPrice)
        binding.tvMarketPrice.text = PricingEngine.formatPrice(marketPrice)

        binding.tvTotalInvestment.text = PricingEngine.formatPrice(calc.totalInvestment)
        binding.tvExpectedRevenue.text = PricingEngine.formatPrice(calc.expectedRevenue)
        binding.tvNetProfit.text = PricingEngine.formatPrice(calc.expectedNetProfit)
        binding.tvProfitPercent.text = "%.1f%%".format(calc.profitPercentage)

        // Color the profit green/red
        val color = if (calc.expectedNetProfit >= 0) {
            android.graphics.Color.parseColor("#2E7D32")
        } else {
            android.graphics.Color.parseColor("#C62828")
        }
        binding.tvNetProfit.setTextColor(color)
        binding.tvProfitPercent.setTextColor(color)

        // Advice message
        binding.tvAdvice.text = when {
            calc.profitPercentage >= 25 -> "✅ Excellent margin! Go ahead and buy this stock."
            calc.profitPercentage >= 15 -> "👍 Good margin. Proceed with purchase."
            calc.profitPercentage >= 5  -> "⚠️ Low margin. Try to reduce transport cost."
            else                         -> "❌ Loss making. Do NOT buy at this Mandi price."
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
