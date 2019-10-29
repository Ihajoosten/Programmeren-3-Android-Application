package com.example.element.models;

import java.io.Serializable;

public class Element implements Serializable {

    //Dit is de class Blindwall, deze gegevens willen we gebruiken
    private String identification;
    private String title;
    private String geographicalLocation;
    private String artist;
    private String material;
    private String underLayer;
    private String description;
    private String imageUri;
    private double latitude;
    private double longitude;



    public Element(String identification,
                   String title,
                   String geographicalLocation,
                   String artist,
                   String material,
                   String underLayer,
                   String description,
                   String imageUri,
                   double latitude,
                   double longitude) {
        this.imageUri = imageUri;
        this.identification = identification;
        this.title = title;
        this.geographicalLocation = geographicalLocation;
        this.artist = artist;
        this.material = material;
        this.underLayer = underLayer;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGeographicalLocation() {
        return geographicalLocation;
    }

    public void setGeographicalLocation(String geographicalLocation) {
        this.geographicalLocation = geographicalLocation;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getUnderLayer() {
        return underLayer;
    }

    public void setUnderLayer(String underLayer) {
        this.underLayer = underLayer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
