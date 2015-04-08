package unibratec.edu.gisomar.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;



public class MainActivity extends ActionBarActivity {

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        }else if (id == R.id.action_map){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String location = prefs.getString(
                    getString(R.string.pref_location_key),
                    getString(R.string.pref_location_default));

            Uri uri = Uri.parse("geo:0,0?")
                    .buildUpon()
                    .appendQueryParameter("q", location)
                    .build();
            Intent it = new Intent(Intent.ACTION_VIEW);
            it.setData(uri);
            if (it.resolveActivity(getPackageManager()) != null){
                startActivity(it);
            } else {
                // Nenhuma app de mapa instalada! :(
            }
        }

        return super.onOptionsItemSelected(item);
    }


}
