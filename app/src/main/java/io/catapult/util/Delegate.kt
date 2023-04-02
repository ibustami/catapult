package io.catapult.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class ViewModelDelegate<VM : ViewModel>(
    private val viewModelClass: KClass<VM>,
    private val viewModelProvider: () -> ViewModelProvider
) : ReadOnlyProperty<Any?, VM>, ReadWriteProperty<Any?, VM> {
    private var viewModel: VM? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): VM {
        if (viewModel == null) {
            viewModel = viewModelProvider()[viewModelClass.java]
        }
        return viewModel!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: VM) {
        viewModel = value
    }
}

inline fun <reified VM : ViewModel> AppCompatActivity.viewModelDelegate(): ViewModelDelegate<VM> {
    val viewModelClass = VM::class
    return ViewModelDelegate(viewModelClass) { ViewModelProvider(this) }
}

inline fun <reified VM : ViewModel> Fragment.viewModelDelegate(): ViewModelDelegate<VM> {
    val viewModelClass = VM::class
    return ViewModelDelegate(viewModelClass) { ViewModelProvider(this) }
}