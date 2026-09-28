package com.leitnerbox.app

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.leitnerbox.app.databinding.ActivityWordsBinding

class WordsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWordsBinding
    private lateinit var adapter: WordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Store.init(applicationContext)

        adapter = WordAdapter(
            onDelete = { word -> confirmDelete(word) },
            onEdit = { word -> showEditDialog(word) }
        )
        binding.recyclerWords.layoutManager = LinearLayoutManager(this)
        binding.recyclerWords.adapter = adapter

        binding.buttonAdd.setOnClickListener { addWord() }
        binding.buttonBack.setOnClickListener { finish() }

        refreshList()
    }

    private fun addWord() {
        val foreign = binding.editForeign.text?.toString()?.trim().orEmpty()
        val forms = binding.editForms.text?.toString()?.trim().orEmpty()
        val translation = binding.editTranslation.text?.toString()?.trim().orEmpty()
        if (foreign.isEmpty() || translation.isEmpty()) return
        Store.addWord(foreign, forms, translation)
        binding.editForeign.text?.clear()
        binding.editForms.text?.clear()
        binding.editTranslation.text?.clear()
        binding.editForeign.requestFocus()
        refreshList()
    }

    private fun confirmDelete(word: Word) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_title))
            .setMessage(getString(R.string.delete_message, word.foreign))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                Store.deleteWord(word.id)
                refreshList()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun showEditDialog(word: Word) {
        val view = layoutInflater.inflate(R.layout.dialog_edit_word, null)
        val editForeign = view.findViewById<EditText>(R.id.dialogForeign)
        val editForms = view.findViewById<EditText>(R.id.dialogForms)
        val editTranslation = view.findViewById<EditText>(R.id.dialogTranslation)
        editForeign.setText(word.foreign)
        editForms.setText(word.forms)
        editTranslation.setText(word.translation)

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.edit_title))
            .setView(view)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val foreign = editForeign.text?.toString()?.trim().orEmpty()
                val forms = editForms.text?.toString()?.trim().orEmpty()
                val translation = editTranslation.text?.toString()?.trim().orEmpty()
                if (foreign.isNotEmpty() && translation.isNotEmpty()) {
                    Store.updateWord(word.id, foreign, forms, translation)
                    refreshList()
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun refreshList() {
        adapter.submit(Store.words.reversed())
        binding.textCount.text = getString(R.string.word_count, Store.words.size)
    }
}
