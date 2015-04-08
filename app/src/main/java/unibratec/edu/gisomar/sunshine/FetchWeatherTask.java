package unibratec.edu.gisomar.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


class FetchWeatherTask extends AsyncTask<String, Void, String[]> {


    Context mcontext;


    FetchWeatherTask(Context mcontext) {
        this.mcontext = mcontext;

    }

    /**
     *
     * @param params
     * @return
     */
    @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            String forecastJsonStr = null;

            final String FORECAST_BASE_URL =
                    "http://api.openweathermap.org/data/2.5/forecast/daily?";
            final String QUERY_PARAM = "q";
            final String FORMAT_PARAM = "mode";
            final String UNITS_PARAM = "units";
            final String DAYS_PARAM = "cnt";


        String format = "json";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mcontext);
            String unit = prefs.getString(
                    mcontext.getString(R.string.pref_metric_key),
                    mcontext.getString(R.string.pref_metric_default));

            int numDays = 14;

            String[] forecasts = null;


            try {

                Uri uri = Uri.parse(FORECAST_BASE_URL)
                        .buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, unit)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .build();
                URL url = new URL(uri.toString());



                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {

                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {

                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
                Log.d("Sunshine", forecastJsonStr);
                forecasts = new WeatherDataParser().getWeatherDataFromJson(mcontext,forecastJsonStr, numDays);


            } catch (Exception e) {
                Log.e("ForecastFragment", "Error ", e);

                forecastJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("ForecastFragment", "Error closing stream", e);
                    }
                }


            }

            return forecasts;
        }


    }
