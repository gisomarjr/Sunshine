package unibratec.edu.gisomar.sunshine;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import unibratec.edu.gisomar.sunshine.data.WheatherContract;


public class SettingsActivity extends ActionBarActivity {

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_activity2);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new SettingsFragment())
                    .commit();
        }
    }


    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        private boolean mBindingPreference;
        /**
         *
         * @param savedInstanceState
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

                bindPreferenceSummary(
                    findPreference(getString(R.string.pref_location_key)));
                bindPreferenceSummary(
                    findPreference(getString(R.string.pref_metric_key)));
        }
        private void bindPreferenceSummary(Preference preference){
            mBindingPreference = true;

            preference.setOnPreferenceChangeListener(this);
            Object value = PreferenceManager.getDefaultSharedPreferences(
                    getActivity()).getString(preference.getKey(), "");
            onPreferenceChange(preference, value);
            mBindingPreference = false;
        }

        /**
         *
         * @param preference
         * @param newValue
         * @return
         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            //Aula 4C
            String stringValue = newValue.toString();
            if (!mBindingPreference) {
                if (preference.getKey().equals(getString(R.string.pref_location_key))){
                    new FetchWeatherTask(getActivity()).execute(stringValue);
                } else {
                    getActivity().getContentResolver().notifyChange(
                            WheatherContract.WeatherEntry.CONTENT_URI, null);
                }
            }


            if (preference instanceof ListPreference){
                ListPreference listPreference =
                        (ListPreference)preference;
                int index = listPreference.findIndexOfValue(stringValue);
                if (index >= 0){
                    preference.setSummary(
                            listPreference.getEntries()[index]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    }
}
