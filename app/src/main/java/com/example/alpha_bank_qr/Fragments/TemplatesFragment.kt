package com.example.alpha_bank_qr.Fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alpha_bank_qr.Activities.CardsActivity
import com.example.alpha_bank_qr.Adapters.RecyclerItemClickListener
import com.example.alpha_bank_qr.Adapters.TemplatesAdapter
import com.example.alpha_bank_qr.Database.AppDatabase
import com.example.alpha_bank_qr.Database.DBService
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Utils.ProgramUtils
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_card.*
import kotlinx.android.synthetic.main.fragment_templates.*
import kotlinx.android.synthetic.main.my_card_list_item.view.*
import kotlinx.android.synthetic.main.my_card_list_item.view.more
import net.glxn.qrgen.android.QRCode

class TemplatesFragment : Fragment() {

    private lateinit var db : AppDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_templates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
                                Toast.makeText(requireContext(), "Подробно", Toast.LENGTH_SHORT).show()
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

    companion object {
        @JvmStatic
        fun newInstance() =
            TemplatesFragment()
    }
}