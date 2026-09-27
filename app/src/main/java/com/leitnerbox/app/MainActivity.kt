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
        binding.buttonWords.setOnClickListener {
            startActivity(Intent(this, WordsActivity::class.java))
        }
        binding.buttonStats.setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java))
        }
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
