package com.cobresun.behemoth.main

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.cobresun.behemoth.R
import com.cobresun.behemoth.models.Entry
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.fragment_main.*
import splitties.toast.toast

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private val entriesAdapter = EntryAdapter(emptyList())
    private val REQUEST_IMAGE_CAPTURE = 1

    private val args: MainFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        FirebaseFirestore.getInstance().collection("users")
            .document(args.userUid)
            .collection("objects")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val entries = snapshot.map {
                        Entry(
                            it.data["name"].toString(),
                            it.data["count"].toString().toInt()
                        )
                    }
                    viewModel.loadEntries(entries)
                }
            }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = entriesAdapter
        }

        newPicBtn.setOnClickListener {
            dispatchTakePictureIntent()
        }

        viewModel.entries.observe(viewLifecycleOwner, Observer {
            entriesAdapter.setData(it ?: emptyList())
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Retrieve and format image from camera as Bitmap
            val imageBitmap = (data!!.extras!!.get("data") as Bitmap)
                .copy(Bitmap.Config.ARGB_8888, true)
            labelImage(imageBitmap)
        }
    }

    private fun labelImage(imageBitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(imageBitmap)
        val labeler = FirebaseVision.getInstance().onDeviceImageLabeler

        labeler.processImage(image)
            .addOnSuccessListener { labels ->
                if (labels.size <= 0) {
                    toast("Nothing found!")
                    return@addOnSuccessListener
                }

                val label = labels[0]

                if (label.confidence >= 0.9) {
                    viewModel.upsertEntry(label.text, args.userUid)
                    toast("Added ${label.text}!")
                } else {
                    toast("Nothing found!")
                }
            }
            .addOnFailureListener { e ->
                toast(e.toString())
            }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

}
