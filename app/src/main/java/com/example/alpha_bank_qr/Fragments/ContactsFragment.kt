package com.example.alpha_bank_qr.Fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alpha_bank_qr.Activities.MainActivity
import com.example.alpha_bank_qr.Adapters.ContactsAdapter
import com.example.alpha_bank_qr.Adapters.RecyclerItemClickListener
import com.example.alpha_bank_qr.Adapters.SelectedContactsAdapter
import com.example.alpha_bank_qr.Constants.TextConstants.ID_SEPARATOR
import com.example.alpha_bank_qr.Database.AppDatabase
import com.example.alpha_bank_qr.Database.FirestoreInstance
import com.example.alpha_bank_qr.Entities.User
import com.example.alpha_bank_qr.Entities.UserBoolean
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Utils.ProgramUtils
import com.example.alpha_bank_qr.Utils.DataUtils
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_contacts.*
import kotlinx.android.synthetic.main.selected_saved_card_list_item.view.*
import net.glxn.qrgen.android.QRCode

class ContactsFragment : Fragment() {

    private lateinit var db: AppDatabase
    private var multipleSelectionMode = false
    private val selectedItems = ArrayList<String>()
    private var actionMode: ActionMode? = null
    private var userContacts = ArrayList<UserBoolean>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireContext())

        setUsersToList(this) {
            contact_list.apply {
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = ContactsAdapter(it)
            }
        }

        contact_list.addOnItemTouchListener(
            RecyclerItemClickListener(context, contact_list, object :
                RecyclerItemClickListener.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    if (multipleSelectionMode) {
                        view.checkbox.isChecked = !view.checkbox.isChecked
                        val contactId = view.contactLink.text.toString()
                        if (selectedItems.contains(contactId)) selectedItems.remove(contactId)
                        else selectedItems.add(contactId)
                    } else {
                        val cardViewFragment =
                            CardViewFragment.newInstance(view.contactLink.text.toString())
                        val tx: FragmentTransaction =
                            requireParentFragment().parentFragmentManager.beginTransaction()
                        tx.replace(R.id.nav_host_fragment, cardViewFragment).addToBackStack(null)
                            .commit()
                    }
                }

                override fun onLongItemClick(view: View, position: Int) {
                    if (!multipleSelectionMode) {
                        setUsersToList(this@ContactsFragment) {
                            contact_list.adapter = null
                            contact_list.adapter = SelectedContactsAdapter(it)
                        }
                        multipleSelectionMode = true
                        selectedItems.clear()

                        setStandardToolbarVisibility(this@ContactsFragment, View.GONE)

                        actionMode =
                            (parentFragment?.requireActivity() as AppCompatActivity).startSupportActionMode(
                                actionModeCallback
                            )
                    }
                }
            })
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.search) Toast.makeText(
            requireContext(),
            "SEARCH",
            Toast.LENGTH_SHORT
        )
            .show()
        else if (item.itemId == R.id.sort) Toast.makeText(
            requireContext(),
            "SORT",
            Toast.LENGTH_SHORT
        )
            .show()
        return super.onOptionsItemSelected(item)
    }


    private val actionModeCallback: ActionMode.Callback = object : ActionMode.Callback {
        override fun onActionItemClicked(
            mode: ActionMode?,
            item: MenuItem?
        ): Boolean {
            if (item != null) {
                if (item.itemId == R.id.share) {
                    shareCards(this@ContactsFragment)
                    onDestroyActionMode(mode)
                } else if (item.itemId == R.id.delete) {
                    deleteCards(this@ContactsFragment)
                    onDestroyActionMode(mode)
                }
                return true
            }
            return false
        }

        override fun onCreateActionMode(
            mode: ActionMode?,
            menu: Menu?
        ): Boolean {
            mode?.menuInflater?.inflate(R.menu.selection_menu, menu)
            mode?.title = "Выберите визитки"
            setNavEnabled(this@ContactsFragment, false)
            return true
        }

        override fun onPrepareActionMode(
            mode: ActionMode?,
            menu: Menu?
        ): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            actionMode = null
            mode?.finish()
            setStandardToolbarVisibility(this@ContactsFragment, View.VISIBLE)

            contact_list.adapter = null
            setUsersToList(this@ContactsFragment) {
                contact_list.adapter = ContactsAdapter(it)
            }

            setNavEnabled(this@ContactsFragment, true)
            multipleSelectionMode = false
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ContactsFragment()

        //Полльзователь
        private fun setUsersToList(
            contactsFragment: ContactsFragment,
            callback: (lists: List<User>) -> Unit
        ) {
            val currentUserId = contactsFragment.db.userDao().getOwnerUser().uuid
            contactsFragment.userContacts.clear()
            contactsFragment.userContacts =
                ArrayList(contactsFragment.db.userBooleanDao().getContactUsers(currentUserId))

            val list: MutableList<UserBoolean> = mutableListOf()

            contactsFragment.userContacts.forEach { userBoolean ->
                val databaseRef =
                    FirestoreInstance.getInstance().collection("users")
                        .document(userBoolean.parentId)
                        .collection("data")
                        .document(userBoolean.parentId)
                databaseRef.get().addOnSuccessListener { document ->
                    val jsonUser = document.data.toString()
                    val userFromDB = Gson().fromJson(jsonUser, User::class.java)
                    list.add(userBoolean)
                    callback(list.map { DataUtils.getUserFromTemplate(userFromDB, it) })
                }.addOnFailureListener { e ->
                    println("Ошибка считывания: " + e.localizedMessage)
                }
            }
        }

        private fun deleteCards(contactsFragment: ContactsFragment) {
            if (contactsFragment.selectedItems.count() == 0) Toast.makeText(
                contactsFragment.requireContext(),
                "Вы не выбрали ни одной визитки!",
                Toast.LENGTH_SHORT
            ).show()
            else {
                val builder = AlertDialog.Builder(contactsFragment.requireContext())
                builder.setTitle("Удаление визиток")
                builder.setMessage("Вы действительно хотите удалить выбранные визитки?")
                builder.setPositiveButton("Да") { _, _ ->
                    contactsFragment.selectedItems.forEach {
                        val contactCard = contactsFragment.db.userBooleanDao()
                            .getUserBooleanById(it.split(ID_SEPARATOR)[1])
                        contactsFragment.db.userBooleanDao().deleteUserBoolean(contactCard)
                    }
                    Toast.makeText(
                        contactsFragment.requireContext(),
                        "Выбранные визитки успешно удалены!",
                        Toast.LENGTH_SHORT
                    ).show()
                    contactsFragment.selectedItems.clear()
                    contactsFragment.contact_list.adapter = null
                    setUsersToList(contactsFragment) {
                        contactsFragment.contact_list.adapter = ContactsAdapter(it)
                    }
                }
                builder.setNegativeButton("Нет") { _, _ -> }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }

        private fun setStandardToolbarVisibility(
            contactsFragment: ContactsFragment,
            visibility: Int
        ) {
            val navHostFragment = contactsFragment.parentFragment as CardsFragment
            navHostFragment.requireView().findViewById<View>(R.id.toolbar).visibility = visibility
        }

        private fun shareCards(contactsFragment: ContactsFragment) {
            val qrList = ArrayList<Bitmap>()
            if (contactsFragment.selectedItems.count() == 0) Toast.makeText(
                contactsFragment.requireContext(),
                "Вы не выбрали ни одной визитки!",
                Toast.LENGTH_SHORT
            ).show()
            else {
                contactsFragment.selectedItems.forEach {
                    var bitmap = QRCode.from(it).withCharset("utf-8").withSize(1000, 1000).bitmap()
                    bitmap = Bitmap.createScaledBitmap(bitmap, 1000, 1000, true)
                    qrList.add(bitmap)
                }
                ProgramUtils.saveImage(contactsFragment.requireContext(), qrList)
            }
        }

        private fun setNavEnabled(contactsFragment: ContactsFragment, visibility: Boolean) {
            val cardsFragment = contactsFragment.parentFragment as CardsFragment
            val mainActivity = cardsFragment.requireActivity() as MainActivity
            mainActivity.nav_view.isVisible = visibility
        }
    }
}