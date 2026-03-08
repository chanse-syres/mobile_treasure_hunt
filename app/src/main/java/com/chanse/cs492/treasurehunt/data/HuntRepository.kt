package com.chanse.cs492.treasurehunt.data

import android.content.Context
import com.chanse.cs492.treasurehunt.R
import com.chanse.cs492.treasurehunt.data.model.Clue
import com.chanse.cs492.treasurehunt.data.model.Hunt
import org.json.JSONObject

class HuntRepository(private val context: Context) {

    private var cachedHunts: List<Hunt>? = null

    fun loadHunts(): List<Hunt> {
        cachedHunts?.let { return it }

        val jsonText = context.resources
            .openRawResource(R.raw.hunts)
            .bufferedReader()
            .use { it.readText() }

        val root = JSONObject(jsonText)
        val huntsArray = root.getJSONArray("hunts")

        val hunts = mutableListOf<Hunt>()

        for (i in 0 until huntsArray.length()) {
            val huntJson = huntsArray.getJSONObject(i)
            val cluesArray = huntJson.getJSONArray("clues")
            val clues = mutableListOf<Clue>()

            for (j in 0 until cluesArray.length()) {
                val clueJson = cluesArray.getJSONObject(j)
                clues += Clue(
                    id = clueJson.getInt("id"),
                    title = clueJson.getString("title"),
                    clueText = clueJson.getString("clueText"),
                    hintText = clueJson.getString("hintText"),
                    latitude = clueJson.getDouble("latitude"),
                    longitude = clueJson.getDouble("longitude"),
                    radiusMeters = clueJson.getDouble("radiusMeters"),
                    solvedInfo = clueJson.getString("solvedInfo")
                )
            }

            hunts += Hunt(
                id = huntJson.getString("id"),
                order = huntJson.getInt("order"),
                title = huntJson.getString("title"),
                intro = huntJson.getString("intro"),
                enabled = huntJson.getBoolean("enabled"),
                finalMessage = huntJson.getString("finalMessage"),
                clues = clues
            )
        }

        cachedHunts = hunts.sortedBy { it.order }
        return cachedHunts!!
    }

    fun getHuntById(huntId: String): Hunt? {
        return loadHunts().firstOrNull { it.id == huntId }
    }
}