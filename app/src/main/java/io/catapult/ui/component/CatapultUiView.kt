package io.catapult.ui.component

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import io.catapult.R
import io.catapult.adapter.SimpleAdapter
import io.catapult.component.UiView
import io.catapult.databinding.ComponentMainBinding
import io.catapult.event.EventFlowable
import io.catapult.ui.component.viewholder.CityAndFoodViewHolder
import io.catapult.ui.component.viewholder.RestaurantViewHolder
import kotlin.reflect.KClass

class CatapultUiView(view: View, eventFlowable: EventFlowable): UiView(view,eventFlowable) {

    private val binding: ComponentMainBinding

    init {
        binding = ComponentMainBinding.bind(view)
    }

    private val adapter by lazy {
        SimpleAdapter{
                when (it) {
                    R.layout.item_city -> CityAndFoodViewHolder::class as KClass<SimpleAdapter.ViewHolder<Any>>
                    R.layout.item_restaurant -> RestaurantViewHolder::class as KClass<SimpleAdapter.ViewHolder<Any>>
                    else -> throw IllegalArgumentException("Unknown binding: $it")
                }
            }
    }

    fun onCreate() {
        setupRecyclerView()
    }

    fun loadCities(cities: List<Pair<String, String>>) {
        adapter.addItems(R.layout.item_city, cities)
    }

    fun loadRestaurants(restaurants: List<String>) {
        adapter.addItems(R.layout.item_restaurant, restaurants)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter
    }

}