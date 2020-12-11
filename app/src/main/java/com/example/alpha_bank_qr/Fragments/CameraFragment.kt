package com.example.alpha_bank_qr.Fragments

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.alpha_bank_qr.Database.AppDatabase
import com.example.alpha_bank_qr.Entities.UserBoolean
import com.example.alpha_bank_qr.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.zxing.Result
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.fragment_camera.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import com.example.alpha_bank_qr.Constants.TextConstants.ID_SEPARATOR
import com.example.alpha_bank_qr.Database.FirestoreInstance


class CameraFragment : Fragment() {

    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getInstance(requireContext())
        Dexter.withContext(view.context)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener, ZXingScannerView.ResultHandler {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    scanner.setResultHandler(this)
                    scanner.startCamera()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(view.context, "Необходим доступ к камере!", Toast.LENGTH_SHORT)
                        .show()
                    requireActivity().onBackPressed()
                }

                // Обрабатываем результат сканирования QR
                override fun handleResult(rawResult: Result?) {
                    if (rawResult != null) {
                        val databaseRef = FirestoreInstance.getInstance()
                        val splitLink = rawResult.toString().split(ID_SEPARATOR)
                        databaseRef.collection("users").document(splitLink[0]).collection("cards")
                            .document(splitLink[1]).addSnapshotListener { snapshot, e ->
                                if (e != null) {
                                    println("Ошибка считывания: " + e.code)
                                }

                                if (snapshot != null && snapshot.exists()) {
                                    val jsonCard = snapshot.data.toString()
                                    val userBoolean =
                                        Gson().fromJson(jsonCard, UserBoolean::class.java)
                                    val scannedCards = db.userBooleanDao().getAllUsersBoolean()

                                    var cardExists = false

                                    scannedCards.forEach {
                                        if (it.uuid == userBoolean.uuid) {
                                            cardExists = true
                                        }
                                    }

                                    when (cardExists) {
                                        true -> {
                                            Toast.makeText(
                                                view.context,
                                                "Визитка уже сущетвует!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            requireActivity().onBackPressed()
                                        }

                                        false -> {
                                            db.userBooleanDao().insertUserBoolean(userBoolean)
                                            Toast.makeText(
                                                view.context,
                                                "Визитка успешно считана!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            requireActivity().onBackPressed()
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        view.context,
                                        "Ошибка считывания визитки!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    requireActivity().onBackPressed()
                                }
                            }
                    }
                }
            }).check()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scanner.stopCamera()
    }

    override fun onPause() {
        super.onPause()
        scanner.stopCamera()
    }

    override fun onStop() {
        super.onStop()
        scanner.stopCamera()
    }
}