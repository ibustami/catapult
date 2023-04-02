package io.catapult.ui.component.viewholder

import android.view.View
import io.catapult.adapter.SimpleAdapter
import io.catapult.databinding.ItemRestaurantBinding

class RestaurantViewHolder(itemView: View): SimpleAdapter.ViewHolder<String>(itemView) {
    override fun bind(itemData: String) {
        val binding = ItemRestaurantBinding.bind(itemView)
        binding.tvRestaurantName.text = itemData
    }
}