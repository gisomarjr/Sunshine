package unibratec.edu.gisomar.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import unibratec.edu.gisomar.sunshine.data.WheatherContract;

public class ForecastAdapter extends CursorAdapter {
    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     *
     * @param context
     * @param cursor
     * @param parent
     * @return
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.list_item_forecast, parent, false);
        return view;
    }

    /**
     *
     * @param view
     * @param context
     * @param cursor
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int idx_max_temp = cursor.getColumnIndex(
                WheatherContract.WeatherEntry.COLUMN_MAX_TEMP);
        int idx_min_temp = cursor.getColumnIndex(
                WheatherContract.WeatherEntry.COLUMN_MIN_TEMP);
        int idx_date = cursor.getColumnIndex(
                WheatherContract.WeatherEntry.COLUMN_DATETEXT);
        int idx_short_desc = cursor.getColumnIndex(
                WheatherContract.WeatherEntry.COLUMN_SHORT_DESC);
        boolean isMetric = Utility.isMetric(mContext);
        String highAndLow =
                Utility.formatTemperature(cursor.getDouble(idx_max_temp), isMetric)
                        + "/" +
                        Utility.formatTemperature(cursor.getDouble(idx_min_temp), isMetric);
        String weatherText = Utility.formatDate(cursor.getString(idx_date))
                + " - " + cursor.getString(idx_short_desc) + " - " + highAndLow;
        TextView tv = (TextView)view;
        tv.setText(weatherText);
    }
}