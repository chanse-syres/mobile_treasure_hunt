package com.chanse.cs492.treasurehunt.data.model

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

data class Hunt(
    val id: String,
    val order: Int,
    val title: String,
    val intro: String,
    val enabled: Boolean,
    val finalMessage: String,
    val clues: List<Clue>
)