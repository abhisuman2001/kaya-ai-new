package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val firestore: FirebaseFirestore?
        get() = try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w("FirestoreRepository", "FirebaseFirestore instance not available: ${e.message}")
            null
        }

    private val auth: FirebaseAuth?
        get() = try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w("FirestoreRepository", "FirebaseAuth instance not available: ${e.message}")
            null
        }

    suspend fun saveUserProfileToFirestore(profile: UserProfile): Boolean {
        val db = firestore ?: return false
        val docId = if (profile.id.isNotBlank()) profile.id else profile.email.replace(".", "_")
        if (docId.isBlank()) return false

        return try {
            val userMap = hashMapOf<String, Any>(
                "id" to profile.id,
                "name" to profile.name,
                "email" to profile.email,
                "role" to profile.role.name,
                "jobTitle" to profile.jobTitle,
                "company" to profile.company,
                "siteLocation" to profile.siteLocation,
                "connectedGlassesModel" to profile.connectedGlassesModel,
                "glassesBattery" to profile.glassesBattery,
                "glassesStatus" to profile.glassesStatus,
                "language" to profile.language,
                "theme" to profile.theme,
                "isGoogleAuth" to profile.isGoogleAuth,
                "isBiometricEnabled" to profile.isBiometricEnabled,
                "isTelemetryShared" to profile.isTelemetryShared,
                "isLocationTrackingEnabled" to profile.isLocationTrackingEnabled,
                "avatarUrl" to profile.avatarUrl,
                "updatedAt" to System.currentTimeMillis()
            )

            db.collection("users").document(docId)
                .set(userMap, SetOptions.merge())
                .await()
            Log.d("FirestoreRepository", "Successfully saved profile to Firestore for user: $docId")
            true
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error saving user profile to Firestore: ${e.message}")
            false
        }
    }

    suspend fun fetchUserProfileFromFirestore(userId: String, email: String = ""): UserProfile? {
        val db = firestore ?: return null
        return try {
            val docRef = if (userId.isNotBlank()) {
                db.collection("users").document(userId).get().await()
            } else if (email.isNotBlank()) {
                val querySnapshot = db.collection("users")
                    .whereEqualTo("email", email)
                    .limit(1)
                    .get()
                    .await()
                querySnapshot.documents.firstOrNull()
            } else {
                null
            }

            if (docRef != null && docRef.exists()) {
                val roleStr = docRef.getString("role") ?: "WORKER"
                val userRole = if (roleStr.contains("SUPERVISOR", ignoreCase = true) || roleStr.contains("OFFICER", ignoreCase = true)) {
                    UserRole.SUPERVISOR
                } else {
                    UserRole.WORKER
                }

                UserProfile(
                    id = docRef.getString("id") ?: userId,
                    name = docRef.getString("name") ?: "",
                    email = docRef.getString("email") ?: email,
                    role = userRole,
                    jobTitle = docRef.getString("jobTitle") ?: "Site Engineer & Inspector",
                    company = docRef.getString("company") ?: "BuildTech Construction",
                    siteLocation = docRef.getString("siteLocation") ?: "Metro Tower Construction — Active Site",
                    connectedGlassesModel = docRef.getString("connectedGlassesModel") ?: "Ray-Ban Meta Smart Glasses (Gen 2)",
                    glassesBattery = (docRef.getLong("glassesBattery") ?: 100L).toInt(),
                    glassesStatus = docRef.getString("glassesStatus") ?: "Connected & Active",
                    language = docRef.getString("language") ?: "English (US)",
                    theme = docRef.getString("theme") ?: "Dark Mode",
                    isGoogleAuth = docRef.getBoolean("isGoogleAuth") ?: false,
                    isBiometricEnabled = docRef.getBoolean("isBiometricEnabled") ?: true,
                    isTelemetryShared = docRef.getBoolean("isTelemetryShared") ?: true,
                    isLocationTrackingEnabled = docRef.getBoolean("isLocationTrackingEnabled") ?: true,
                    avatarUrl = docRef.getString("avatarUrl") ?: ""
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error fetching profile from Firestore: ${e.message}")
            null
        }
    }

    suspend fun firebaseSignInWithGoogleToken(idToken: String): UserProfile? {
        val firebaseAuth = auth ?: return null
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: return null

            val profile = UserProfile(
                id = firebaseUser.uid,
                name = firebaseUser.displayName ?: "Google User",
                email = firebaseUser.email ?: "user@gmail.com",
                role = UserRole.SUPERVISOR,
                isGoogleAuth = true,
                avatarUrl = firebaseUser.photoUrl?.toString() ?: ""
            )

            // Persist user details to Firestore database
            saveUserProfileToFirestore(profile)
            profile
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Firebase Google sign in failed: ${e.message}")
            null
        }
    }
}
