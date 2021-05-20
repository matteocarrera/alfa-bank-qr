package com.example.cloud_cards.Adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cloud_cards.Activities.CardViewActivity
import com.example.cloud_cards.Database.AppDatabase
import com.example.cloud_cards.Entities.Company
import com.example.cloud_cards.Entities.User
import com.example.cloud_cards.Fragments.ContactsFragment
import com.example.cloud_cards.R
import com.example.cloud_cards.Utils.ProgramUtils

class CompanyHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.company_list_item, parent, false)) {

    private var name: TextView = itemView.findViewById(R.id.company_name)
    private var address: TextView = itemView.findViewById(R.id.company_address)
    private var email: TextView = itemView.findViewById(R.id.company_email)

    @SuppressLint("ResourceAsColor")
    fun bind(company: Company, fragment: ContactsFragment) {

        name.text = company.name
        address.text = if (company.address.isNotEmpty()) company.address else "Адрес не указан"
        email.text = if (company.email.isNotEmpty()) company.email else "Электронная почта не указана"

        this.itemView.setOnLongClickListener {
            val pop = PopupMenu(this.itemView.context, it, Gravity.END)
            pop.inflate(R.menu.contact_menu)

            val s = SpannableString(this.itemView.context.getString(R.string.delete))
            s.setSpan(ForegroundColorSpan(Color.RED), 0, s.length, 0)
            pop.menu.getItem(2).title = s

            pop.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.qr -> {
                        val contactLink = "http://cloudcards.h1n.ru/#${company.parentUuid}&${company.uuid}"
                        ProgramUtils.setQRWindow(fragment.context, contactLink)
                    }
                    R.id.share -> {
                        val link = "http://cloudcards.h1n.ru/#${company.parentUuid}&${company.uuid}"
                        ProgramUtils.showShareIntent(fragment.requireContext(), link)
                    }
                    R.id.delete -> {
                        val db = AppDatabase.getInstance(this.itemView.context)
                        val idPair = db.idPairDao().getIdPairById(company.uuid)
                        db.idPairDao().deletePair(idPair)
                        fragment.onActivityCreated(null)
                    }
                }
                true
            }
            pop.show()
            true
        }

        this.itemView.setOnClickListener {
            val intent = Intent(fragment.context, CardViewActivity::class.java)
            intent.putExtra("user", User())
            intent.putExtra("company", company)
            fragment.startActivity(intent)
        }
    }
}