package io.catapult.component

import androidx.lifecycle.LifecycleOwner

object ComponentProviders {

    val componentProviderMap: MutableMap<LifecycleOwner, ComponentProvider> = HashMap()

    fun of(lifecycleOwner: LifecycleOwner): ComponentProvider {
        return componentProviderMap.getOrPut(lifecycleOwner) {
            ComponentProvider(lifecycleOwner)
        }
    }
}