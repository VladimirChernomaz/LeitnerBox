package com.leitnerbox.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.LocalDate
import java.util.UUID

/**
 * Holds all words and their learning progress in memory, and persists them
 * as a small JSON file in the app's private storage. No server, no account -
 * everything lives only on this phone.
 */
object Store {

    private lateinit var file: File

    val words = mutableListOf<Word>()
    val progress = mutableListOf<Progress>()

    fun init(context: Context) {
        file = File(context.filesDir, "leitnerbox.json")
        load()
    }

    private fun today(): Long = LocalDate.now().toEpochDay()

    // ---------- Word management ----------

    fun addWord(foreign: String, translation: String) {
        val word = Word(id = UUID.randomUUID().toString(), foreign = foreign, translation = translation)
        words.add(word)
        val due = today()
        progress.add(Progress(word.id, Direction.FORWARD, Box.NEAR, 0, due))
        progress.add(Progress(word.id, Direction.REVERSE, Box.NEAR, 0, due))
        save()
    }

    fun deleteWord(wordId: String) {
        words.removeAll { it.id == wordId }
        progress.removeAll { it.wordId == wordId }
        save()
    }

    // ---------- Review session ----------

    data class ReviewItem(val word: Word, val progress: Progress)

    fun buildDueQueue(directions: Set<Direction>): MutableList<ReviewItem> {
        val due = today()
        val items = progress
            .filter { it.direction in directions }
            .filter { it.box == Box.NEAR || it.nextDueEpochDay <= due }
            .mapNotNull { p -> words.find { it.id == p.wordId }?.let { ReviewItem(it, p) } }
            .toMutableList()
        items.shuffle()
        return items
    }

      fun dueCount(directions: Set<Direction>): Int = buildDueQueue(directions).size

    fun nearCount(directions: Set<Direction>): Int =
        progress.count { it.direction in directions && it.box == Box.NEAR }

        fun farDueCount(directions: Set<Direction>): Int {
        val due = today()
        return progress.count { it.direction in directions && it.box == Box.FAR && it.nextDueEpochDay <= due }
    }

    fun farTotalCount(directions: Set<Direction>): Int =
        progress.count { it.direction in directions && it.box == Box.FAR }
    }

    fun markKnown(p: Progress) {
        val due = today()
        if (p.box == Box.NEAR) {
            p.box = Box.FAR
            p.farStage = 0
            p.nextDueEpochDay = due + Progress.FAR_INTERVAL_DAYS[0]
        } else {
            p.farStage = (p.farStage + 1).coerceAtMost(Progress.FAR_INTERVAL_DAYS.size - 1)
            p.nextDueEpochDay = due + Progress.FAR_INTERVAL_DAYS[p.farStage]
        }
        save()
    }

    fun markForgot(p: Progress) {
        p.box = Box.NEAR
        p.farStage = 0
        p.nextDueEpochDay = today()
        save()
    }

    // ---------- Persistence (plain JSON file, no extra libraries needed) ----------

    private fun save() {
        val root = JSONObject()

        val wordsArray = JSONArray()
        words.forEach { w ->
            wordsArray.put(JSONObject().apply {
                put("id", w.id)
                put("foreign", w.foreign)
                put("translation", w.translation)
            })
        }
        root.put("words", wordsArray)

        val progressArray = JSONArray()
        progress.forEach { p ->
            progressArray.put(JSONObject().apply {
                put("wordId", p.wordId)
                put("direction", p.direction.name)
                put("box", p.box.name)
                put("farStage", p.farStage)
                put("nextDueEpochDay", p.nextDueEpochDay)
            })
        }
        root.put("progress", progressArray)

        file.writeText(root.toString())
    }

    private fun load() {
        words.clear()
        progress.clear()
        if (!file.exists()) return

        val root = try {
            JSONObject(file.readText())
        } catch (_: Exception) {
            return
        }

        val wordsArray = root.optJSONArray("words") ?: JSONArray()
        for (i in 0 until wordsArray.length()) {
            val o = wordsArray.getJSONObject(i)
            words.add(Word(o.getString("id"), o.getString("foreign"), o.getString("translation")))
        }

        val progressArray = root.optJSONArray("progress") ?: JSONArray()
        for (i in 0 until progressArray.length()) {
            val o = progressArray.getJSONObject(i)
            progress.add(
                Progress(
                    wordId = o.getString("wordId"),
                    direction = Direction.valueOf(o.getString("direction")),
                    box = Box.valueOf(o.getString("box")),
                    farStage = o.getInt("farStage"),
                    nextDueEpochDay = o.getLong("nextDueEpochDay")
                )
            )
        }
    }
}
