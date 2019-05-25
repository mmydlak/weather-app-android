package com.example.m.weatherapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class ShowWeatherActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener,
        Response.Listener<JSONObject>,
        Response.ErrorListener,
        JsonObjectParser<CityWeatherEntity>{


    private Date currentDate;
    private Toolbar toolbar;
    private Spinner spinner;
    private TextView cityLabel;
    private TextView coordinatesLabel;
    private TextView weatherLabel;
    private TextView tempValueLabel;
    private TextView pressureValueLabel;
    private TextView windValueLabel;
    private ImageView weatherImage;
    private RelativeLayout mainLayout;
    private RequestQueue mRequestQueue;
    private GeoCoordinates localCoords;
    private CityWeatherEntity cityWeatherEntity;
    private boolean isDayThemeSet = false;
    private boolean isNightThemeSet = false;
    private String sharedPrefKey = "main";
    private OWMapRequestCustomizer owmReuestCustomizer = new OWMapRequestCustomizer(KeyHolder.getKey());

    public LocationService locationService;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            String name = className.getClassName();

            if (name.endsWith("LocationService")) {
                //Toast.makeText(ShowWeatherActivity.this, "Zaczynam updejtowac lokacje", Toast.LENGTH_SHORT).show();
                locationService = ((LocationService.LocationServiceBinder) service).getService();
                locationService.startUpdatingLocation();
                Location newLocation = locationService.getLastLocation();
                if(newLocation!=null){
                    localCoords.setLat(newLocation.getLatitude());
                    localCoords.setLon(newLocation.getLongitude());
                }
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            if (className.getClassName().equals("LocationService")) {
                locationService = null;
            }
        }
    };

    private BroadcastReceiver locationUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(ShowWeatherActivity.this, "Jestem w onReceive bo zmienily sie wsp", Toast.LENGTH_SHORT).show();

            Location newLocation = intent.getParcelableExtra("location");
            //localCoords = new GeoCoordinates(newLocation.getLatitude(), newLocation.getLongitude());

            localCoords.setLat(newLocation.getLatitude());
            localCoords.setLon(newLocation.getLongitude());
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_weather);
        Toast.makeText(this, "Zaczynam", Toast.LENGTH_SHORT).show();
        localCoords = new GeoCoordinates(0,0);

        final Intent serviceStart = new Intent(this.getApplication(), LocationService.class);
        this.getApplication().startService(serviceStart);
        this.getApplication().bindService(serviceStart, serviceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                locationUpdateReceiver,
                new IntentFilter("LocationUpdated"));

        findViews();
        toolbar.setTitle(getResources().getString(R.string.app_name));
        //toolbar.setTitleTextColor(Color.RED);

        cutomizeSpinner();

        setRequestQueue();

        //setNightMotive();

//        if (restore() != null) {
//            cityWeatherEntity = restore();
//            getWeatherForCityByName(cityWeatherEntity.getCity());
//            displayWeatherInfo(cityWeatherEntity);
//        }
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

//    public void save() {
//        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor sharedPrefEditor = sharedPrefs.edit();
//        Gson gson = new Gson();
//
//        String json = gson.toJson(cityWeatherEntity);
//
//        sharedPrefEditor.putString(sharedPrefKey, json);
//        sharedPrefEditor.commit();
//    }






