package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.widget.Toast;

import android.location.LocationListener;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements  LocationListener{
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    String provider;
    LocationManager locationManager;
    double latitude, longitude;
    List<Address> addresses = null;
    RequestQueue requestQueue;
    Toolbar toolbar;
    Handler handler;
    AutoSuggestAdapter autoSuggestAdapter;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 200;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.i("CHECK INNER 1","CHECK INNER");
                new AlertDialog.Builder(this)
                        .setTitle("Need Location Access")
                        .setMessage("Please allow location access for weather app")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                Log.i("CHECK INNER 2","CHECK INNER");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            Log.i("CHECK INNER 3","CHECK INNER");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        provider = locationManager.getBestProvider(new Criteria(), false);
                        Location location = locationManager.getLastKnownLocation(provider);
                        if(location!=null){
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                            populateWeatherCard(latitude,longitude);
                            populateNewsCards();
                        }
                        else{
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
                        }

                    }

                } else {
                    Log.println(Log.INFO,"INFO","here");
                }
                return;
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchMenu = menu.findItem(R.id.app_bar_menu_search);
        SearchView searchView = (SearchView) searchMenu.getActionView();
        final AutoCompleteTextView searchAutoComplete = (AutoCompleteTextView) searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setTextColor(Color.BLACK);

        autoSuggestAdapter = new AutoSuggestAdapter(this,
                android.R.layout.simple_dropdown_item_1line);
        searchAutoComplete.setAdapter(autoSuggestAdapter);

        searchAutoComplete.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        searchAutoComplete.setText(autoSuggestAdapter.getObject(position));
                        final String query = autoSuggestAdapter.getObject(position);
                        searchAutoComplete.setOnEditorActionListener(new AutoCompleteTextView.OnEditorActionListener(){
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                                    //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                    //setTitle("Search Results for "+query);
                                    Fragment fragment = new Fragment();
                                    setFragment(R.id.weather_frame,fragment);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("query", query );
                                    HomeFragment homeFragment = new HomeFragment();
                                    homeFragment.setArguments(bundle);
                                    setFragment(R.id.card_frame,homeFragment);
                                    return true;
                                }
                                return false;
                            }
                        });
                    }
                });
        searchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }


            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(searchAutoComplete.getText())) {
                        makeApiCall(searchAutoComplete.getText().toString());
                    }
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void makeApiCall(String text) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        String url = "https://webtech-autosuggest-v2.cognitiveservices.azure.com/bing/v7.0/suggestions?q="+text;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject responseObject) {
                List<String> stringList = new ArrayList<>();
                try {
                    //JSONObject responseObject = new JSONObject(response);
//                    JSONArray array = responseObject.getJSONArray("results");
                    JSONArray array = responseObject.getJSONArray("suggestionGroups").
                            getJSONObject(0).getJSONArray("searchSuggestions");
                    for (int i = 0; i < 5; i++) {
                        JSONObject row = array.getJSONObject(i);
                        //stringList.add(row.getString("trackName"));
                        stringList.add(row.getString("displayText"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                autoSuggestAdapter.setData(stringList);
                autoSuggestAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Ocp-Apim-Subscription-Key", "d60658ee24b9447f80fbc114ee129003");
                return headers;
            }
        };
        mRequestQueue.add(req);

    }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onCreate (Bundle savedInstanceState){
            setTheme(R.style.AppTheme);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            setTitle("NewsApp");

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.i("CHECK WEATHER","CHECK WEATHER");
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
            }
            Log.i("CHECK WEATHER123","CHECK WEATHER");

            if(checkLocationPermission()) populateNewsCards();


            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navbar1);
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_home).setChecked(true);

           // bottomNavigationView.setSelectedItemId(R.id.bottom_nav_home);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    Log.i("CHECK WHICH TAB ",id+"");
                    if(id==(R.id.bottom_nav_home)){
                        populateWeatherCard(latitude,longitude);
                        populateNewsCards();
                        return true;
                    }
                    else if(id==(R.id.bottom_nav_home_headlines)) {
                        Log.i("ISSUE CHECK","here");
                        Fragment fragment = new Fragment();
                        setFragment(R.id.weather_frame,fragment);
                        HeadlineFragment headlineFragment = new HeadlineFragment();
                        setFragment(R.id.card_frame,headlineFragment);
                        return true;
                    }
                    else if(id==(R.id.bottom_nav_home_trending)) {
                        Fragment fragment = new Fragment();
                        setFragment(R.id.weather_frame,fragment);
                        TrendingFragment trendingFragment = new TrendingFragment();
                        setFragment(R.id.card_frame,trendingFragment);
                        return true;
                    }
                    else if(id==(R.id.bottom_nav_home_bookmarks)){
                        Fragment fragment = new Fragment();
                        setFragment(R.id.weather_frame,fragment);
                        BookmarkFragment bookmarkFragment = new BookmarkFragment();
                        setFragment(R.id.card_frame,bookmarkFragment);
                        return true;
                    }
                    else return true;
                }
            });
        }

        private void setFragment(int id, Fragment fragment){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(id, fragment).addToBackStack("test");
            fragmentTransaction.commit();
        }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);

    }

    private String getCityState(double latitude, double longitude){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String cityName = addresses.get(0).getAddressLine(0);
        return cityName;
    }

    private void populateWeatherCard(double latitude, double longitude){
        String city = getCityState(latitude,longitude);
        final String cityName = city.split(",")[1].trim();
        final String state = "California";
        requestQueue = Volley.newRequestQueue(this);
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+cityName+"&units=metric&appid=0497bc99bb38ef144842e0a75fbc9ade";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String temperature = response.getJSONObject("main").getString("temp");
                    String weather_type = response.getJSONArray("weather").getJSONObject(0).getString("main");
                    int imgSrc;
                    Log.println(Log.INFO,"RESPONSE",temperature+" "+weather_type);
                    Bundle bundle = new Bundle();
                    bundle.putString("city", cityName );
                    bundle.putString("state", state );
                    bundle.putString("temperature", temperature+" °C");
                    bundle.putString("weatherType", weather_type );

                    switch (weather_type){
                        case "Clouds":
                            imgSrc=R.drawable.cloudy_weather;
                            break;
                        case "Clear":
                            imgSrc=R.drawable.clear_weather;
                            break;
                        case "Snow":
                            imgSrc=R.drawable.snowy_weather;
                            break;
                        case "Rain":
                            imgSrc=R.drawable.rainy_weather;
                            break;
                        case "Drizzle":
                            imgSrc=R.drawable.rainy_weather;
                            break;
                        case "Thunderstorm":
                            imgSrc=R.drawable.thunder_weather;
                            break;
                        default:
                            imgSrc=R.drawable.sunny_weather;
                            break;
                    }
                    bundle.putInt("imgSrc",imgSrc);
                    WeatherFragment weatherFragment = new WeatherFragment();
                    weatherFragment.setArguments(bundle);
                    setFragment(R.id.weather_frame,weatherFragment);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void populateNewsCards(){
//        if(getSupportFragmentManager().getFragments().size()>=1){
//            Fragment fragment = getSupportFragmentManager().getFragments()
//                    .get(getSupportFragmentManager().getFragments().size() - 1);
//            Log.i("CHECK FRAGMENT",fragment.getId()+"");
//            Fragment fragment1 = new Fragment();
//            setFragment(R.id.weather_frame,fragment1);
//            HeadlineFragment headlineFragment = new HeadlineFragment();
//            if(fragment.getId()==headlineFragment.getId()){
//                Log.i("ISSUE CHECK","here");
//                setFragment(R.id.card_frame,headlineFragment);
//                return;
//            }
//        }



        Bundle bundle = new Bundle();
        bundle.putString("category", "latest" );
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);
        setFragment(R.id.card_frame,homeFragment);
    }

    @Override
    public void onLocationChanged(Location location) {
        locationManager.removeUpdates(this);
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        populateWeatherCard(latitude,longitude);
        populateNewsCards();


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
