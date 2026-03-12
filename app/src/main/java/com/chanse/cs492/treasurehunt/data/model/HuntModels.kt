/*
 * File: HuntModels.kt
 * Author: Chanse Syres
 * Course: CS 492 Mobile Development
 * Project: Treasure Hunt
 * Description:
 * Defines the core data models used by the Treasure Hunt app.
 * A Hunt contains metadata for one hunt plus its ordered list of clues.
 * Each Clue stores the location, hint text, and completion information
 * needed to guide the user through the hunt.
 */

package com.chanse.cs492.treasurehunt.data.model

/**
 * Represents a single clue within a treasure hunt.
 *
 * @property id Unique numeric identifier for the clue.
 * @property title Short title shown to the user for the clue.
 * @property clueText Main clue text that helps the user find the location.
 * @property hintText Optional hint text used when the user needs extra help.
 * @property latitude Latitude of the target clue location.
 * @property longitude Longitude of the target clue location.
 * @property radiusMeters Allowed distance from the target location for success.
 * @property solvedInfo Text displayed after the clue has been solved.
 */
data class Clue(
    val id: Int,
    val title: String,
    val clueText: String,
    val hintText: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Double,
    val solvedInfo: String
)

/**
 * Represents one complete treasure hunt.
 *
 * @property id Unique string identifier for the hunt.
 * @property order Display/order position used when listing hunts.
 * @property title Title of the hunt shown in the UI.
 * @property intro Introductory text that explains the hunt to the user.
 * @property enabled Whether this hunt should currently be available in the app.
 * @property finalMessage Message shown after all clues in the hunt are completed.
 * @property clues Ordered list of clues that belong to this hunt.
 */
data class Hunt(
    val id: String,
    val order: Int,
    val title: String,
    val intro: String,
    val enabled: Boolean,
    val finalMessage: String,
    val clues: List<Clue>
)