//
//    private CityWeatherEntity restore() {
//        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
//        Gson gson = new Gson();
//        String jsonTaskList = sharedPrefs.getString(sharedPrefKey, null);
//        Type type = new TypeToken<CityWeatherEntity>() {}.getType();
//        return gson.fromJson(jsonTaskList, type);
//    }

    private void findViews(){
        toolbar = findViewById(R.id.toolbar);
        cityLabel = findViewById(R.id.cityLabel);
        coordinatesLabel = findViewById(R.id.coordinatesLabel);
        weatherLabel = findViewById(R.id.weatherLabel);
        tempValueLabel = findViewById(R.id.tempValueLabel);
        pressureValueLabel = findViewById(R.id.pressureValueLabel);
        windValueLabel = findViewById(R.id.windValueLabel);
        weatherImage = findViewById(R.id.weatherImage);
        mainLayout = findViewById(R.id.mainLayout);
    }

    private boolean isNight(CityWeatherEntity entity){
        long sunrise = entity.getSunrise();
        long sunset = entity.getSunset();
        Date sunriseDate = new Date();
        Date sunsetDate = new Date();
        sunriseDate.setTime(sunrise*1000);
        sunsetDate.setTime(sunset*1000-86400000);
        //TimeZone.
        //Calendar.getInstance().HosetTime(sunriseDate);
        currentDate = Calendar.getInstance().getTime();
        currentDate.setTime(currentDate.getTime());//-(86400000));
        if(currentDate.before(sunriseDate) && currentDate.after(sunsetDate)){
            return true;
        }
        else {
            return false;
        }
    }

    private void setDayMotive(){
        isNightThemeSet = false;
        isDayThemeSet = true;
        toolbar.setBackgroundResource(R.color.colorPrimary);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            window.setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));

        }
        //ResourcesCompat.getDrawable(getResources(), R.drawable.day_bg, null);
        mainLayout.setBackgroundResource(R.drawable.day_bg);
        changeTextColor(mainLayout, Color.rgb(0,30,50));
        toolbar.setTitleTextColor(Color.WHITE);
    }

    private void changeTextColor(ViewGroup layout, int color) {
        for (int count=0; count < layout.getChildCount(); count++){
            View view = layout.getChildAt(count);
            if(view instanceof TextView) {
                ((TextView) view).setTextColor(color);
            } else if(view instanceof ViewGroup){
                if(!(view instanceof Spinner)) {
                    changeTextColor((ViewGroup) view, color);
                }
            }
        }
    }

    private void setNightMotive() {
        isNightThemeSet = true;
        isDayThemeSet = false;
        toolbar.setBackgroundResource(R.color.colorPrimary2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark2));
            window.setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark2));

        }
        mainLayout.setBackgroundResource(R.drawable.night_bg);
        changeTextColor(mainLayout, Color.WHITE);
    }


    private void setRequestQueue(){
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);
        // Start the queue
        mRequestQueue.start();
    }

    private void cutomizeSpinner(){
        spinner = findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.cities, R.layout.custom_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void displayWeatherInfo(CityWeatherEntity cityWeatherEntity) {
        cityLabel.setText(cityWeatherEntity.getCity());
        coordinatesLabel.setText(cityWeatherEntity.getCoordinates().toString());
        owmReuestCustomizer.getWeatherIconRequest(cityWeatherEntity.getIconId()).resize(140, 140).into(weatherImage);
        weatherLabel.setText(cityWeatherEntity.getWeatherDescription());
        tempValueLabel.setText(""+(int)cityWeatherEntity.getTemperature() + "ºC");
        pressureValueLabel.setText(""+(int)cityWeatherEntity.getPressure() + " hPa");
        windValueLabel.setText(""+cityWeatherEntity.getWindSpeed()+" m/s");
        if(isNight(cityWeatherEntity)){
            if(!isNightThemeSet) {
                setNightMotive();
            }
        }
        else {
            if(!isDayThemeSet) {
                setDayMotive();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String city = parent.getItemAtPosition(position).toString();
        if(city.equals("MyLocation")){
            //Toast.makeText(this, "Jestem w onItemSelected w mylocation", Toast.LENGTH_SHORT).show();
            mRequestQueue.add(owmReuestCustomizer.getWeatherRequestByCoordinates(""+localCoords.getLat(), ""+localCoords.getLon(), null, this, this));
        }
        else {
            mRequestQueue.add(owmReuestCustomizer.getWeatherRequestByCityName(city, null, this, this));
        }
        //((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
    }

    private void getWeatherForCityByName(String city) {
        mRequestQueue.add(owmReuestCustomizer.getWeatherRequestByCityName(city, null, this, this));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onResponse(JSONObject response) {

        try {
            cityWeatherEntity = parseJsonObject(response);
            displayWeatherInfo(cityWeatherEntity);
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
            e.printStackTrace();
        }

//        JSONObject a;
//        JSONArray b;
//
//        try {
//            b = response.getJSONArray("weather");
//            a = b.getJSONObject(0);
//            String text = a.getString("main");
//            weatherLabel.setText(text);
//            text = response.getString("name");
//            cityLabel.setText(text);
//            //notifyAll();
//
//            String iconURL = "http://openweathermap.org/img/w/10d.png";
//            Picasso.get().load(iconURL).resize(130,130).into(weatherImage);
//            StringRequest stringRequest = new StringRequest(iconURL, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    Toast.makeText(ShowWeatherActivity.this, response, Toast.LENGTH_LONG).show();
//
//                    //Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(imageView);
//
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(ShowWeatherActivity.this, "nie da sie", Toast.LENGTH_LONG).show();
//                }
//            });
//
//            mRequestQueue.add(stringRequest);
//
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        //Toast.makeText(this, "Zaczynam", Toast.LENGTH_SHORT).show();
        //save();
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Getting weather failed.", Toast.LENGTH_LONG).show();
    }

    @Override
    public CityWeatherEntity parseJsonObject(JSONObject jsonObject) throws JSONException {
        //jsonObject = jsonObject.getJSONArray("list").getJSONObject(0);
        JSONObject weather = jsonObject.getJSONArray("weather").getJSONObject(0);
        JSONObject main = jsonObject.getJSONObject("main");
        JSONObject coords = jsonObject.getJSONObject("coord");
        GeoCoordinates geoCoords = new GeoCoordinates(coords.getDouble("lat"), coords.getDouble("lon"));
        JSONObject sys = jsonObject.getJSONObject("sys");
        double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
        String city = jsonObject.getString("name");

        return new CityWeatherEntity(city,
                geoCoords,
                weather.getString("description"),
                weather.getString("icon"),
                main.getDouble("temp"),
                main.getDouble("pressure"),
                windSpeed,
                sys.getLong("sunrise"),
                sys.getLong("sunset"));
    }




//    public void refresh(View view) {
//        finish();
//        startActivity(getIntent());
//    }
}
