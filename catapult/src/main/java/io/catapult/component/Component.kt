package io.catapult.component

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.catapult.event.EventFlowable

abstract class Component<T : UiView>(val view: View, private val lifecycleOwner: LifecycleOwner)
    : LifecycleEventObserver {

    private val eventFlowable: EventFlowable = EventFlowable.getInstance(lifecycleOwner)

    lateinit var uiView: T

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    abstract fun onCreateView(view: View, eventFlowable: EventFlowable): T

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                uiView = onCreateView(view, eventFlowable)
                EventFlowable.bindSubscriber(this, eventFlowable)
                onCreate()
            }
            Lifecycle.Event.ON_START -> onStart()
            Lifecycle.Event.ON_RESUME -> onResume()
            Lifecycle.Event.ON_DESTROY -> onDestroy()
            Lifecycle.Event.ON_PAUSE -> onPause()
            Lifecycle.Event.ON_STOP -> onStop()
            else -> Unit
        }
    }

    open fun onCreate() = Unit
    open fun onStart() = Unit
    open fun onResume() = Unit
    open fun onPause() = Unit
    open fun onStop() = Unit
    open fun onDestroy() = Unit
}