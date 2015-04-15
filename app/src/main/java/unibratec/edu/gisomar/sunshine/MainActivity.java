package unibratec.edu.gisomar.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends ActionBarActivity implements Callback {

    private boolean isTablet;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DetailFragment - fazer! - Aula 5C
        //verifica se é tablet ou celular
        isTablet = Utility.isTable(MainActivity.this);

        if(isTablet)

            if (savedInstanceState == null) {
                
                String today = new SimpleDateFormat().format(Utility.DATE_FORMAT)
                        .format(new Date());
                
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, new DetailFragment().newInstance())
                        .commit();
            }
    }

    /**
     * DetailFragment
     * ----------1ª alterar metodo onCreateLoader-------
     * Aula 5c
     *
     * if(getActivity().getIntent()getData
     * Uri uri testar se é diferente de null
     * else return null depois do if
     *
     * antes do if
     *
     * String date = getArguments().getString("weather_date");
     *   Uri uri = WeatherContrarct.wheatherEntry.builWeatherLocationWithDate(locationSetting,date);
     *
     *   //---retirar outra URI antiga
     *
     * ----------2ª alteração--------------
     *
     * //antes do oncreate
     * public static DetailFragment newInstance(String date){
     *     Bundle params = new Bundle();
     *     params.putString("weather_date",date);
     *
     *     DetailFragment df = new DetailFragment();
     *     df.setArguments(params);
     *     return df;
     * }
     *
     */


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


    /**
     * Clique do item para verificar se ele vai para o detail ou para um fragment
     * caso for tablet
     * Aula 5C
     * @param date
     */
    @Override
    public void onItemSelected(String date) {

        if(isTablet){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, DetailFragment.newInstance(date))
                    .commit();
        }else{
            Intent it = new Intent(this, DetailActivity.class);
            it.putExtra("weather_date",date);
            startActivity(it);
        }

    }
}
