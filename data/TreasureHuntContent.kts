package com.chanse.cs492.treasurehunt.data

import android.content.Context
import androidx.annotation.RawRes
import com.chanse.cs492.treasurehunt.R
import org.json.JSONArray
import org.json.JSONObject

/**
 * All content for the treasure hunt app is loads from a single resource file.
 */
data class TreasureHuntContent(
    val home: HomeContent,
    val difficulty: DifficultyContent,
    val game: GameContent
)

data class HomeContent(
    val title: String,
    val playButton: String,
    val locationPermissionTitle: String,
    val locationPermissionMessage: String,
    val continueButton: String,
    val cancelButton: String,
    val enableLocationTitle: String,
    val enableLocationMessage: String,
    val openSettingsButton: String
)

data class DifficultyContent(
    val hardLabel: String,
    val mediumLabel: String,
    val easyLabel: String,
    val prompt: String
)

data class GameContent(
    val easy: DifficultyGameContent,
    val medium: DifficultyGameContent,
    val hard: DifficultyGameContent
)

data class DifficultyGameContent(
    val description: String,
    val clues: List<String>
)

object TreasureHuntContentLoader {
    fun load(
        context: Context,
        @RawRes resourceId: Int = R.raw.treasure_hunt_content
    ): TreasureHuntContent {
        val rawJson = context.resources.openRawResource(resourceId)
            .bufferedReader()
            .use { it.readText() }

        return parseJsonString(rawJson)
    }

    fun parseJsonString(rawJson: String): TreasureHuntContent = parse(JSONObject(rawJson))

    private fun parse(json: JSONObject): TreasureHuntContent {
        val homeJson = json.getJSONObject("home")
        val difficultyJson = json.getJSONObject("difficulty")
        val gameJson = json.getJSONObject("game")

        return TreasureHuntContent(
            home = HomeContent(
                title = homeJson.getString("title"),
                playButton = homeJson.getString("playButton"),
                locationPermissionTitle = homeJson.getString("locationPermissionTitle"),
                locationPermissionMessage = homeJson.getString("locationPermissionMessage"),
                continueButton = homeJson.getString("continueButton"),
                cancelButton = homeJson.getString("cancelButton"),
                enableLocationTitle = homeJson.getString("enableLocationTitle"),
                enableLocationMessage = homeJson.getString("enableLocationMessage"),
                openSettingsButton = homeJson.getString("openSettingsButton")
            ),
            difficulty = DifficultyContent(
                hardLabel = difficultyJson.getString("hardLabel"),
                mediumLabel = difficultyJson.getString("mediumLabel"),
                easyLabel = difficultyJson.getString("easyLabel"),
                prompt = difficultyJson.getString("prompt")
            ),
            game = GameContent(
                easy = parseDifficultyGameContent(gameJson.getJSONObject("easy")),
                medium = parseDifficultyGameContent(gameJson.getJSONObject("medium")),
                hard = parseDifficultyGameContent(gameJson.getJSONObject("hard"))
            )
        )
    }

    private fun parseDifficultyGameContent(json: JSONObject): DifficultyGameContent {
        val cluesArray = json.getJSONArray("clues")
        return DifficultyGameContent(
            description = json.getString("description"),
            clues = cluesArray.toStringList()
        )
    }

    private fun JSONArray.toStringList(): List<String> =
        List(length()) { index -> getString(index) }
}
