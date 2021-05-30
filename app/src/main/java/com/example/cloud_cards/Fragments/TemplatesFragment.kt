package com.example.cloud_cards.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cloud_cards.Activities.CreateCompanyCardActivity
import com.example.cloud_cards.Activities.CreatePersonalCardActivity
import com.example.cloud_cards.Adapters.TemplatesAdapter
import com.example.cloud_cards.Database.AppDatabase
import com.example.cloud_cards.Entities.CardType
import com.example.cloud_cards.Entities.User
import com.example.cloud_cards.Entities.UserBoolean
import com.example.cloud_cards.R
import com.example.cloud_cards.Utils.ProgramUtils
import com.example.cloud_cards.Utils.QRUtils
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_templates.*

class TemplatesFragment : Fragment() {

    private lateinit var db : AppDatabase
    private lateinit var templateUserList: ArrayList<UserBoolean>
    private var ownerUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_templates, container, false)
        val toolbar = view.findViewById(R.id.templates_toolbar) as MaterialToolbar
        toolbar.inflateMenu(R.menu.add_template_menu)
        toolbar.setOnMenuItemClickListener { item ->
            if (db.userDao().getOwnerUser() == null) {
                Toast.makeText(context, "Создайте профиль в настройках, чтобы открыть доступ к созданию визиток!", Toast.LENGTH_SHORT).show()
                return@setOnMenuItemClickListener false
            }
            when (item.itemId) {
                R.id.personal_card -> {
                    val intent = Intent(context, CreatePersonalCardActivity::class.java)
                    intent.putParcelableArrayListExtra("templateUserList", templateUserList)
                    startActivity(intent)
                }
                R.id.company_card -> {
                    val intent = Intent(context, CreateCompanyCardActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
        toolbar.overflowIcon = resources.getDrawable(R.drawable.ic_add)

        db = AppDatabase.getInstance(requireContext())
        ownerUser = db.userDao().getOwnerUser()
        val templatesIdPairList = db.idPairDao().getAllTemplatesPairs(ownerUser?.parentId)
        templateUserList = ArrayList()

        templatesIdPairList.forEach { idPair ->
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(idPair.parentUuid)
                .collection("cards")
                .document(idPair.uuid)
                .get()
                .addOnSuccessListener { document ->
                    val templateUser = Gson().fromJson(Gson().toJson(document.data).toString(), UserBoolean::class.java)

                    templateUserList.add(templateUser)
                }
                .addOnFailureListener { exception ->
                    Log.d("Error", "get failed with ", exception)
                }
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        val cards = db.cardDao().getAllCards()
        val templateCards = cards.toMutableList()
        templateCards.add(null)

        templates_grid.adapter = TemplatesAdapter(this, templateCards)

        templates_grid.onItemClickListener = AdapterView.OnItemClickListener { _, view, position, _ ->
            if (templateCards[position] == null) {
                if (db.userDao().getOwnerUser() == null) {
                    Toast.makeText(context, "Создайте профиль в настройках, чтобы открыть доступ к созданию визиток!", Toast.LENGTH_SHORT).show()
                    return@OnItemClickListener
                }
                val pop = PopupMenu(context, view, Gravity.END)
                pop.inflate(R.menu.add_template_menu)

                pop.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.personal_card -> {
                            val intent = Intent(context, CreatePersonalCardActivity::class.java)
                            intent.putParcelableArrayListExtra("templateUserList", templateUserList)
                            startActivity(intent)
                        }
                        R.id.company_card -> {
                            val intent = Intent(context, CreateCompanyCardActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    true
                }
                pop.show()
            } else {
                val card = templateCards[position]
                val link = QRUtils.generateSiteLink(ownerUser!!.uuid, card!!.cardUuid, card.type == CardType.personal)
                ProgramUtils.setQRWindow(context, link)
            }
        }
    }
}