package io.catapult.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KClass

// set alias Int to view type of item view layout
typealias ViewType = Int

open class SimpleAdapter(
    private val viewHolderProvider: (ViewType) -> KClass<ViewHolder<Any>>
) : ListAdapter<SimpleAdapter.Item<Any>, SimpleAdapter.ViewHolder<Any>>(SimpleDiffCallback()) {

    interface OnBindAdditional {
        fun onBindAdditional(vararg additionalItems: Any)
    }

    open class ViewHolder<T: Any>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        open fun bind(itemData: T) = Unit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Any> {
        val kClass = viewHolderProvider(viewType)
        val constructor = kClass.constructors.first()
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return constructor.call(view)
    }

    override fun onBindViewHolder(holder: ViewHolder<Any>, position: Int) {
        val item = getItem(position)
        holder.bind(item.itemData)

        if (holder is OnBindAdditional) {
            item.additionalItems?.let { additionalItems ->
                holder.onBindAdditional(*additionalItems)
            }
        }
    }

    fun addItems(viewType: ViewType, items: List<Any>, position: Int? = null, additionalItems: Array<Any>? = null) {
        val newList = items.map { Item(viewType, it, additionalItems) }
        val oldList = currentList.toMutableList()
        position?.let {
            oldList.addAll(position, newList)
        } ?: run {
            oldList.addAll(newList)
        }
        submitList(oldList)
    }

    fun addItem(viewType: ViewType, item: Any, position: Int? = null, additionalItems: Array<Any>? = null) {
        val newItem = Item(viewType, item, additionalItems)
        val oldList = currentList.toMutableList()
        position?.let {
            oldList.add(position, newItem)
        } ?: run {
            oldList.add(newItem)
        }
        submitList(oldList)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }

    data class Item<T>(
        val viewType: Int,
        val itemData: T,
        val additionalItems: Array<Any>? = null
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Item<*>

            if (viewType != other.viewType) return false
            if (itemData != other.itemData) return false
            if (additionalItems != null) {
                if (other.additionalItems == null) return false
                if (!additionalItems.contentEquals(other.additionalItems)) return false
            } else if (other.additionalItems != null) return false

            return true
        }

        override fun hashCode(): Int {
            var result = viewType
            result = 31 * result + itemData.hashCode()
            result = 31 * result + (additionalItems?.contentHashCode() ?: 0)
            return result
        }
    }

    class SimpleDiffCallback<T> : DiffUtil.ItemCallback<Item<T>>() {
        override fun areItemsTheSame(oldItem: Item<T>, newItem: Item<T>): Boolean {
            return oldItem.itemData == newItem.itemData
        }

        override fun areContentsTheSame(oldItem: Item<T>, newItem: Item<T>): Boolean {
            return oldItem == newItem
        }
    }

}
