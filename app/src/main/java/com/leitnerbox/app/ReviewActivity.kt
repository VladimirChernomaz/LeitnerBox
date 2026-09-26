package com.leitnerbox.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.leitnerbox.app.databinding.ActivityReviewBinding

class ReviewActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DIRECTIONS = "directions"
    }

    private lateinit var binding: ActivityReviewBinding
    private lateinit var queue: MutableList<Store.ReviewItem>
    private var revealed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Store.init(applicationContext)

        val directionNames = intent.getStringArrayListExtra(EXTRA_DIRECTIONS) ?: arrayListOf()
        val directions = directionNames.map { Direction.valueOf(it) }.toSet()
        queue = Store.buildDueQueue(directions)

        binding.buttonReveal.setOnClickListener { reveal() }
        binding.buttonKnown.setOnClickListener { answer(known = true) }
        binding.buttonForgot.setOnClickListener { answer(known = false) }

        showCurrent()
    }

    private fun showCurrent() {
        if (queue.isEmpty()) {
            binding.textPrompt.text = getString(R.string.session_done)
            binding.textAnswer.text = ""
            binding.textAnswer.visibility = android.view.View.GONE
            binding.buttonReveal.visibility = android.view.View.GONE
            binding.buttonKnown.visibility = android.view.View.GONE
            binding.buttonForgot.visibility = android.view.View.GONE
            binding.textCounter.text = ""
            return
        }

        val item = queue.first()
        val prompt = if (item.progress.direction == Direction.FORWARD) item.word.foreign else item.word.translation
        binding.textPrompt.text = prompt
        binding.textCounter.text = getString(R.string.counter, queue.size)

        revealed = false
        binding.textAnswer.visibility = android.view.View.INVISIBLE
        binding.buttonReveal.visibility = android.view.View.VISIBLE
        binding.buttonKnown.visibility = android.view.View.GONE
        binding.buttonForgot.visibility = android.view.View.GONE
    }

    private fun reveal() {
        if (queue.isEmpty()) return
        val item = queue.first()
        val answer = if (item.progress.direction == Direction.FORWARD) item.word.translation else item.word.foreign
        binding.textAnswer.text = answer
        binding.textAnswer.visibility = android.view.View.VISIBLE
        binding.buttonReveal.visibility = android.view.View.GONE
        binding.buttonKnown.visibility = android.view.View.VISIBLE
        binding.buttonForgot.visibility = android.view.View.VISIBLE
        revealed = true
    }

    private fun answer(known: Boolean) {
        if (queue.isEmpty() || !revealed) return
        val item = queue.removeAt(0)
        if (known) {
            Store.markKnown(item.progress)
        } else {
            Store.markForgot(item.progress)
            // Put it back a bit later in today's session so it gets repeated.
            val insertAt = minOf(3, queue.size)
            queue.add(insertAt, item)
        }
        showCurrent()
    }
}
