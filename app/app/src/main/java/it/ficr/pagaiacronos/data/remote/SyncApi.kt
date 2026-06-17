package it.ficr.pagaiacronos.data.remote

import it.ficr.pagaiacronos.data.remote.dto.SyncPayload
import retrofit2.http.GET
import retrofit2.http.Url

interface SyncApi {
    /** Fetches the sync payload from a fully-qualified URL (user-configurable). */
    @GET
    suspend fun fetchPayload(@Url url: String): SyncPayload
}
