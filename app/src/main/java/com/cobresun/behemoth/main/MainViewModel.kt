package com.cobresun.behemoth.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cobresun.behemoth.models.Entry
import com.google.firebase.firestore.FirebaseFirestore

class MainViewModel(private val userUid: String) : ViewModel() {

    class MainViewModelFactory(
        private val userUid: String
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(userUid) as T
        }
    }

    private val _entries: MutableLiveData<List<Entry>> = MutableLiveData()
    val entries: LiveData<List<Entry>> = _entries


    init {
        // TODO: Move to its own class
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(userUid)
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
                    _entries.value = entries.sortedBy { it.name }
                }
            }
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
