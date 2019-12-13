package com.example.shoppinglist

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.adapter.ItemAdapter
import com.example.shoppinglist.data.AppDatabase
import com.example.shoppinglist.data.Category
import com.example.shoppinglist.data.Item
import com.example.shoppinglist.touch.ItemRecyclerTouchCallback
import kotlinx.android.synthetic.main.item_row.*
import kotlinx.android.synthetic.main.shopping_list.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class ShoppingListActivity : AppCompatActivity(), ItemDialog.ItemHandler {

    companion object {
        const val KEY_ITEM = "KEY_ITEM"
        const val KEY_STARTED = "KEY_STARTED"
        const val TAG_ITEM_EDIT = "TAG_ITEM_EDIT"
        const val TAG_ITEM_DIALOG = "TAG_ITEM_DIALOG"
        const val TAG_FILTER = "TAG_FILTER"
        const val CLOTHING = "CLOTHING"
        const val ELECTRONICS = "ELECTRONICS"
        const val ENTERTAINMENT = "ENTERTAINMENT"
        const val FOOD = "FOOD"
        const val HOME = "HOME"
        const val TOTAL = "TOTAL"
    }

    lateinit var itemAdapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shopping_list)
        setSupportActionBar(toolbar)

        fabAdd.setOnClickListener {
            showAddItemDialog()
        }

        initRecyclerView()
        if (!wasStartedBefore()) {
            showTargetPrompt()
        }
    }

    private fun showTargetPrompt() {
        MaterialTapTargetPrompt.Builder(this).setTarget(R.id.fabAdd)
            .setPrimaryText(getString(R.string.new_item))
            .setSecondaryText(getString(R.string.tap_here))
            .setBackgroundColour(resources.getColor(R.color.colorPrimaryDark)).show()
        saveWasStarted()
    }

    private fun saveWasStarted() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPref.edit()
        editor.putBoolean(KEY_STARTED, true)
        editor.apply()
    }

    private fun wasStartedBefore(): Boolean {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPref.getBoolean(KEY_STARTED, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter -> {
                showFilterDialog()
                true
            }
            R.id.deleteAll -> {
                itemAdapter.deleteAllItems()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun showFilterDialog() {
        Thread {
            val database = AppDatabase.getInstance(this@ShoppingListActivity).itemDao()
            val clothingSum = database.getTotalPriceOfCategory(Category.CLOTHING)
            val electronicsSum = database.getTotalPriceOfCategory(Category.ELECTRONICS)
            val entertainmentSum = database.getTotalPriceOfCategory(Category.ENTERTAINMENT)
            val foodSum = database.getTotalPriceOfCategory(Category.FOOD)
            val homeSum = database.getTotalPriceOfCategory(Category.HOME)
            val totalSum = database.getTotalPrice()

            runOnUiThread {
                val bundle = Bundle()
                bundle.putDouble(CLOTHING, clothingSum)
                bundle.putDouble(ELECTRONICS, electronicsSum)
                bundle.putDouble(ENTERTAINMENT, entertainmentSum)
                bundle.putDouble(FOOD, foodSum)
                bundle.putDouble(HOME, homeSum)
                bundle.putDouble(TOTAL, totalSum)
                val filterDialog = FilterDialog()
                filterDialog.arguments = bundle
                filterDialog.show(supportFragmentManager, TAG_FILTER)
            }
        }.start()
    }

    private fun initRecyclerView() {
        Thread {
            val itemList =
                AppDatabase.getInstance(this@ShoppingListActivity).itemDao().getAllItems()

            runOnUiThread {
                itemAdapter = ItemAdapter(this, itemList)
                recyclerItem.adapter = itemAdapter

                addScrollListenerToRecyclerView(itemList)
                addItemDecoration()
                addTouchCallback()
            }
        }.start()
    }

    private fun addTouchCallback() {
        val callback = ItemRecyclerTouchCallback(itemAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerItem)
    }

    private fun addItemDecoration() {
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerItem.addItemDecoration(itemDecoration)
    }

    private fun addScrollListenerToRecyclerView(
        itemList: List<Item>
    ) {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(
            displayMetrics
        )

        recyclerItem.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    fabAdd.hide()
                } else if (dy < 0) {
                    fabAdd.show()
                } else if (itemList.size * itemLayout.measuredHeight < displayMetrics.heightPixels) {
                    // edge case where an item is removed after scroll down/FAB is hidden
                    fabAdd.show()
                }
            }
        }
        )
    }

    private fun showAddItemDialog() {
        ItemDialog().show(supportFragmentManager, TAG_ITEM_DIALOG)
    }

    var editIndex: Int = -1

    fun showEditItemDialog(itemToEdit: Item, index: Int) {
        editIndex = index

        val editDialog = ItemDialog()
        val bundle = Bundle()
        bundle.putSerializable(KEY_ITEM, itemToEdit)
        editDialog.arguments = bundle
        editDialog.show(supportFragmentManager, TAG_ITEM_EDIT)
    }

    private fun saveItem(item: Item) {
        Thread {
            val newId = AppDatabase.getInstance(this).itemDao().insertItem(
                item
            )

            item.itemId = newId

            runOnUiThread {
                itemAdapter.addItem(item)
            }
        }.start()
    }

    override fun itemCreated(item: Item) {
        saveItem(item)
    }

    override fun itemUpdated(item: Item) {
        Thread {
            AppDatabase.getInstance(this@ShoppingListActivity).itemDao().updateItem(item)

            runOnUiThread {
                itemAdapter.updateItemOnPosition(item, editIndex)
            }
        }.start()
    }
}