package it.ficr.pagaiacronos.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import it.ficr.pagaiacronos.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val KEY_SYNC_URL = stringPreferencesKey("sync_url")
    private val KEY_AUTO_SYNC = booleanPreferencesKey("auto_sync")
    private val KEY_DONATION_URL = stringPreferencesKey("donation_url")
    private val KEY_PERSONAL_ATHLETE_ID = longPreferencesKey("personal_athlete_id")

    val syncUrlFlow: Flow<String> = context.dataStore.data
        .map { it[KEY_SYNC_URL] ?: Constants.DEFAULT_SYNC_URL }

    val autoSyncFlow: Flow<Boolean> = context.dataStore.data
        .map { it[KEY_AUTO_SYNC] ?: false }

    val donationUrlFlow: Flow<String> = context.dataStore.data
        .map { it[KEY_DONATION_URL] ?: Constants.DEFAULT_DONATION_URL }

    val personalAthleteIdFlow: Flow<Long?> = context.dataStore.data
        .map { it[KEY_PERSONAL_ATHLETE_ID] }

    suspend fun setSyncUrl(url: String) =
        context.dataStore.edit { it[KEY_SYNC_URL] = url }

    suspend fun setAutoSync(enabled: Boolean) =
        context.dataStore.edit { it[KEY_AUTO_SYNC] = enabled }

    suspend fun setDonationUrl(url: String) =
        context.dataStore.edit { it[KEY_DONATION_URL] = url }

    suspend fun setPersonalAthleteId(athleteId: Long?) =
        context.dataStore.edit {
            if (athleteId == null) it.remove(KEY_PERSONAL_ATHLETE_ID) else it[KEY_PERSONAL_ATHLETE_ID] = athleteId
        }

    suspend fun getSyncUrl(): String = syncUrlFlow.first()
    suspend fun getAutoSync(): Boolean = autoSyncFlow.first()
    suspend fun getPersonalAthleteId(): Long? = personalAthleteIdFlow.first()
}
