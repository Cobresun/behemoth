package com.cobresun.behemoth.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.cobresun.behemoth.R
import com.cobresun.behemoth.models.Entry
import kotlinx.android.synthetic.main.item_entry_card.view.*

class EntryAdapter(
    private var data: List<Entry>
) : RecyclerView.Adapter<EntryAdapter.ViewHolder>() {

    class ViewHolder(itemView: CardView) : RecyclerView.ViewHolder(itemView) {
        val countTextView: TextView = itemView.countTextView
        val nameTextView: TextView = itemView.nameTextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_entry_card, parent, false) as CardView
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.countTextView.text = data[position].count.toString()
        holder.nameTextView.text = data[position].name
    }

    fun setData(newData: List<Entry>) {
        data = newData
        notifyDataSetChanged()
    }

}
