package com.leitnerbox.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.leitnerbox.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Store.init(applicationContext)

        binding.buttonForward.setOnClickListener { startReview(setOf(Direction.FORWARD)) }
        binding.buttonReverse.setOnClickListener { startReview(setOf(Direction.REVERSE)) }
        binding.buttonMixed.setOnClickListener { startReview(setOf(Direction.FORWARD, Direction.REVERSE)) }
        binding.buttonWords.setOnClickListener {
            startActivity(Intent(this, WordsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        updateCounts()
    }

        private fun updateCounts() {
        val fwd = setOf(Direction.FORWARD)
        val rev = setOf(Direction.REVERSE)
        binding.textSummary.text = getString(
            R.string.summary,
            Store.dueCount(fwd), Store.nearCount(fwd), Store.farDueCount(fwd),
            Store.dueCount(rev), Store.nearCount(rev), Store.farDueCount(rev)
        )
        binding.textWordCount.text = getString(R.string.word_count_footer, Store.words.size)
    }

    private fun startReview(directions: Set<Direction>) {
        val intent = Intent(this, ReviewActivity::class.java)
        intent.putStringArrayListExtra(
            ReviewActivity.EXTRA_DIRECTIONS,
            ArrayList(directions.map { it.name })
        )
        startActivity(intent)
    }
}
