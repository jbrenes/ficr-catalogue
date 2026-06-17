package it.ficr.pagaiacronos.ui.navigation

sealed class Screen(val route: String) {
    data object Results : Screen("results")
    data object Sync : Screen("sync")
    data object Settings : Screen("settings")
    data object About : Screen("about")
    data object AthleteProfile : Screen("athlete/{athleteId}") {
        fun withId(id: Long) = "athlete/$id"
    }
}
