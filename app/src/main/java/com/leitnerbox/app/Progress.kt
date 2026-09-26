package com.leitnerbox.app

enum class Direction { FORWARD, REVERSE }

enum class Box { NEAR, FAR }

/**
 * Tracks how well a single word is known in ONE direction (foreign->native
 * or native->foreign). Each word has two independent Progress entries.
 */
data class Progress(
    val wordId: String,
    val direction: Direction,
    var box: Box,
    var farStage: Int,      // index into FAR_INTERVAL_DAYS, only meaningful when box == FAR
    var nextDueEpochDay: Long
) {
    companion object {
        // How many days to wait before the next review, once a word is in
        // the FAR box: first a week, then two weeks, then settles at a month.
        val FAR_INTERVAL_DAYS = intArrayOf(7, 14, 30)
    }
}
