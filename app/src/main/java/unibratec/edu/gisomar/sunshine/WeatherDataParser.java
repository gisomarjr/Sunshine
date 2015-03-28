package unibratec.edu.gisomar.sunshine;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import unibratec.edu.gisomar.sunshine.data.WheatherContract;

/**
 * Created by gisomar on 25/02/2015.
 */
public class WeatherDataParser {

    /**
     *
     * @param time
     * @return
     */
    private String getReadableDateString(long time) {
        //conversao de valores - Horas em Dias
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
        return format.format(date).toString();
    }


    /**
     *
     * @param high
     * @param low
     * @return
     */
    public String formatHighLows(double high, double low) {
//temperatura max e min - arredondar
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /**
     *
     * @param context
     * @param forecastJsonStr
     * @param numDays
     * @return
     * @throws JSONException
     */
    public String[] getWeatherDataFromJson(Context context, String forecastJsonStr, int numDays)
            throws JSONException {


        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DATETIME = "dt";
        final String OWM_DESCRIPTION = "main";
        final String OWM_WEATHER_ID = "id";
        final String OWM_PRESSURE = "pressure";
        final String OWM_HUMIDITY = "humidity";
        final String OWM_WIND_SPEED = "speed";
        final String OWM_WIND_DIRECTION = "deg";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        JSONObject cityJson = forecastJson.getJSONObject("city");
        String cityName = cityJson.getString("name");
        JSONObject locationJson = cityJson.getJSONObject("coord");
        double lat = locationJson.getDouble("lat");
        double lng = locationJson.getDouble("lon");
        long idLocation = addLocation(context, Utility.getLocationSetting(context), cityName,lat, lng);


        List<ContentValues> valuesToBeInserted = new ArrayList<>();

        String[] resultStrs = new String[numDays];
        for (int i = 0; i < weatherArray.length(); i++) {

            String day;
            String description;
            String highAndLow;


            JSONObject dayForecast = weatherArray.getJSONObject(i);


            long dateTime = dayForecast.getLong(OWM_DATETIME);
            day = getReadableDateString(dateTime);


            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);

            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high, low);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;


            int weatherId = weatherObject.getInt(OWM_WEATHER_ID);
            double pressure = dayForecast.getDouble(OWM_PRESSURE);
            double speed = dayForecast.getDouble(OWM_WIND_SPEED);
            double deg = dayForecast.getDouble(OWM_WIND_DIRECTION);
            int humidity = dayForecast.getInt(OWM_HUMIDITY);

            ContentValues values = new ContentValues();
            values.put(WheatherContract.WeatherEntry.COLUMN_LOC_KEY, idLocation);
            values.put(WheatherContract.WeatherEntry.COLUMN_DATETEXT,
                    WheatherContract.WeatherEntry.getDbDateString(new Date(dateTime * 1000)));
            values.put(WheatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);
            values.put(WheatherContract.WeatherEntry.COLUMN_SHORT_DESC, description);
            values.put(WheatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
            values.put(WheatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
            values.put(WheatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
            values.put(WheatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
            values.put(WheatherContract.WeatherEntry.COLUMN_WIND_SPEED, speed);
            values.put(WheatherContract.WeatherEntry.COLUMN_DEGREES, deg);
            valuesToBeInserted.add(values);

        }

        if (valuesToBeInserted.size() > 0) {
            ContentValues[] arrayOfValues =
                    new ContentValues[valuesToBeInserted.size()];
            valuesToBeInserted.toArray(arrayOfValues);
            context.getContentResolver().bulkInsert(
                    WheatherContract.WeatherEntry.CONTENT_URI, arrayOfValues);
        }

        return resultStrs;
    }

    private long addLocation(Context context, String locationSetting,
                             String location, double lat, double lng) {
        Cursor cursor = context.getContentResolver().query(
                WheatherContract.LocationEntry.CONTENT_URI,
                new String[]{WheatherContract.LocationEntry._ID},
                WheatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[]{locationSetting},
                null);
        if (cursor.moveToFirst()) {
            return cursor.getLong(
                    cursor.getColumnIndex(WheatherContract.LocationEntry._ID));
        } else {
            ContentValues values = new ContentValues();
            values.put(WheatherContract.LocationEntry.COLUMN_CITY_NAME, location);
            values.put(WheatherContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
            values.put(WheatherContract.LocationEntry.COLUMN_COORD_LAT, lat);
            values.put(WheatherContract.LocationEntry.COLUMN_COORD_LONG, lng);

            Uri uri = context.getContentResolver().insert(
                    WheatherContract.LocationEntry.CONTENT_URI, values);
            return ContentUris.parseId(uri);

        }


    }
}
