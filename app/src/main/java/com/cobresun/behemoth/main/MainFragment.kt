package com.cobresun.behemoth.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.cobresun.behemoth.R
import kotlinx.android.synthetic.main.fragment_main.*
import splitties.toast.toast

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private val entriesAdapter = EntryAdapter(emptyList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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
