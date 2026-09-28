package com.leitnerbox.app

import android.os.Bundle
import android.view.View
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
        binding.buttonBack.setOnClickListener { finish() }

        showCurrent()
    }

    private fun promptFor(item: Store.ReviewItem): String =
        if (item.progress.direction == Direction.FORWARD) item.word.foreign else item.word.translation

    private fun answerFor(item: Store.ReviewItem): String {
        val main = if (item.progress.direction == Direction.FORWARD) item.word.translation else item.word.foreign
        return if (item.word.forms.isBlank()) main else main + "\n\n" + item.word.forms
    }

    private fun showCurrent() {
        if (queue.isEmpty()) {
            binding.textPrompt.text = getString(R.string.session_done)
            binding.textAnswer.text = ""
            binding.textAnswer.visibility = View.GONE
            binding.buttonReveal.visibility = View.GONE
            binding.buttonKnown.visibility = View.GONE
            binding.buttonForgot.visibility = View.GONE
            binding.textCounter.text = ""
            return
        }

        val item = queue.first()
        binding.textPrompt.text = promptFor(item)
        binding.textCounter.text = getString(R.string.counter, queue.size)

        revealed = false
        binding.textAnswer.visibility = View.INVISIBLE
        binding.buttonReveal.visibility = View.VISIBLE
        binding.buttonKnown.visibility = View.GONE
        binding.buttonForgot.visibility = View.GONE
    }

    private fun reveal() {
        if (queue.isEmpty()) return
        binding.textAnswer.text = answerFor(queue.first())
        binding.textAnswer.visibility = View.VISIBLE
        binding.buttonReveal.visibility = View.GONE
        binding.buttonKnown.visibility = View.VISIBLE
        binding.buttonForgot.visibility = View.VISIBLE
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
