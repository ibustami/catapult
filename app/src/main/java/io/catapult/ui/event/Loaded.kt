package io.catapult.ui.event

import io.catapult.event.Event

open class Loaded<T>(val data: T?): Event

class CityLoaded(cities: List<Pair<String, String>>?): Loaded<List<Pair<String, String>>>(cities)

class RestaurantLoaded(restaurants: List<String>?): Loaded<List<String>>(restaurants)
