package com.leitnerbox.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.leitnerbox.app.databinding.ActivityStatsBinding

class StatsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Store.init(applicationContext)

        binding.buttonBack.setOnClickListener { finish() }
        binding.editFarLimit.setText(Store.farLimit.toString())

        binding.buttonSaveLimit.setOnClickListener {
            val value = binding.editFarLimit.text?.toString()?.toIntOrNull()
            if (value != null && value >= 0) {
                Store.updateFarLimit(value)
                android.widget.Toast.makeText(this, getString(R.string.save), android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        updateStats()
    }

    private fun updateStats() {
        binding.textTotal.text = getString(R.string.stats_total, Store.words.size)

        val fwd = setOf(Direction.FORWARD)
        binding.textForwardStats.text = getString(
            R.string.stats_line,
            Store.nearCount(fwd),
            Store.farTotalCount(fwd),
            Store.farDueCount(fwd)
        )

        val rev = setOf(Direction.REVERSE)
        binding.textReverseStats.text = getString(
            R.string.stats_line,
            Store.nearCount(rev),
            Store.farTotalCount(rev),
            Store.farDueCount(rev)
        )
    }
}
