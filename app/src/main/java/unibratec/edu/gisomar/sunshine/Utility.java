package unibratec.edu.gisomar.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import java.text.DateFormat;
import java.util.Date;

import unibratec.edu.gisomar.sunshine.data.WheatherContract;

public class Utility {

    /**
     *
     * @param context
     * @return
     */
        public static String getLocationSetting(Context context){
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(context);

        return preferences.getString(
        context.getString(R.string.pref_location_key),
        context.getString(R.string.pref_location_default));

}

    /**
     *
     * @param context
     * @return
     */
        public static boolean isMetric(Context context){
            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(context);

            String unit = preferences.getString(
                    context.getString(R.string.pref_metric_key),
                    context.getString(R.string.pref_metric_default)
            );
            return unit.equals(context.getString(R.string.pref_metric_default));
        }

    /**
     *
     * @param temperature
     * @param isMetric
     * @return
     */
    public static String formatTemperature(double temperature,
                                           boolean isMetric) {
        double temp;
        if ( !isMetric ) {
            temp = 9 * temperature / 5 + 32;
        } else {
            temp = temperature;
        }
        return String.format("%.0f", temp);
    }
    public static String formatDate(String dateString) {
        Date date = WheatherContract.WeatherEntry.getDateFromDb(dateString);
        return DateFormat.getDateInstance().format(date);
    }
}