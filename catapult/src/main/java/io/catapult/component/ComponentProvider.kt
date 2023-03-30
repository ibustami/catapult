package io.catapult.component

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass

class ComponentProvider(var lifecycleOwner: LifecycleOwner?) : LifecycleEventObserver {

    val componentMap: MutableMap<View, Component<out UiView>> = mutableMapOf()

    inline fun <reified T : Component<out UiView>> add(
        componentClass: KClass<T>,
        view: View
    ): ComponentProvider {
        get(componentClass, view)
        return this
    }

    /**
     * add component and view to parent constraint layout without add component view in xml files
     * @topToBottom : id of the view that the current view will be added below
     */
    inline fun <reified T: Component<out UiView>, reified V: ViewBinding> addComponentView(
        binding: () -> V,
        parentLayout: ConstraintLayout,
        topToBottom: Int? = null
    ): Int {
        val view = binding().root
        add(T::class, view)
        addViewToConstraintLayout(parentLayout, view, topToBottom)
        return view.id
    }

    fun addViewToConstraintLayout(
        parentLayout: ConstraintLayout,
        currentView: View,
        previousViewId: Int? = null
    ) {
        val viewId = View.generateViewId()
        currentView.id = viewId
        parentLayout.addView(currentView)

        (currentView.layoutParams as ConstraintLayout.LayoutParams).apply {
            width = ConstraintLayout.LayoutParams.MATCH_PARENT
            height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            previousViewId?.let { topToBottom = it }
        }
    }

    inline fun <reified T : Component<out UiView>> get(componentClass: KClass<T>, view: View): T? {
        var component: T? = componentMap[view]?.let { it as T }
        return component
            ?: run {
                try {
                    val constructor = componentClass.constructors.first()
                    component = constructor.callBy(
                        mapOf(
                            constructor.parameters[0] to view,
                            constructor.parameters[1] to lifecycleOwner
                        )
                    )
                    componentMap[view] = component!!
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
                component
            }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            componentMap.clear()
            ComponentProviders.componentProviderMap.remove(lifecycleOwner)
            lifecycleOwner = null
        }
        if (event == Lifecycle.Event.ON_CREATE) {
            lifecycleOwner?.lifecycle?.addObserver(this)
        }
    }
}