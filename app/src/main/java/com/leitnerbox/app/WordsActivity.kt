package com.leitnerbox.app

import android.os.Bundle
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

        adapter = WordAdapter { word ->
            Store.deleteWord(word.id)
            refreshList()
        }
        binding.recyclerWords.layoutManager = LinearLayoutManager(this)
        binding.recyclerWords.adapter = adapter

        binding.buttonAdd.setOnClickListener { addWord() }

        refreshList()
    }

    private fun addWord() {
        val foreign = binding.editForeign.text?.toString()?.trim().orEmpty()
        val translation = binding.editTranslation.text?.toString()?.trim().orEmpty()
        if (foreign.isEmpty() || translation.isEmpty()) return
        Store.addWord(foreign, translation)
        binding.editForeign.text?.clear()
        binding.editTranslation.text?.clear()
        binding.editForeign.requestFocus()
        refreshList()
    }

    private fun refreshList() {
        adapter.submit(Store.words.reversed())
        binding.textCount.text = getString(R.string.word_count, Store.words.size)
    }
}
