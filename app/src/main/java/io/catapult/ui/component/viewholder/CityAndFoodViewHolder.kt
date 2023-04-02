package io.catapult.ui.component.viewholder

import android.view.View
import io.catapult.adapter.SimpleAdapter
import io.catapult.databinding.ItemCityBinding

class CityAndFoodViewHolder(itemView: View) :
    SimpleAdapter.ViewHolder<Pair<String, String>>(itemView) {

    override fun bind(itemData: Pair<String, String>) {
        val binding = ItemCityBinding.bind(itemView)
        binding.tvCityName.text = itemData.first
        binding.tvFoodName.text = itemData.second
    }
}