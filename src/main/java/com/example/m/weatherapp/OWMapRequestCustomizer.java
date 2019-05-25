package com.example.m.weatherapp;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.json.JSONObject;

public class OWMapRequestCustomizer {

    private String apiKey;
    private static final String commonURL = "http://api.openweathermap.org/data/2.5/weather?";


    public OWMapRequestCustomizer(String apiKey) {
        this.apiKey = apiKey;
    }

    private String extendURLWithCityName(String cityName){
        return commonURL + "q=" + cityName + "&cnt=1&units=metric&appid=" + apiKey;
    }

    private String extendURLWithCoordinates(String lat, String lon){
        return commonURL + "lat=" + lat + "&lon=" + lon + "&cnt=1&units=metric&appid=" + apiKey;
    }

    public JsonObjectRequest getWeatherRequestByCityName(String cityName,
                                                          JSONObject jsonRequest,
                                                          Response.Listener<JSONObject> listener,
                                                          Response.ErrorListener errorListener){
        String url = extendURLWithCityName(cityName);
        return new JsonObjectRequest(Request.Method.GET, url, jsonRequest, listener, errorListener);
    }

    public JsonObjectRequest getWeatherRequestByCoordinates(String lat,
                                                          String lon,
                                                          JSONObject jsonRequest,
                                                          Response.Listener<JSONObject> listener,
                                                          Response.ErrorListener errorListener){
        return new JsonObjectRequest(Request.Method.GET, extendURLWithCoordinates(lat, lon), jsonRequest, listener, errorListener);
    }

    private String getIconURL(String iconId) {
        return "http://openweathermap.org/img/w/" + iconId + ".png";
    }

    public RequestCreator getWeatherIconRequest(String iconId) {
        return Picasso.get().load(getIconURL(iconId));
    }


}
