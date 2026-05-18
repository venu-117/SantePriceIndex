package com.sante.priceindex.ui.priceboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sante.priceindex.R
import com.sante.priceindex.databinding.ItemPriceBoardBinding

class PriceBoardAdapter(
    private var items: List<PriceBoardItem>
) : RecyclerView.Adapter<PriceBoardAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemPriceBoardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PriceBoardItem) {
            val context = binding.root.context
            binding.tvBoardEmoji.text = item.emoji
            binding.tvBoardName.text = item.name
            binding.tvBoardHindi.text = item.nameHindi
            binding.tvBoardPrice.text = item.price
            binding.tvBoardUnit.text = context.getString(R.string.unit_format, item.unit)
            binding.tvLiveMandi.text = context.getString(R.string.live_mandi_price_label, item.mandiPrice.toString())
        }
    }

    fun updateData(newItems: List<PriceBoardItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPriceBoardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}
