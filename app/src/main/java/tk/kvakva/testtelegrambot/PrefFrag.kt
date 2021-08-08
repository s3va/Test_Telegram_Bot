package tk.kvakva.testtelegrambot

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class PrefFrag: PreferenceFragmentCompat() {
    /**
     * Called during [.onCreate] to supply the preferences for this fragment.
     * Subclasses are expected to call [.setPreferenceScreen] either
     * directly or via helper methods such as [.addPreferencesFromResource].
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     * this is the state.
     * @param rootKey            If non-null, this preference fragment should be rooted at the
     * PreferenceScreen] with this key.
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs,rootKey)
    }


}