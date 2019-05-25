package com.example.m.weatherapp;

public class CityWeatherEntity {

    private String city;
    private GeoCoordinates coordinates;
    private String weatherDescription;
    private String iconId;
    private double temperature;
    private double pressure;
    private double windSpeed;
    private long sunrise;
    private long sunset;

    public long getSunrise() {
        return sunrise;
    }

    public void setSunrise(long sunrise) {
        this.sunrise = sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public void setSunset(long sunset) {
        this.sunset = sunset;
    }

    public CityWeatherEntity(String city, GeoCoordinates coordinates, String weatherDescription, String iconId, double temperature, double pressure, double windSpeed) {
        this.city = city;
        this.coordinates = coordinates;
        this.weatherDescription = weatherDescription;
        this.iconId = iconId;
        this.temperature = temperature;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
    }

    public CityWeatherEntity(String city, GeoCoordinates coordinates, String weatherDescription, String iconId, double temperature, double pressure, double windSpeed, long sunrise, long sunset) {
        this.city = city;
        this.coordinates = coordinates;
        this.weatherDescription = weatherDescription;
        this.iconId = iconId;
        this.temperature = temperature;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public GeoCoordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(GeoCoordinates coordinates) {
        this.coordinates = coordinates;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }
}
