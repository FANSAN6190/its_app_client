package com.example.civiceye.ui.home

import java.io.Serializable

data class WhistleData(
    val whistleId: String,
    val category: String,
    val subcategory: String,
    val latitude: String,
    val longitude: String,
    val description: String,
    val userRating: String,
    val timestamp: String
) : Serializable