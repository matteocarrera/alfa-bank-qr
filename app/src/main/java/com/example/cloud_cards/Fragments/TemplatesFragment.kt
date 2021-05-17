package com.example.cloud_cards.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.cloud_cards.Adapters.TemplatesAdapter
import com.example.cloud_cards.Database.AppDatabase
import com.example.cloud_cards.Entities.User
import com.example.cloud_cards.Entities.UserBoolean
import com.example.cloud_cards.R
import com.example.cloud_cards.Utils.ProgramUtils
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
            when (item.itemId) {
                R.id.add -> {
                    val tx: FragmentTransaction = parentFragmentManager.beginTransaction()
                    tx.replace(R.id.nav_host_fragment, AddFragment(templateUserList)).addToBackStack(null).commit()
                }
            }
            true
        }

        db = AppDatabase.getInstance(requireContext())
        ownerUser = db.userDao().getOwnerUser()
        val templatesIdPairList = db.idPairDao().getAllTemplatesPairs(ownerUser?.parentId)
        templateUserList = ArrayList()

        templatesIdPairList.forEach { idPair ->
            FirebaseFirestore.getInstance()
                .collection("users").document(idPair.parentUuid)
                .collection("cards").document(idPair.uuid)
                .get().addOnSuccessListener { document ->
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

        templates_grid.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            if (templateCards[position] == null) {
                val tx: FragmentTransaction = parentFragmentManager.beginTransaction()
                tx.replace(R.id.nav_host_fragment, AddFragment(templateUserList)).addToBackStack(null).commit()
            } else {
                val templateLink = "http://cloudcards.h1n.ru/#${ownerUser?.uuid}&${templateCards[position]?.cardUuid}"
                ProgramUtils.setQRWindow(context, templateLink)
            }
        }
    }
}