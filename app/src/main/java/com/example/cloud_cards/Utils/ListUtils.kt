package com.example.cloud_cards.Utils

import android.view.View
import android.widget.ListView

class ListUtils {
    companion object {
        fun setDynamicHeight(mListView: ListView) {
            val mListAdapter = mListView.adapter ?: return
            var height = 0
            val desiredWidth =
                View.MeasureSpec.makeMeasureSpec(mListView.width, View.MeasureSpec.UNSPECIFIED)
            for (i in 0 until mListAdapter.count) {
                val listItem = mListAdapter.getView(i, null, mListView)
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
                height += listItem.measuredHeight
            }
            val params = mListView.layoutParams
            params.height = height + mListView.dividerHeight * (mListAdapter.count - 1)
            mListView.layoutParams = params
            mListView.requestLayout()
        }
    }
}