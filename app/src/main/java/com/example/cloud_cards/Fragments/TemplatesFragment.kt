package com.example.cloud_cards.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.cloud_cards.Database.AppDatabase
import com.example.cloud_cards.R
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.android.synthetic.main.fragment_templates.*

class TemplatesFragment : Fragment() {

    private lateinit var db : AppDatabase
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
                    tx.replace(R.id.nav_host_fragment, EditProfileFragment()).addToBackStack(null).commit()
                }
            }
            true
        }
        return view
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar()
    }

    private fun setToolbar() {
        templates_toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        templates_toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.add) Toast.makeText(requireContext(), "ADD", Toast.LENGTH_SHORT)
                .show()
            true
        }

    }
}