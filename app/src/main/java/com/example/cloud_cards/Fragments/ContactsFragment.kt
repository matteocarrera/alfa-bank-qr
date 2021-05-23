package com.example.cloud_cards.Fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cloud_cards.Activities.CameraActivity
import com.example.cloud_cards.Adapters.CompanyAdapter
import com.example.cloud_cards.Adapters.ContactsAdapter
import com.example.cloud_cards.Database.AppDatabase
import com.example.cloud_cards.Entities.*
import com.example.cloud_cards.R
import com.example.cloud_cards.Utils.DataUtils
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_card_view.*
import kotlinx.android.synthetic.main.activity_qr.view.*
import kotlinx.android.synthetic.main.fragment_contacts.*

class ContactsFragment : Fragment() {

    private lateinit var db: AppDatabase
    private lateinit var searchView: SearchView
    private var contactList = ArrayList<User>()
    private var companyList = ArrayList<Company>()
    private var checkedItem = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val view = inflater.inflate(R.layout.fragment_contacts, container, false)

        // Устанавливаем Toolbar во фрагмент
        val toolbar = view.findViewById(R.id.contacts_toolbar) as MaterialToolbar
        toolbar.inflateMenu(R.menu.main_menu)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.sort -> {
                    // Сортировка контактов
                    val listItems = arrayOf("По имени", "По фамилии", "По компании", "По должности")
                    val mBuilder = AlertDialog.Builder(requireContext())
                    mBuilder.setTitle("Сортировать:")
                    mBuilder.setSingleChoiceItems(listItems, checkedItem) { dialogInterface, i ->
                        checkedItem = i
                        when (i) {
                            0 -> contactList.sortBy { it.name }
                            1 -> contactList.sortBy { it.surname }
                            2 -> contactList.sortBy { it.company }
                            3 -> contactList.sortBy { it.jobTitle }
                        }
                        contact_list.adapter?.notifyDataSetChanged()
                        dialogInterface.dismiss()
                    }
                    mBuilder.setNeutralButton("Отмена") { dialog, _ ->
                        dialog.cancel()
                    }

                    val mDialog = mBuilder.create()
                    mDialog.show()
                }
                R.id.camera -> {
                    if (cameraPermissionIsGranted()) {
                        val intent = Intent(context, CameraActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            true
        }

        // Устанавливаем поведение SearchView в Toolbar
        val searchItem = toolbar.menu.findItem(R.id.search)
        searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (tab_layout.selectedTabPosition == 0) {
                    val sortedContacts = contactList.filter { it.surname.lowercase().contains(newText?.lowercase() ?: String()) }
                    applyRecyclerView(ContactsAdapter(sortedContacts, this@ContactsFragment), R.drawable.divider_contacts)
                } else {
                    val sortedCompanies = companyList.filter { it.name.lowercase().contains(newText?.lowercase() ?: String()) }
                    applyRecyclerView(CompanyAdapter(sortedCompanies, this@ContactsFragment), R.drawable.divider_data)
                }
                return true
            }
        })

        // Устанавливаем TabBar во фрагмент, задаем адаптер для RecyclerView относительно Таба
        val tabBar = view.findViewById(R.id.tab_layout) as TabLayout
        tabBar.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                searchView.setQuery(String(), false)
                searchView.isIconified = true
                when (tab?.position) {
                    0 -> {
                        applyRecyclerView(ContactsAdapter(contactList, this@ContactsFragment), R.drawable.divider_contacts)
                    }
                    else -> {
                        applyRecyclerView(CompanyAdapter(companyList, this@ContactsFragment), R.drawable.divider_data)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        return view
    }

    override fun onResume() {
        super.onResume()

        if (!searchView.isIconified) {
            return
        }

        db = AppDatabase.getInstance(requireContext())
        contactList.clear()
        companyList.clear()
        progress_bar.visibility = View.VISIBLE

        val ownerUser = db.userDao().getOwnerUser()
        val idPairs = ArrayList(db.idPairDao().getAllContactsPairs(ownerUser?.uuid ?: String()))

        if (idPairs.size == 0) {
            scan_hint.visibility = View.VISIBLE
            progress_bar.visibility = View.GONE
            return
        }

        Thread {
            idPairs.forEach { idPair ->

                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(idPair.parentUuid)
                    .collection("cards")
                    .document(idPair.uuid)
                    .get()
                    .addOnSuccessListener { document ->

                        val cardTypeRaw = document.data?.get("type") as? String
                        val cardType = if (cardTypeRaw != null) CardType.valueOf(cardTypeRaw) else null
                        val businessCardUser = when (cardType) {
                            CardType.personal -> {
                                val businessCard = Gson().fromJson(Gson().toJson(document.data).toString(), BusinessCard::class.java)
                                Gson().fromJson(Gson().toJson(businessCard.data).toString(), UserBoolean::class.java)
                            }
                            CardType.company -> {
                                val businessCard = Gson().fromJson(Gson().toJson(document.data).toString(), BusinessCard::class.java)
                                val businessCardCompany = Gson().fromJson(Gson().toJson(businessCard.data).toString(), Company::class.java)
                                companyList.add(businessCardCompany)
                                checkForEndOfContactList(idPairs)
                                return@addOnSuccessListener
                            }
                            else -> {
                                Gson().fromJson(Gson().toJson(document.data).toString(), UserBoolean::class.java)
                            }
                        }

                        FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(idPair.parentUuid)
                            .collection("data")
                            .document(idPair.parentUuid)
                            .get()
                            .addOnSuccessListener { secondDocument ->

                                val mainUser = Gson().fromJson(Gson().toJson(secondDocument.data).toString(), User::class.java)
                                val currentUser = DataUtils.getUserFromTemplate(mainUser, businessCardUser)

                                contactList.add(currentUser)
                                checkForEndOfContactList(idPairs)
                            }
                            .addOnFailureListener { exception ->
                                Log.d("Error", "An error occurred while retrieving data about Parent User", exception)
                            }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("Error", "An error occurred while retrieving data about Card User", exception)
                    }
            }
        }.start()
    }

    /*
        Метод, позволяющий установить необходимые данные в RecyclerView
     */

    private fun applyRecyclerView(mAdapter: RecyclerView.Adapter<*>, drawableInt: Int) {
        val itemDecorator = DividerItemDecoration(
            context, DividerItemDecoration.VERTICAL
        )
        itemDecorator.setDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                drawableInt
            )!!
        )

        val list = view?.findViewById(R.id.contact_list) as RecyclerView
        if (list.itemDecorationCount != 0) contact_list.removeItemDecorationAt(0)
        list.adapter = null
        list.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
            addItemDecoration(itemDecorator)
        }
    }

    private fun checkForEndOfContactList(idPairs: List<IdPair>) {
        if (contactList.size + companyList.size == idPairs.size) {
            contactList.sortBy { it.surname }
            if (tab_layout.selectedTabPosition == 0) {
                applyRecyclerView(ContactsAdapter(contactList, this@ContactsFragment), R.drawable.divider_contacts)
            } else {
                applyRecyclerView(CompanyAdapter(companyList, this@ContactsFragment), R.drawable.divider_data)
            }
            progress_bar.visibility = View.GONE
            scan_hint.visibility = View.GONE
        }
    }

    /*
        Метод, позволяющий проверить наличие разрешений на взаимодействие с камерой
     */

    private fun cameraPermissionIsGranted(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                1)
            return false
        }
        return true
    }

    /*
        Метод, позволяющий обработать результат запроса на разрешение доступа к камере
     */

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(context, CameraActivity::class.java)
            startActivity(intent)
        }
    }
}