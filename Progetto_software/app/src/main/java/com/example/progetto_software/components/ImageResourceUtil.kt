package com.example.progetto_software.components

import android.content.Context

object ImageResourceUtil {
    fun getDrawableResIdByName(context: Context, resName: String?): Int {
        if (resName.isNullOrEmpty()) return 0
        return context.resources.getIdentifier(resName, "drawable", context.packageName)
    }
}