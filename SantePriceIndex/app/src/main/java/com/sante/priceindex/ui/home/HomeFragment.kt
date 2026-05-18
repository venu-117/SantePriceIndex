package com.sante.priceindex.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sante.priceindex.databinding.FragmentHomeBinding
import com.sante.priceindex.ui.SharedViewModel
import com.sante.priceindex.ui.priceboard.PriceBoardActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var adapter: MandiPriceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = MandiPriceAdapter { selectedItem ->
            viewModel.selectVegetable(selectedItem)
            Toast.makeText(
                requireContext(),
                "${selectedItem.emoji} ${selectedItem.vegetableName} selected for calculator",
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.recyclerMandi.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.prices.observe(viewLifecycleOwner) { prices ->
            adapter.submitList(prices)
            binding.tvItemCount.text = "${prices.size} items today"
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.recyclerMandi.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.lastRefreshed.observe(viewLifecycleOwner) { time ->
            binding.tvLastRefreshed.text = "Updated: $time"
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnRefresh.setOnClickListener {
            viewModel.loadPrices()
        }

        binding.btnPriceBoard.setOnClickListener {
            val intent = Intent(requireContext(), PriceBoardActivity::class.java)
            // Pass current prices to PriceBoard
            val prices = viewModel.prices.value ?: return@setOnClickListener
            val names = prices.map { "${it.emoji} ${it.vegetableName}" }.toTypedArray()
            val rrps = prices.map { "₹${(it.mandiPrice * 1.35).toInt()}" }.toTypedArray()
            intent.putExtra("NAMES", names)
            intent.putExtra("PRICES", rrps)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
