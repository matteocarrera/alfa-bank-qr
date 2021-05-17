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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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


    /* override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireContext())

        val cards = db.cardDao().getAllCards()

        templates_list.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = TemplatesAdapter(cards)
        }

        templates_list.addOnItemTouchListener(
            RecyclerItemClickListener(context, templates_list, object :
                RecyclerItemClickListener.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    if (view.card_qr.visibility == View.GONE) {
                        val user = db.userDao().getUserById(view.user_id.text.toString())
                        var bitmap = QRCode.from(user.id).withCharset("utf-8").withSize(1000, 1000).bitmap()
                        bitmap = Bitmap.createScaledBitmap(bitmap, 1000, 1000, true)
                        view.card_qr.visibility = View.VISIBLE
                        view.card_qr.setImageBitmap(bitmap)
                    } else {
                        view.card_qr.visibility = View.GONE
                    }
                }

                override fun onLongItemClick(view: View, position: Int) {
                    val popupMenu = PopupMenu(requireContext(), view.more)

                    popupMenu.setOnMenuItemClickListener { item ->

                        when(item.itemId) {
                            R.id.more -> {
                                val cardViewFragment = CardViewFragment.newInstance(view.user_id.text.toString())
                                val tx: FragmentTransaction = requireParentFragment().parentFragmentManager.beginTransaction()
                                tx.replace(R.id.nav_host_fragment, cardViewFragment).addToBackStack(null).commit()
                            }
                            R.id.share -> {
                                val user = db.userDao().getUserById(view.user_id.text.toString())
                                var bitmap = QRCode.from(user.id).withCharset("utf-8").withSize(1000, 1000).bitmap()
                                bitmap = Bitmap.createScaledBitmap(bitmap, 1000, 1000, true)
                                ProgramUtils.saveImage(view.context, arrayListOf(bitmap))
                            }
                            R.id.delete -> {
                                val builder = AlertDialog.Builder(requireContext())
                                builder.setTitle("Удаление визитки")
                                builder.setMessage("Вы действительно хотите удалить данную визитку?")
                                builder.setPositiveButton("Да"){ _, _ ->
                                    val card = db.cardDao().getCardById(view.card_id.text.toString().toInt())
                                    db.cardDao().deleteCard(card)
                                    Toast.makeText(requireContext(),"Визитка успешно удалена!", Toast.LENGTH_SHORT).show()

                                    val updatedCards = db.cardDao().getAllCards()
                                    templates_list.adapter = null
                                    templates_list.apply {
                                        layoutManager = LinearLayoutManager(requireActivity())
                                        adapter = TemplatesAdapter(updatedCards)
                                    }
                                }
                                builder.setNegativeButton("Нет"){ _, _ -> }
                                val dialog: AlertDialog = builder.create()
                                dialog.show()
                            }
                        }
                        true
                    }

                    popupMenu.menuInflater.inflate(R.menu.my_card_menu, popupMenu.menu)

                    popupMenu.show()
                }
            })
        )
    }
    */
}