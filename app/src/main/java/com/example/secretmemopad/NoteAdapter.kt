package com.example.secretmemopad

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.secretmemopad.databinding.ItemNoteBinding

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private var note: List<Notes> = listOf()

    inner class NoteViewHolder(private val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        private val title: TextView = binding.Title
        private val note: TextView = binding.Note

        fun bind(notes: Notes) {
            title.text = notes.title
            note.text = notes.note
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(note[position])
    }

    override fun getItemCount(): Int {
        return note.size
    }

    fun submitList(newOrders: List<Notes>?) {
        newOrders?.let {
            note = it
            notifyDataSetChanged()
        }
    }


}
