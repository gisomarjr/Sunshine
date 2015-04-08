package unibratec.edu.gisomar.sunshine;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.test.AndroidTestCase;

import unibratec.edu.gisomar.sunshine.data.WeatherDbHelper;
import unibratec.edu.gisomar.sunshine.data.WheatherContract;

/**
 * Created by gisomar on 10/03/15.
 */
public class TestDb extends AndroidTestCase {
    /**
     * Verifica conexão com o banco
     */
    public void testCreateDb() {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(mContext)
                .getWritableDatabase();

        assertEquals(true,db.isOpen());
        db.close();
    }


    public void testCrud(){

        WeatherDbHelper helper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //Coluna - valor

        String inputCityName = "Recife";
        String inputLocationSetting = "recife";
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

        Cursor cursor = db.rawQuery(
                "select * from "+ WheatherContract.LocationEntry.TABLE_NAME +
                 " where " + WheatherContract.LocationEntry._ID + " = ?",
                 new String[]{String.valueOf(id)});

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
