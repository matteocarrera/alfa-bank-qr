package com.example.cloud_cards.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.cloud_cards.Database.AppDatabase
import com.example.cloud_cards.Entities.Card
import com.example.cloud_cards.Fragments.TemplatesFragment
import com.example.cloud_cards.R

internal class TemplatesAdapter(private val fragment: TemplatesFragment, private val templateCards: List<Card?>) : BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null

    override fun getCount(): Int {
        return templateCards.size
    }

    override fun getItem(position: Int): Any? {
        return templateCards[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (layoutInflater == null) {
            layoutInflater =
                fragment.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.template_grid_item, null)
        }
        val cardContainer = convertView?.findViewById(R.id.card_container) as CardView
        val cardTitleView = convertView.findViewById(R.id.card_title) as TextView
        val cardMenuImage = convertView.findViewById(R.id.card_menu_image) as ImageView
        val cardTypeImage = convertView.findViewById(R.id.card_type_image) as ImageView

        if (templateCards[position] == null) {
            cardTitleView.text = "Создать визитку"
            cardMenuImage.visibility = View.INVISIBLE
            cardTypeImage.setImageResource(R.drawable.ic_add_round)
            cardContainer.setCardBackgroundColor(ContextCompat.getColor(fragment.requireContext(), R.color.colorPrimary))

            return convertView
        }

        cardTitleView.text = templateCards[position]?.title
        cardTypeImage.setImageResource(R.drawable.ic_user)
        cardContainer.setCardBackgroundColor(Color.parseColor(templateCards[position]?.color))

        cardMenuImage.setOnClickListener {
            val pop = PopupMenu(convertView.context, it, Gravity.END)
            pop.inflate(R.menu.templates_menu)

            val s = SpannableString(convertView.context.getString(R.string.delete))
            s.setSpan(ForegroundColorSpan(Color.RED), 0, s.length, 0)
            pop.menu.getItem(2).title = s

            pop.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.more -> {
                    }
                    R.id.share -> {
                    }
                    R.id.delete -> {
                        val db = AppDatabase.getInstance(fragment.requireContext())
                        db.cardDao().deleteCard(templateCards[position]!!)
                        fragment.onActivityCreated(null)
                    }
                }
                true
            }
            pop.show()
        }

        return convertView
    }
}