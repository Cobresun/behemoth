package com.cobresun.behemoth.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cobresun.behemoth.models.Entry
import com.google.firebase.firestore.FirebaseFirestore

class MainViewModel : ViewModel() {

    private val _entries: MutableLiveData<List<Entry>> = MutableLiveData()
    val entries: LiveData<List<Entry>> = _entries

    fun addEntry (entry: Entry){
        val current = _entries.value?: emptyList()
        val newEntries = current.toMutableList()

        if (newEntries.none { it.name == (entry.name) }) {
            var index = newEntries.indexOfFirst {ent ->
                ent.name.compareTo(entry.name, true) > 0}

            if (index == -1){
                index = newEntries.size
            }

            newEntries.add(index, entry)
            _entries.value = newEntries
        }
    }

    fun updateEntry (name : String, userID : String){
        val current = _entries.value?: emptyList()
        val newEntries = current.toMutableList()
        val db = FirebaseFirestore.getInstance()

        // Insert into entries (update if exists) in alphabetical order
        if (newEntries.none { it.name == (name) }){
            var index = newEntries.indexOfFirst {entry ->
                entry.name.compareTo(name, true) > 0}

            var newEntry = Entry(name, 1)

            if (index == -1){
                index = newEntries.size
            }

            newEntries.add(index, newEntry)

            val entryObj = hashMapOf(
                "count" to 1,
                "name" to name
            )

            db.collection("users")
                .document(userID)
                .collection("objects")
                .document(name)
                .set(entryObj)
        }
        else {
            val index = newEntries.indexOfFirst { ent -> ent.name == name }

            val newCount = newEntries[index].count + 1
            val newEntry = Entry(name, newCount)

            newEntries.removeAt(index)
            newEntries.add(index, newEntry)

            val entryObj = hashMapOf(
                "count" to newCount,
                "name" to name
            )

            db.collection("users")
                .document(userID)
                .collection("objects")
                .document(name)
                .set(entryObj)
        }

        _entries.value = newEntries
    }

}
