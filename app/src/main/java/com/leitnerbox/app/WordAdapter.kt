package com.leitnerbox.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WordAdapter(
    private val onDelete: (Word) -> Unit
) : RecyclerView.Adapter<WordAdapter.VH>() {

    private val items = mutableListOf<Word>()

    fun submit(newItems: List<Word>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val foreign: TextView = view.findViewById(R.id.textForeign)
        val translation: TextView = view.findViewById(R.id.textTranslation)
        val delete: TextView = view.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_word, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.foreign.text = item.foreign
        holder.translation.text = item.translation
        holder.delete.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount(): Int = items.size
}
