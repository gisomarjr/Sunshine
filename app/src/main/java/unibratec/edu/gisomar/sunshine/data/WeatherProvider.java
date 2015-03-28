package unibratec.edu.gisomar.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;


public class WeatherProvider extends ContentProvider {
    public WeatherProvider() {
    }

    //Aula 4b
    private WeatherDbHelper weatherDbHelper;
    //join
    public static SQLiteQueryBuilder sLocationJoinWeather = null;
    public static String sLocationFilter = null;
    public static  String sLocationWithStartDate = null;
    public static final String sLocationWithDate;


    //AULA 4b
    public static final int WEATHER = 100;
    public static final int WEATHER_WITH_LOCATION = 101;
    public static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    public static final int LOCATION = 200;
    private static UriMatcher sUriMatcher = buildUriMatcher();
    private static UriMatcher buildUriMatcher(){
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WheatherContract.CONTENT_AUTHORITY;
        uriMatcher.addURI(authority, WheatherContract.PATH_WEATHER,
                WEATHER); // 100
        uriMatcher.addURI(authority, WheatherContract.PATH_WEATHER + "/*",
                WEATHER_WITH_LOCATION); // 101
        uriMatcher.addURI(authority, WheatherContract.PATH_WEATHER + "/*/*",
                WEATHER_WITH_LOCATION_AND_DATE); // 102
        uriMatcher.addURI(authority, WheatherContract.PATH_LOCATION,
                LOCATION); // 200
        return uriMatcher;
    }

    /**
     *
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = weatherDbHelper.getWritableDatabase();
        int affectedRows = 0;
        final int uriType = sUriMatcher.match(uri);
        switch (uriType) {
            case WEATHER: {
                affectedRows = db.delete(WheatherContract.WeatherEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            }
            case LOCATION: {
                affectedRows = db.delete(WheatherContract.LocationEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            }
            default: throw new UnsupportedOperationException("Unknow uri: "+ uri);
        }
        if (selection == null || affectedRows != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;
    }

    /**
     *
     * @param uri
     * @return
     */
    @Override
    public String getType(Uri uri) {
        final int uriType = sUriMatcher.match(uri);
        switch (uriType){
            case WEATHER_WITH_LOCATION_AND_DATE:
                return WheatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
            case WEATHER_WITH_LOCATION:
            case WEATHER:
                return WheatherContract.WeatherEntry.CONTENT_TYPE;
            case LOCATION:
                return WheatherContract.LocationEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unkownn Uri: "+ uri);
        }

    }


