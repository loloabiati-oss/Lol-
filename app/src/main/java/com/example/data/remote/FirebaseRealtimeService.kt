package com.example.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class FirebaseRealtimeService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    // Users can override with their own Firebase Realtime DB URL or Project ID in settings
    var customFirebaseUrl: String = "https://efootball-tournament-default-rtdb.firebaseio.com"

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun syncTournamentToCloud(tournamentJson: String, tournamentId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = "$customFirebaseUrl/tournaments/$tournamentId.json"
            val requestBody = tournamentJson.toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .put(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                Log.d("FirebaseRealtime", "Tournament sync status: ${response.code}")
                return@withContext response.isSuccessful
            }
        } catch (e: Exception) {
            Log.w("FirebaseRealtime", "Cloud sync offline or skipped: ${e.message}")
            return@withContext false
        }
    }

    suspend fun syncMatchToCloud(matchJson: String, tournamentId: String, matchId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = "$customFirebaseUrl/matches/$tournamentId/$matchId.json"
            val requestBody = matchJson.toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .put(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                Log.d("FirebaseRealtime", "Match sync status: ${response.code}")
                return@withContext response.isSuccessful
            }
        } catch (e: Exception) {
            Log.w("FirebaseRealtime", "Match sync offline or skipped: ${e.message}")
            return@withContext false
        }
    }

    suspend fun sendChatMessageToCloud(chatJson: String, tournamentId: String, messageId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = "$customFirebaseUrl/chats/$tournamentId/$messageId.json"
            val requestBody = chatJson.toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .put(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                return@withContext response.isSuccessful
            }
        } catch (e: Exception) {
            Log.w("FirebaseRealtime", "Chat sync offline or skipped: ${e.message}")
            return@withContext false
        }
    }

    suspend fun syncUserToCloud(userJson: String, userId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = "$customFirebaseUrl/users/$userId.json"
            val requestBody = userJson.toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .put(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                return@withContext response.isSuccessful
            }
        } catch (e: Exception) {
            Log.w("FirebaseRealtime", "User sync offline or skipped: ${e.message}")
            return@withContext false
        }
    }

    suspend fun testConnection(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = "$customFirebaseUrl/.json?shallow=true"
            val request = Request.Builder().url(url).get().build()
            client.newCall(request).execute().use { response ->
                return@withContext response.isSuccessful || response.code in 200..404
            }
        } catch (e: Exception) {
            return@withContext false
        }
    }
}
