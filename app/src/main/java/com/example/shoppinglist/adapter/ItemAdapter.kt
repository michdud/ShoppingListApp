package com.example.shoppinglist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.ShoppingListActivity
import com.example.shoppinglist.data.AppDatabase
import com.example.shoppinglist.data.Category
import com.example.shoppinglist.data.Item
import com.example.shoppinglist.touch.ItemTouchHelperCallback
import kotlinx.android.synthetic.main.item_row.view.*
import java.util.*

class ItemAdapter(
    private val context: Context,
    listItems: List<Item>
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>(), ItemTouchHelperCallback {
    private var itemList = mutableListOf<Item>()

    init {
        itemList.addAll(listItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemRow = LayoutInflater.from(context).inflate(
            R.layout.item_row, parent, false
        )

        return ViewHolder(itemRow)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[holder.adapterPosition]
        holder.btnDelete.setOnClickListener {
            deleteItem(holder.adapterPosition)
        }
        holder.cbStatus.isChecked = item.status
        holder.cbStatus.setOnClickListener {
            item.status = holder.cbStatus.isChecked
            updateItem(item)
        }
        setCategoryViewImage(holder.ivCategory, item.category)
        holder.tvQuantity.text =
            context.getString(R.string.quantity, item.quantity)
        holder.tvItemName.text = itemList[holder.adapterPosition].name
        holder.tvPrice.text = context.getString(R.string.x, "%.2f".format(item.price))
        holder.btnEdit.setOnClickListener {
            (context as ShoppingListActivity).showEditItemDialog(
                item, holder.adapterPosition
            )
        }

    }

    private fun updateItem(item: Item) {
        Thread {
            AppDatabase.getInstance(context).itemDao().updateItem(item)
        }.start()
    }

    fun updateItemOnPosition(item: Item, index: Int) {
        itemList.set(index, item)
        notifyItemChanged(index)
    }

    private fun setCategoryViewImage(ivCategory: ImageView, category: Category) {
        when (category) {
            Category.CLOTHING -> ivCategory.setImageResource(R.drawable.clothing)
            Category.ELECTRONICS -> ivCategory.setImageResource(R.drawable.phone)
            Category.ENTERTAINMENT -> ivCategory.setImageResource(R.drawable.entertainment)
            Category.FOOD -> ivCategory.setImageResource(R.drawable.food)
            Category.HOME -> ivCategory.setImageResource(R.drawable.home)
        }
    }

    private fun deleteItem(index: Int) {
        Thread {
            AppDatabase.getInstance(context).itemDao().deleteItem(itemList[index])

            (context as ShoppingListActivity).runOnUiThread {
                itemList.removeAt(index)
                notifyItemRemoved(index)
            }
        }.start()
    }

    fun deleteAllItems() {
        Thread {
            AppDatabase.getInstance(context).itemDao().deleteAllItems()

            (context as ShoppingListActivity).runOnUiThread {
                itemList.clear()
                notifyDataSetChanged()
            }
        }.start()
    }

    fun addItem(item: Item) {
        itemList.add(item)

        notifyItemInserted(itemList.lastIndex)
    }

    override fun onDismissed(position: Int) {
        deleteItem(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(itemList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnDelete = itemView.btnDelete
        val ivCategory = itemView.ivCategory
        val tvQuantity = itemView.tvQuantity
        val tvItemName = itemView.tvItemName
        val tvPrice = itemView.tvPrice
        val cbStatus = itemView.cbStatus
        val btnEdit = itemView.btnEdit
    }
}