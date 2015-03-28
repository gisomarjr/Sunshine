package unibratec.edu.gisomar.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import unibratec.edu.gisomar.sunshine.data.WheatherContract;


public class DetailActivity extends ActionBarActivity {

    private static final int DETAIL_LOADER_ID = 1;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_activity2);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_activity2, menu);
        return true;
    }

    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public static class PlaceholderFragment extends Fragment
            implements LoaderManager.LoaderCallbacks<Cursor> {

        public PlaceholderFragment() {

        }

        private static final int DETAIL_LOADER_ID = 1;

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
            View rootView = inflater.inflate(R.layout.fragment_detail_activity2, container, false);

            Intent it = getActivity().getIntent();

            TextView txtForecast = (TextView)rootView.findViewById(R.id.text_forecast);
            txtForecast.setText(it.getStringExtra("forecast"));
            return rootView;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Uri uri = getActivity().getIntent().getData();
            return new CursorLoader(getActivity(), uri, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if (data.moveToFirst()) {
                String date = data.getString(data.getColumnIndex(
                        WheatherContract.WeatherEntry.COLUMN_DATETEXT));
// Outros campos...
                View view = getView();
                if (view != null) {
                    ((TextView) view.findViewById(R.id.text_forecast)).setText(date);
// Outros campos...
                }
               // mForecast = date +" - " + dscr +" - " + max +"/"+ min;
               // mShareActionProvider.setShareIntent(createShareIntent());
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

        //Aula 4C
        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
        }
    }
}
