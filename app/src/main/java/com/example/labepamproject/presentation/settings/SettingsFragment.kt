package com.example.labepamproject.presentation.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.labepamproject.R
import com.example.labepamproject.presentation.setFragmentTitle

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        setFragmentTitle(activity, getString(R.string.settings))
    }
}