package com.example.cloudCards.Utils

import android.view.View
import android.widget.ImageView
import android.widget.ListView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.cloudCards.R

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

        fun setVisibility(layout: ConstraintLayout, list: ListView, image: ImageView) {
            layout.setOnClickListener {
                if (list.visibility == View.VISIBLE) {
                    list.visibility = View.GONE
                    image.setImageResource(R.drawable.ic_down)
                } else {
                    list.visibility = View.VISIBLE
                    image.setImageResource(R.drawable.ic_up)
                }
            }
        }
    }
}