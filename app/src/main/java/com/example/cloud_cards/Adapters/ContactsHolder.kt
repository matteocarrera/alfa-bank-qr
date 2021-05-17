package com.example.cloud_cards.Adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cloud_cards.Activities.CardViewActivity
import com.example.cloud_cards.Database.AppDatabase
import com.example.cloud_cards.Entities.User
import com.example.cloud_cards.Fragments.ContactsFragment
import com.example.cloud_cards.R
import com.example.cloud_cards.Utils.ImageUtils
import com.example.cloud_cards.Utils.ProgramUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.contact_list_item.view.*

class ContactsHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.contact_list_item, parent, false)) {

    private var photo: CircleImageView = itemView.findViewById(R.id.photo)
    private var id: TextView = itemView.findViewById(R.id.contact_id)
    private var name: TextView = itemView.findViewById(R.id.name)
    private var jobTitle: TextView = itemView.findViewById(R.id.job_title)
    private var company: TextView = itemView.findViewById(R.id.company)

    @SuppressLint("ResourceAsColor")
    fun bind(user: User, fragment: ContactsFragment) {
        if (user.photo == "") {
            itemView.letters.visibility = View.VISIBLE
            itemView.letters.text = user.name.take(1).plus(user.surname.take(1))
        } else {
            photo.visibility = View.VISIBLE
            itemView.letters.visibility = View.GONE
            ImageUtils.getImageFromFirebase(user.photo, photo)
        }
        id.text = user.uuid
        name.text = user.name.plus(" ").plus(user.surname)
        jobTitle.text = if (user.jobTitle.isNotEmpty()) user.jobTitle else "Должность не указана"
        company.text = if (user.company.isNotEmpty()) user.company else "Компания не указана"

        this.itemView.setOnLongClickListener {
            val pop = PopupMenu(this.itemView.context, it, Gravity.END)
            pop.inflate(R.menu.contact_menu)

            val s = SpannableString(this.itemView.context.getString(R.string.delete))
            s.setSpan(ForegroundColorSpan(Color.RED), 0, s.length, 0)
            pop.menu.getItem(2).title = s

            pop.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.qr -> {
                        val contactLink = "http://cloudcards.h1n.ru/#${user.parentId}&${user.uuid}"
                        ProgramUtils.setQRWindow(fragment.context, contactLink)
                    }
                    R.id.share -> {
                    }
                    R.id.delete -> {
                        val db = AppDatabase.getInstance(this.itemView.context)
                        val idPair = db.idPairDao().getIdPairById(user.uuid)
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
            intent.putExtra("user", user)
            fragment.startActivity(intent)
        }
    }
}