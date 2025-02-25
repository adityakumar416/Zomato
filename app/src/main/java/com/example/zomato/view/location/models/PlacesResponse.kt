package com.test.zomato.view.location.models

data class PlacesResponse(
    val features: List<PlaceFeature>?
)

data class PlaceFeature(
    val properties: PlaceProperties
)

data class PlaceProperties(
    val name: String?,
    val formatted: String?
)
