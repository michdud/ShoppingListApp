package com.example.shoppinglist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import java.lang.RuntimeException

class FilterDialog : BottomSheetDialogFragment() {

    private lateinit var tvClothingSum : TextView
    private lateinit var tvElectronicsSum : TextView
    private lateinit var tvEntertainmentSum : TextView
    private lateinit var tvFoodSum : TextView
    private lateinit var tvHomeSum : TextView
    private lateinit var tvTotalSum : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = requireActivity().layoutInflater.inflate(
            R.layout.bottom_sheet, null
        )

        tvClothingSum = rootView.tvClothingSum
        tvElectronicsSum = rootView.tvElectronicsSum
        tvEntertainmentSum = rootView.tvEntertainmentSum
        tvFoodSum = rootView.tvFoodSum
        tvHomeSum = rootView.tvHomeSum
        tvTotalSum = rootView.tvTotalSum

        if (arguments == null) {
            throw RuntimeException("No arguments passed to filter activity")
        }

        tvClothingSum.text = context!!.getString(R.string.dollar, "%.2f".format(arguments!!.get(ShoppingListActivity.CLOTHING)))
        tvElectronicsSum.text = context!!.getString(R.string.dollar, "%.2f".format(arguments!!.get(ShoppingListActivity.ELECTRONICS)))
        tvEntertainmentSum.text = context!!.getString(R.string.dollar, "%.2f".format(arguments!!.get(ShoppingListActivity.ENTERTAINMENT)))
        tvFoodSum.text = context!!.getString(R.string.dollar, "%.2f".format(arguments!!.get(ShoppingListActivity.FOOD)))
        tvHomeSum.text = context!!.getString(R.string.dollar, "%.2f".format(arguments!!.get(ShoppingListActivity.HOME)))
        tvTotalSum.text = context!!.getString(R.string.dollar, "%.2f".format(arguments!!.get(ShoppingListActivity.TOTAL)))

        return rootView
    }
}