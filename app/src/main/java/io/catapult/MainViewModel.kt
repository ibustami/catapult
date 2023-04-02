package io.catapult

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.catapult.event.EventFlowable
import io.catapult.ui.component.CatapultComponent
import io.catapult.ui.event.CityLoaded
import io.catapult.ui.event.RestaurantLoaded
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    fun fetchData(eventFlowable: EventFlowable) {
        val listCity = listOf("Malang", "Surabaya", "Jakarta", "Bali", "Bandung")
        val listFood = listOf("Nasi Goreng", "Mie Goreng", "Soto", "Bakso", "Sate")
        val listRestaurant = listOf("McDonald", "KFC", "Burger King", "Pizza Hut", "Dominos")
        val listCityAndFood = listCity.zip(listFood)
        viewModelScope.launch {
            eventFlowable.emit(CatapultComponent::class, CityLoaded(listCityAndFood))
            delay(100)
            eventFlowable.emit(CatapultComponent::class, RestaurantLoaded(listRestaurant))
        }
    }

}