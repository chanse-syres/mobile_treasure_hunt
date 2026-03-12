/*
 * File: HuntRepository.kt
 * Author: Chanse Syres
 * Course: CS 492 Mobile Development
 * Project: Treasure Hunt
 * Description:
 * Provides access to hunt data stored in the app's raw JSON resource file.
 * This repository reads and parses the hunts data, converts it into Hunt and
 * Clue model objects, caches the results in memory, and exposes helper methods
 * for retrieving all hunts or a specific hunt by id.
 */

package com.chanse.cs492.treasurehunt.data

import android.content.Context
import com.chanse.cs492.treasurehunt.R
import com.chanse.cs492.treasurehunt.data.model.Clue
import com.chanse.cs492.treasurehunt.data.model.Hunt
import org.json.JSONObject

/**
 * Repository responsible for loading treasure hunt data from the raw JSON file.
 *
 * @property context Application context used to access app resources.
 */
class HuntRepository(private val context: Context) {

    /**
     * In-memory cache of parsed hunts so the JSON file does not need to be
     * re-read every time data is requested.
     */
    private var cachedHunts: List<Hunt>? = null

    /**
     * Loads all hunts from the raw JSON resource.
     *
     * If hunts were previously loaded, the cached list is returned instead of
     * parsing the file again. Parsed hunts are sorted by their order property
     * before being cached and returned.
     *
     * @return A list of Hunt objects sorted by display order.
     */
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

    /**
     * Retrieves a single hunt by its unique id.
     *
     * This function uses the loaded/cached hunt list and returns the first hunt
     * whose id matches the provided value.
     *
     * @param huntId The id of the hunt to search for.
     * @return The matching Hunt if found, or null if no hunt matches the id.
     */
    fun getHuntById(huntId: String): Hunt? {
        return loadHunts().firstOrNull { it.id == huntId }
    }
}