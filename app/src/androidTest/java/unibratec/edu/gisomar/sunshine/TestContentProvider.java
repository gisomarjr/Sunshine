package unibratec.edu.gisomar.sunshine;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import unibratec.edu.gisomar.sunshine.data.WeatherDbHelper;
import unibratec.edu.gisomar.sunshine.data.WheatherContract;

/**
 * Created by gisomar on 10/03/15.
 */
public class TestContentProvider extends AndroidTestCase {

    //Realizar teste no Content Provider
    //Aula 4b
    public void testGetType(){
        //Wheather
        String type = getContext().getContentResolver().getType(
                WheatherContract.WeatherEntry.CONTENT_URI);
        assertEquals(type, WheatherContract.WeatherEntry.CONTENT_TYPE);
        //Location
        type = getContext().getContentResolver().getType(
                WheatherContract.LocationEntry.CONTENT_URI);
        assertEquals(type, WheatherContract.LocationEntry.CONTENT_TYPE);
        //Where Location
        type = getContext().getContentResolver().getType(
                WheatherContract.WeatherEntry.buildWeatherLocation("Recife"));
        assertEquals(type, WheatherContract.WeatherEntry.CONTENT_TYPE);
        //Where Location - Date
        type = getContext().getContentResolver().getType(
                WheatherContract.WeatherEntry.buildWeatherLocationWithDate("Recife","2222515"));
        assertEquals(type, WheatherContract.WeatherEntry.CONTENT_ITEM_TYPE);


        // Fazer para as 3 outras URIs...
    }


    public void testCrud(){

        WeatherDbHelper helper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //Coluna - valor

        String inputCityName = "Recife";
        String inputLocationSetting = "recifes";
        double inputLat = -8.0464492;
        double inputLng = -34.9324882;

        values.put(WheatherContract.LocationEntry.COLUMN_CITY_NAME, inputCityName);
        values.put(WheatherContract.LocationEntry.COLUMN_LOCATION_SETTING, inputLocationSetting);
        values.put(WheatherContract.LocationEntry.COLUMN_COORD_LAT,inputLat);
        values.put(WheatherContract.LocationEntry.COLUMN_COORD_LONG, inputLng);
        //se der erro é -1
        long id = db.insert(WheatherContract.LocationEntry.TABLE_NAME, null, values);

        assertTrue(id != -1);

       // db.close();


        //Busca de Registro

        //Na aula 4A
      /*  Cursor cursor = db.rawQuery(
                "select * from "+ WheatherContract.LocationEntry.TABLE_NAME +
                 " where " + WheatherContract.LocationEntry._ID + " = ?",
                 new String[]{String.valueOf(id)});*/

        //Na aula 4B
        Cursor cursor = getContext().getContentResolver().query(
                WheatherContract.LocationEntry.CONTENT_URI,
                null, WheatherContract.LocationEntry._ID + " = ?" , new String[]{String.valueOf(id)}, null);

        assertTrue(cursor.moveToNext());
        {
            String name = cursor.getString(
                    cursor.getColumnIndex(
                            WheatherContract.LocationEntry.COLUMN_CITY_NAME));
            String location = cursor.getString(
                    cursor.getColumnIndex(
                            WheatherContract.LocationEntry.COLUMN_LOCATION_SETTING));
            double lat = cursor.getDouble(
                    cursor.getColumnIndex(
                            WheatherContract.LocationEntry.COLUMN_COORD_LAT));
            double lng = cursor.getDouble(
                    cursor.getColumnIndex(
                            WheatherContract.LocationEntry.COLUMN_COORD_LONG));

            assertEquals(name, inputCityName);
            assertEquals(location, inputLocationSetting);
            assertEquals(lat, inputLat);
            assertEquals(lng, inputLng);
        }
            cursor.close();
            db.close();

    }

}
