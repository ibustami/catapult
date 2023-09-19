package io.catapult

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.catapult.component.ComponentProvider
import io.catapult.component.ComponentProviders
import io.catapult.databinding.ActivityMainBinding
import io.catapult.databinding.ComponentMainBinding
import io.catapult.event.EventFlowable
import io.catapult.ui.component.CatapultComponent
import io.catapult.util.viewModelDelegate

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val eventFlowable: EventFlowable
        get() = EventFlowable.getInstance(this)

    private val viewModel: MainViewModel by viewModelDelegate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        EventFlowable.bindSubscriber(this, eventFlowable)
        bindComponent(ComponentProviders.of(this))
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchData(eventFlowable)
    }

    private fun bindComponent(componentProvider: ComponentProvider) {
        componentProvider.addComponentView<CatapultComponent, ComponentMainBinding>({
            ComponentMainBinding.inflate(layoutInflater)
        }, binding.root)
    }
}