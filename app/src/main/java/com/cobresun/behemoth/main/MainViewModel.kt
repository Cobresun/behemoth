package com.cobresun.behemoth.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cobresun.behemoth.models.Entry

class MainViewModel : ViewModel() {

    private val _entries: MutableLiveData<List<Entry>> = MutableLiveData()
    val entries: LiveData<List<Entry>> = _entries

    init {
        _entries.value = listOf(
            Entry(1, "Girl"),
            Entry(1000, "Cat")
        )
    }

}