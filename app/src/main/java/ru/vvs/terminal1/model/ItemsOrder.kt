package ru.vvs.terminal1.model

data class ItemsOrder(
    val GroupString: String,
    val Product: String,
    val Character: String,
    val Barcode: String,
    var counts: Int,
    val Price: Int
)
