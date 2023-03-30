package io.catapult.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

open class SimpleAdapter<T>(
    private val viewHolderProvider: (ViewGroup, Int) -> ViewHolder<T>
) : ListAdapter<SimpleAdapter.Item<T>, SimpleAdapter.ViewHolder<T>>(SimpleDiffCallback()) {

    interface OnBindAdditional {
        fun onBindAdditional(vararg additionalItems: Any)
    }

    abstract class ViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(itemData: T)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return viewHolderProvider(parent, viewType).apply { itemView.tag = view }
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        val item = getItem(position)
        holder.bind(item.itemData)

        if (holder is OnBindAdditional) {
            item.additionalItems?.let { additionalItems ->
                holder.onBindAdditional(*additionalItems)
            }
        }
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

            return true
        }

        override fun hashCode(): Int {
            var result = viewType
            result = 31 * result + (itemData?.hashCode() ?: 0)
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
