# Catapult

[![Maven Central](https://img.shields.io/maven-central/v/io.github.ibustami/catapult.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.ibustami%22%20AND%20a:%22catapult%22)


Catapult is an Android library that provides easy-to-use classes and utilities for building modern Android applications. It includes a variety of components and helpers that make it easy to create responsive, high-performance apps with a modern UI.

## Installation
To use Catapult in your Android project, add the following dependency to your build.gradle file:

```gradle
dependencies {
    implementation 'io.github.ibustami:catapult:{latest_version}'
}
```

## Usage

Catapult includes a variety of classes and utilities that can be used in your Android project. Here are some examples of how to use them:

### RecyclerView Adapter

The `SimpleAdapter` class in Catapult makes it easy to create a RecyclerView adapter with multiple view types:

```kotlin
val adapter = SimpleAdapter {
    when (it) {
        R.layout.item_city -> CityAndFoodViewHolder::class as KClass<SimpleAdapter.ViewHolder<Any>>
        R.layout.item_restaurant -> RestaurantViewHolder::class as KClass<SimpleAdapter.ViewHolder<Any>>
        else -> throw IllegalArgumentException("Unknown binding: $it")
    }
}

fun loadCities(cities: List<Pair<String, String>>) {
    adapter.addItems(R.layout.item_city, cities)
}

fun loadRestaurants(restaurants: List<String>) {
    adapter.addItems(R.layout.item_restaurant, restaurants)
}
```
`SimpleAdapter` extend from `ListAdapter` which utilize `DiffUtil` so it dont use `notifyDataSetChanged` to update list of RecyclerView

### EventFlowable

The EventFlowable class is a lifecycle-aware event bus that makes it easy to communicate 
