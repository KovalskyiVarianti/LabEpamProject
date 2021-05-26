package com.example.labepamproject.presentation.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.example.labepamproject.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        setAppName()
    }

    private fun setAppName() {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.settings)
    }
}