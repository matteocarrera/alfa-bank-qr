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
import com.example.cloud_cards.Utils.QRUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_contacts.*

class ContactsHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.contact_list_item, parent, false)) {

    private var photo: CircleImageView = itemView.findViewById(R.id.photo)
    private var name: TextView = itemView.findViewById(R.id.name)
    private var jobTitle: TextView = itemView.findViewById(R.id.job_title)
    private var company: TextView = itemView.findViewById(R.id.company)
    private var letters: TextView = itemView.findViewById(R.id.letters)

    @SuppressLint("ResourceAsColor")
    fun bind(user: User, fragment: ContactsFragment) {
        if (user.photo == "") {
            letters.visibility = View.VISIBLE
            letters.text = user.name.take(1).plus(user.surname.take(1))
            photo.setImageResource(R.color.colorPrimary)
        } else {
            letters.visibility = View.GONE
            ImageUtils.getImageFromFirebase(user.photo, photo)
        }
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
                        val link = QRUtils.generateSiteLink(user.parentId, user.uuid, true)
                        ProgramUtils.setQRWindow(fragment.context, link)
                    }
                    R.id.share -> {
                        val link = QRUtils.generateSiteLink(user.parentId, user.uuid, true)
                        ProgramUtils.showShareIntent(fragment.requireContext(), link)
                    }
                    R.id.delete -> {
                        val db = AppDatabase.getInstance(this.itemView.context)
                        val idPair = db.idPairDao().getIdPairById(user.uuid)
                        db.idPairDao().deletePair(idPair)
                        fragment.contact_list.adapter?.notifyDataSetChanged()
                        fragment.onResume()
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