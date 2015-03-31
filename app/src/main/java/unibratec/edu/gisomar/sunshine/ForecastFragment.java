package unibratec.edu.gisomar.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.Date;

import unibratec.edu.gisomar.sunshine.data.WheatherContract;

/**
 * Created by gisomar on 25/02/2015.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    ForecastAdapter adapter;
    ListView listView;

    public ForecastFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (mLocation != null && !mLocation.equals(
                Utility.getLocationSetting(getActivity()))) {
            getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        }*/
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String locationSetting = Utility.getLocationSetting(getActivity());
        String sortOrder = WheatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";
        Uri weatherForLocationUri =
                WheatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                        locationSetting, WheatherContract.WeatherEntry.getDbDateString(new Date()));
        Cursor cur = getActivity().getContentResolver().query(
                weatherForLocationUri, null, null, null, sortOrder);

        adapter = new ForecastAdapter(getActivity(), cur, 0);


         listView = (ListView)
                rootView.findViewById(R.id.listview_forecast);

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view, int position, long id) {
                        //String forecastStr = parent.getAdapter().getItem(position).toString();
                        //Toast.makeText(getActivity(), forecastStr, Toast.LENGTH_SHORT).show();

                        //Intent it = new Intent(getActivity(), DetailActivity.class);
                        //it.putExtra("forecast", forecastStr);
                        //startActivity(it);


                        //Aula 4C

                        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                        if (cursor != null) {
                            String locationSetting =
                                    Utility.getLocationSetting(getActivity());
                            String datStr = cursor.getString(cursor.getColumnIndex(
                                    WheatherContract.WeatherEntry.COLUMN_DATETEXT));
                            Uri uri = WheatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting, datStr);
                            Intent intent = new Intent(getActivity(),
                                    DetailActivity.class);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }


                });
        listView.setAdapter(adapter);


        return rootView;
    }

    /**
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {

            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(
                getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));


        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
        weatherTask.execute(location);
    }

    //---------Implementação - LoaderManager.LoaderCallbacks<Cursor>
    /**
     *
     * @param i
     * @param bundle
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    /**
     *
     * @param cursorLoader
     * @param cursor
     */
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        adapter.swapCursor(cursor);
        listView.setAdapter(adapter);
    }

    /**
     *
     * @param cursorLoader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}