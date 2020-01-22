package com.cobresun.behemoth.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cobresun.behemoth.models.Entry
import com.google.firebase.firestore.FirebaseFirestore

class MainViewModel : ViewModel() {

    private val _entries: MutableLiveData<List<Entry>> = MutableLiveData()
    val entries: LiveData<List<Entry>> = _entries


    fun loadEntries(entries: List<Entry>) {
        _entries.value = entries.sortedBy { it.name }
    }

    fun upsertEntry(name: String, userID: String) {
        val current = _entries.value ?: emptyList()

        if (current.any { it.name == name }) {
            updateEntry(current, name, userID)
        } else {
            insertEntry(current, name, userID)
        }
    }

    private fun insertEntry(current: List<Entry>, name: String, userID: String) {
        val newEntries = current.toMutableList()
        val newEntry = Entry(name, 1)
        newEntries.add(newEntry)
        _entries.value = newEntries.sortedBy { it.name }
        pushToFirebase(newEntry, userID)
    }

    private fun updateEntry(current: List<Entry>, name: String, userID: String) {
        val newEntries = current.toMutableList()
        val targetIndex = current.indexOfFirst { it.name == name }

        val newCount = current[targetIndex].count + 1
        val newEntry = Entry(name, newCount)

        newEntries.removeAt(targetIndex)
        newEntries.add(targetIndex, newEntry)
        _entries.value = newEntries.sortedBy { it.name }

        pushToFirebase(newEntry, userID)
    }

    private fun pushToFirebase(entry: Entry, userID: String) {
        val db = FirebaseFirestore.getInstance()

        val entryObj = hashMapOf(
            "count" to entry.count,
            "name" to entry.name
        )
        db.collection("users")
            .document(userID)
            .collection("objects")
            .document(entry.name)
            .set(entryObj)
    }

}
