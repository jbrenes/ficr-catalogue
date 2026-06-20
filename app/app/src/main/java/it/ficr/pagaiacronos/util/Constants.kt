package it.ficr.pagaiacronos.util

object Constants {
    const val DEFAULT_SYNC_URL =
        "https://raw.githubusercontent.com/jbrenes/ficr-catalogue/refs/heads/master/data/ficrdb.json"
    const val DEFAULT_DONATION_URL = "https://ko-fi.com"
    const val DATABASE_NAME = "pagaiacronos.db"
    const val PAGE_SIZE = 30
    val BOAT_CLASSES = listOf("K1", "K2", "K4")
    val DISTANCES = listOf(200, 500, 1000, 5000, 20000)
}
