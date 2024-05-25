package com.underoid.ecogreen

import android.content.Context
import android.content.SharedPreferences

object GlobalPostId {
    private const val PREFS_NAME = "EcoGreenPrefs"
    private const val POST_ID_KEY = "postId"
    private var sharedPreferences: SharedPreferences? = null
    private var postId: Int = 200


    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        postId = sharedPreferences?.getInt(POST_ID_KEY, 200) ?: 200
    }

    fun getPostID(): Int {
        return postId
    }

    fun incrementPostID() {
        postId++
        savePostID()
    }

    private fun savePostID() {
        sharedPreferences?.edit()?.putInt(POST_ID_KEY, postId)?.apply()
    }
}