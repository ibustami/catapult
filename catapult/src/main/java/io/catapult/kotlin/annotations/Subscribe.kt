package io.catapult.kotlin.annotations

import kotlin.reflect.KClass

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class Subscribe(
    val value: KClass<*>,
    val single: Boolean = false,
    val keepAlive: Boolean = false
)
