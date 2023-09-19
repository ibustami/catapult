package io.catapult.ui.component

import android.view.View
import androidx.lifecycle.LifecycleOwner
import io.catapult.annotations.Subscribe
import io.catapult.component.Component
import io.catapult.event.EventFlowable
import io.catapult.ui.event.CityLoaded
import io.catapult.ui.event.RestaurantLoaded

class CatapultComponent(view: View, lifecycleOwner: LifecycleOwner)
    : Component<CatapultUiView>(view, lifecycleOwner) {

    override fun onCreateView(view: View, eventFlowable: EventFlowable) =
        CatapultUiView(view, eventFlowable)

    @Subscribe(CatapultComponent::class)
    fun subscribeCityLoaded(cityLoaded: CityLoaded) {
        cityLoaded.data?.let {
            uiView.show()
            uiView.loadCities(it)
        } ?: run { uiView.hide() }
    }

    override fun onCreate() {
        super.onCreate()
        uiView.onCreate()
    }

    @Subscribe(CatapultComponent::class)
    fun subscribeRestaurantLoaded(restaurantLoaded: RestaurantLoaded) {
        restaurantLoaded.data?.let {
            uiView.show()
            uiView.loadRestaurants(it)
        } ?: run { uiView.hide() }
    }
}