    /**
     *
     * @param uri
     * @param values
     * @return
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Uri insertedUri = null;
        final int uriType = sUriMatcher.match(uri);
        SQLiteDatabase db = weatherDbHelper.getWritableDatabase();
        switch (uriType){
            case WEATHER: {
                long _id = db.insert(
                        WheatherContract.WeatherEntry.TABLE_NAME, null, values);
                if (_id != -1){
                    insertedUri =
                            WheatherContract.WeatherEntry.buildWeatherUri(_id);
                } else {
                    throw new SQLException("Fail to insert weather.");
                }
                break;
            }
            case LOCATION:
                long _id = db.insert(
                        WheatherContract.LocationEntry.TABLE_NAME, null, values);
                if (_id != -1){
                    insertedUri = WheatherContract.LocationEntry.buildLocationUri(_id);
                } else {
                    throw new SQLException("Fail to insert location.");
                }
                break;
        }
        //No caso de mudanças na uri - no caso inseriu novamente
        //Realizar novas buscas
        getContext().getContentResolver().notifyChange(uri, null);
        return insertedUri;
    }


    /**
     *
     * @param uri
     * @param values
     * @return
     */
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final int uriType = sUriMatcher.match(uri);
        switch (uriType) {
            case WEATHER: {
                int count = 0;
                SQLiteDatabase db = weatherDbHelper.getWritableDatabase();
                db.beginTransaction();
                try {
                    for (int i = 0; i < values.length; i++) {
                        long _id = db.insert(WheatherContract.WeatherEntry.TABLE_NAME,
                                null, values[i]);
                        if (_id != -1) {
                            count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public boolean onCreate() {
        weatherDbHelper = new WeatherDbHelper(getContext());
        return true;
    }

    /**
     * Query
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor = null;
        final int uriType = sUriMatcher.match(uri);
        switch (uriType){
            case WEATHER:
                cursor = weatherDbHelper.getWritableDatabase().query(
                        WheatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case LOCATION:
                cursor = weatherDbHelper.getReadableDatabase().query(
                        WheatherContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case WEATHER_WITH_LOCATION:
                cursor = getWeatherByLocation(uri, projection, sortOrder);
                break;

            case WEATHER_WITH_LOCATION_AND_DATE:
                cursor = getWeatherByLocationAndDate(uri, projection, sortOrder);
                break;
        }

        if(cursor != null){
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

      return cursor;

    }

    /**
     *
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        SQLiteDatabase db = weatherDbHelper.getWritableDatabase();
        int affectedRows = 0;
        final int uriType = sUriMatcher.match(uri);
        switch (uriType) {
            case WEATHER: {
                affectedRows = db.update(WheatherContract.WeatherEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            }
            case LOCATION: {
                affectedRows = db.update(WheatherContract.LocationEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            }
            default: throw new UnsupportedOperationException("Unknow uri: "+ uri);
        }
        if (affectedRows != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;
    }


    //Aula 4b - join
    static {
        String tblLocation = WheatherContract.LocationEntry.TABLE_NAME;
        String tblWeather = WheatherContract.WeatherEntry.TABLE_NAME;
        // JOIN
        sLocationJoinWeather = new SQLiteQueryBuilder();
        sLocationJoinWeather.setTables(
                tblLocation +" INNER JOIN "+ tblWeather +" ON "+
                        tblLocation +"."+ WheatherContract.LocationEntry._ID +" = "+
                        tblWeather +"."+ WheatherContract.WeatherEntry.COLUMN_LOC_KEY);
        // Weather by Location
        sLocationFilter = tblLocation +"."+
                WheatherContract.LocationEntry.COLUMN_LOCATION_SETTING +" = ? ";
        // Weather by Location and start date
        sLocationWithStartDate = sLocationFilter +" AND "+
                tblWeather +"."+ WheatherContract.WeatherEntry.COLUMN_DATETEXT +" >= ?";

        sLocationWithDate = sLocationFilter +" AND "+
                tblWeather +"."+ WheatherContract.WeatherEntry.COLUMN_DATETEXT +" = ?";
    }

    /**
     *
     * @param uri
     * @param projection
     * @param sortOrder
     * @return
     */
    private Cursor getWeatherByLocation(
            Uri uri, String[] projection, String sortOrder){
        String locationSetting =
                WheatherContract.WeatherEntry.getLocationFromUri(uri);
        String startDate =
                WheatherContract.WeatherEntry.getStartDateFromUri(uri);
        String selection = (startDate == null) ?
                sLocationFilter : sLocationWithStartDate;
        String[] selectionArgs = startDate == null ?
                new String[] { locationSetting } :
                new String[] { locationSetting, startDate };
        return sLocationJoinWeather.query(
                weatherDbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
    }

    /**
     *
     * @param uri
     * @param projection
     * @param sortOrder
     * @return
     */
    private Cursor getWeatherByLocationAndDate(
            Uri uri, String[] projection, String sortOrder){
        String locationSetting =
                WheatherContract.WeatherEntry.getLocationFromUri(uri);
        String date = WheatherContract.WeatherEntry.getDateFromUri(uri);
        String[] selectionArgs = new String[] { locationSetting, date };
        return sLocationJoinWeather.query(
                weatherDbHelper.getReadableDatabase(),
                projection, sLocationWithDate, selectionArgs, null, null,
                sortOrder);
    }



}
