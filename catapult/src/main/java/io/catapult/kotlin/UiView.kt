package io.catapult.kotlin

import android.content.Context
import android.view.View

open class UiView(protected var view: View, protected var eventFlowable: EventFlowable) {

    val context: Context
        get() = view.context

    open fun show() {
        view.visibility = View.VISIBLE
    }

    open fun hide() {
        view.visibility = View.GONE
    }
}