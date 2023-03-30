package io.catapult.event

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.catapult.annotations.Subscribe
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.jvmErasure

class EventFlowable(val lifecycleOwner: LifecycleOwner): LifecycleEventObserver {

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    companion object {
        val sharedFlows = mutableMapOf<KClass<*>, MutableSharedFlow<out Event>>()
        private val eventFlowableMap = mutableMapOf<LifecycleOwner, EventFlowable>()

        fun getInstance(lifecycleOwner: LifecycleOwner): EventFlowable {
            return eventFlowableMap[lifecycleOwner]?: EventFlowable(lifecycleOwner)
        }

        inline fun <reified T: Any> bindSubscriber(target: T, eventFlowable: EventFlowable) {
            val methodMaps = mutableMapOf<KClass<*>, MutableList<KFunction<*>>>()
            for (method in target::class.functions) {
                if (method.findAnnotations(Subscribe::class).isEmpty() ||
                    method.parameters.isEmpty() ||
                    method.typeParameters.size > 2
                ) {
                    continue
                }

                val subscribe: Subscribe? = method.findAnnotation()

                subscribe?.let {
                    putMethodIntoMaps(methodMaps, it, method)
                }
            }

            for (entrySet in methodMaps.entries) {
                eventFlowable.subscribe<Event>(eventFlowable.lifecycleOwner, entrySet.key) {
                    invokeMethod(entrySet, it, target)
                }
            }
        }

        fun putMethodIntoMaps(
            methodMaps: MutableMap<KClass<*>, MutableList<KFunction<*>>>,
            subscribe: Subscribe,
            method: KFunction<*>
        ) {
            methodMaps.getOrPut(subscribe.value) { mutableListOf() }.add(method)
        }

        inline fun <reified T : Any> invokeMethod(
            entry: Map.Entry<KClass<*>, List<KFunction<*>>>,
            event: Event,
            target: T
        ): List<KFunction<*>> {
            val methodInvokes = mutableListOf<KFunction<*>>()
            for (method in entry.value) {
                val eventClass = method
                    .parameters.last().type.jvmErasure.java as Class<out Event>
                if (eventClass != event.javaClass) {
                    continue
                }
                try {
                    val result = method.call(target, event)
                    if (result !is Boolean || result) {
                        methodInvokes.add(method)
                    }
                } catch (e: Exception) {
                    // do nothing
                }
            }
            return methodInvokes
        }
    }

    suspend inline fun <reified T: Event> emit(kClass: KClass<*>, event: T) {
        withContext(Dispatchers.IO) {
            val sharedFlow = getShareFlow<T>(kClass) as MutableSharedFlow<T>
            sharedFlow.emit(event)
        }
    }

    inline fun <reified T: Event> getShareFlow(kClass: KClass<*>): MutableSharedFlow<out Event> {
        return sharedFlows[kClass]?: createShareFlow<T>(kClass)
    }

    inline fun <reified T: Event> createShareFlow(kClass: KClass<*>): MutableSharedFlow<out Event> {
        val shareFlowEvent = MutableSharedFlow<T>()
        sharedFlows[kClass] = shareFlowEvent
        return shareFlowEvent
    }

    inline fun <reified T: Event> subscribe(lifecycleOwner: LifecycleOwner, kClass: KClass<*>, crossinline onEvent: (T) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            getShareFlow<T>(kClass).filterIsInstance<T>().flowOn(Dispatchers.IO).collectLatest { event ->
                kotlin.coroutines.coroutineContext.ensureActive()
                onEvent(event)
            }
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            eventFlowableMap.remove(source)
        }
    }
}