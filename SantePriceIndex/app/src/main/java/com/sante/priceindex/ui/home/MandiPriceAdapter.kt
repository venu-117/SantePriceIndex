package com.sante.priceindex.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sante.priceindex.R
import com.sante.priceindex.data.model.MandiPrice
import com.sante.priceindex.data.model.PriceTrend
import com.sante.priceindex.databinding.ItemMandiPriceBinding

class MandiPriceAdapter(
    private val onItemClick: (MandiPrice) -> Unit
) : ListAdapter<MandiPrice, MandiPriceAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MandiPrice>() {
            override fun areItemsTheSame(old: MandiPrice, new: MandiPrice) = old.id == new.id
            override fun areContentsTheSame(old: MandiPrice, new: MandiPrice) = old == new
        }
    }

    inner class ViewHolder(private val binding: ItemMandiPriceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MandiPrice) {
            binding.tvEmoji.text = item.emoji
            binding.tvVegName.text = item.vegetableName
            binding.tvVegHindi.text = item.vegetableNameHindi
            binding.tvMandiPrice.text = "₹${item.mandiPrice}/${item.unit}"
            binding.tvLastUpdated.text = item.lastUpdated

            // Suggested RRP (35% markup default)
            val rrp = item.mandiPrice * 1.35
            binding.tvRrp.text = "RRP ~₹${rrp.toInt()}/${item.unit}"

            // Trend indicator
            when (item.trend) {
                PriceTrend.RISING -> {
                    binding.tvTrend.text = "↑ Rising"
                    binding.tvTrend.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.trend_rising)
                    )
                    binding.cardRoot.setCardBackgroundColor(
                        ContextCompat.getColor(binding.root.context, R.color.card_rising)
                    )
                }
                PriceTrend.FALLING -> {
                    binding.tvTrend.text = "↓ Falling"
                    binding.tvTrend.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.trend_falling)
                    )
                    binding.cardRoot.setCardBackgroundColor(
                        ContextCompat.getColor(binding.root.context, R.color.card_falling)
                    )
                }
                PriceTrend.STABLE -> {
                    binding.tvTrend.text = "→ Stable"
                    binding.tvTrend.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.trend_stable)
                    )
                    binding.cardRoot.setCardBackgroundColor(
                        ContextCompat.getColor(binding.root.context, R.color.card_stable)
                    )
                }
            }

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMandiPriceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
