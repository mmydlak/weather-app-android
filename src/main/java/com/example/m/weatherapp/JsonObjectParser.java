package com.example.m.weatherapp;

import org.json.JSONException;
import org.json.JSONObject;

public interface JsonObjectParser<T> {

    T parseJsonObject(JSONObject jsonObject) throws JSONException;
}
