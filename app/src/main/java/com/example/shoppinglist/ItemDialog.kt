package com.example.shoppinglist

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.shoppinglist.data.Category
import com.example.shoppinglist.data.Item
import kotlinx.android.synthetic.main.item_dialog.view.*

class ItemDialog : DialogFragment() {
    interface ItemHandler {
        fun itemCreated(item: Item)
        fun itemUpdated(item: Item)
    }

    private lateinit var itemHandler: ItemHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ItemHandler) {
            itemHandler = context
        } else {
            throw RuntimeException("The activity does not implement the ItemHandlerInterface")
        }
    }

    private lateinit var etName: EditText
    private lateinit var spCategory: Spinner
    private lateinit var etPrice: EditText
    private lateinit var etDescription: EditText
    private lateinit var cbBought: CheckBox
    private lateinit var etQuantity: EditText
    private var isEditMode = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(getString(R.string.new_item))

        val rootView = initViews()
        builder.setView(rootView)

        spCategory.adapter = ArrayAdapter<Category>(
            (itemHandler as Context),
            android.R.layout.simple_spinner_dropdown_item,
            Category.values()
        )

        isEditMode = ((arguments != null) && arguments!!.containsKey(ShoppingListActivity.KEY_ITEM))

        if (isEditMode) {
            builder.setTitle(getString(R.string.edit_item))
            populateEditMode()
        }

        builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
        }

        return builder.create()
    }

    private fun initViews(): View? {
        val rootView = requireActivity().layoutInflater.inflate(
            R.layout.item_dialog, null
        )
        etName = rootView.etName
        spCategory = rootView.spCategory
        etPrice = rootView.etPrice
        etDescription = rootView.etDescription
        cbBought = rootView.cbBought
        etQuantity = rootView.etQuantity
        return rootView
    }

    private fun populateEditMode() {
        val item: Item = (arguments?.getSerializable(ShoppingListActivity.KEY_ITEM) as Item)

        etName.setText(item.name)
        spCategory.setSelection((spCategory.adapter as ArrayAdapter<Category>).getPosition(item.category))
        etPrice.setText(item.price.toString())
        etDescription.setText(item.description)
        cbBought.isChecked = item.status
        etQuantity.setText(item.quantity.toString())
    }

    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (isEditMode) {
                handleItemEdit()
                (dialog as AlertDialog).dismiss()
            } else {
                if (etName.text.isNotEmpty() && etDescription.text.isNotEmpty() && etPrice.text.isNotEmpty() && etQuantity.text.isNotEmpty()) {
                    if (isEditMode) {
                        handleItemEdit()
                    } else {
                        handleItemCreate()
                    }
                    (dialog as AlertDialog).dismiss()
                } else {
                    setTextErrors()
                }
            }
        }
    }

    private fun setTextErrors() {
        if (etName.text.isEmpty()) {
            etName.error = getString(R.string.field_cannot_be_empty)
        }
        if (etDescription.text.isEmpty()) {
            etDescription.error = getString(R.string.field_cannot_be_empty)
        }
        if (etPrice.text.isEmpty()) {
            etPrice.error = getString(R.string.field_cannot_be_empty)
        }
        if (etQuantity.text.isEmpty()) {
            etQuantity.error = getString(R.string.field_cannot_be_empty)
        }
    }

    private fun handleItemCreate() {
        itemHandler.itemCreated(
            Item(
                null,
                Category.valueOf(spCategory.selectedItem.toString().toUpperCase()),
                etName.text.toString(),
                etDescription.text.toString(),
                etPrice.text.toString().toDouble(),
                cbBought.isChecked,
                etQuantity.text.toString().toInt()
            )
        )
    }

    private fun handleItemEdit() {
        var itemToEdit = arguments?.getSerializable(
            ShoppingListActivity.KEY_ITEM
        ) as Item
        itemToEdit.category = Category.valueOf(spCategory.selectedItem.toString().toUpperCase())
        itemToEdit.name = etName.text.toString()
        itemToEdit.description = etDescription.text.toString()
        itemToEdit.price = etPrice.text.toString().toDouble()
        itemToEdit.status = cbBought.isChecked
        itemToEdit.quantity = etQuantity.text.toString().toInt()

        itemHandler.itemUpdated(itemToEdit)
    }

}