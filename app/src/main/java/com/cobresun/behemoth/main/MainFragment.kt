package com.cobresun.behemoth.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.cobresun.behemoth.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_main.*
import splitties.toast.toast

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private val entriesAdapter = EntryAdapter(emptyList())

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
                    toast("Listen failed. $e")
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    toast("Current number of objects: ${snapshot.size()}")
                } else {
                   toast( "Current data: null")
                }
            }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = entriesAdapter
        }

        newPicBtn.setOnClickListener {
            toast("TODO: Open Camera")
        }

        viewModel.entries.observe(viewLifecycleOwner, Observer {
            entriesAdapter.setData(it ?: emptyList())
        })
    }

}
