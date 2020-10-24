package com.mbpatel.weatherinfo

import android.app.Application
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class App : Application() {

    companion object {
        private lateinit var firebaseAuth: FirebaseAuth
        fun getFirebaseAuth(): FirebaseAuth {
            return firebaseAuth
        }
    }

    override fun onCreate() {
        super.onCreate()
        firebaseAuth = Firebase.auth
    }
}