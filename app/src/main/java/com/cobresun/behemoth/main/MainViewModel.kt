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
            Entry("Futsal shuffle", 69),
            Entry("Girl", 1),
            Entry("Hope", 0)
        )
    }

    fun addEntry (name : String){
        val current = _entries.value?: emptyList()
        val newEntries = current.toMutableList()

        // Insert into entries (update if exists) in alphabetical order
        if (newEntries.none { it.name == (name) }){
            val index = newEntries.indexOfFirst {entry -> entry.name > name}

            if (index == -1){
                newEntries.add(newEntries.size, Entry(name, 1))
            }
            else{
                newEntries.add(index, Entry(name, 1))
            }
        }
        else {
            val index = newEntries.indexOfFirst { it.name == (name) }
            val newCount = newEntries[index].count + 1
            val newEntry = Entry(name, newCount)

            newEntries.removeAt(index)
            newEntries.add(index, newEntry)
        }

        _entries.value = newEntries
    }

}
