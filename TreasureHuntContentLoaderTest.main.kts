#!/usr/bin/env kotlin

package com.chanse.cs492.treasurehunt

import com.chanse.cs492.treasurehunt.data.TreasureHuntContentLoader
import org.junit.Assert.assertEquals
import org.junit.Test

class TreasureHuntContentLoaderTest {

    @Test
    fun parseJsonString_readsHomeDifficultyAndGameSections() {
        val json = """
            {
              "home": {
                "title": "Treasure Hunt",
                "playButton": "Play",
                "locationPermissionTitle": "Location Permission",
                "locationPermissionMessage": "Message",
                "continueButton": "Continue",
                "cancelButton": "Cancel",
                "enableLocationTitle": "Enable Location",
                "enableLocationMessage": "Enable GPS",
                "openSettingsButton": "Open Settings"
              },
              "difficulty": {
                "hardLabel": "Hard",
                "mediumLabel": "Medium",
                "easyLabel": "Easy",
                "prompt": "Select difficulty above"
              },
              "game": {
                "easy": {
                  "description": "Easy mode",
                  "clues": ["e1", "e2"]
                },
                "medium": {
                  "description": "Medium mode",
                  "clues": ["m1", "m2"]
                },
                "hard": {
                  "description": "Hard mode",
                  "clues": ["h1", "h2"]
                }
              }
            }
        """.trimIndent()

        val content = TreasureHuntContentLoader.parseJsonString(json)

        assertEquals("Treasure Hunt", content.home.title)
        assertEquals("Select difficulty above", content.difficulty.prompt)
        assertEquals("Medium mode", content.game.medium.description)
        assertEquals(listOf("h1", "h2"), content.game.hard.clues)
    }
}
