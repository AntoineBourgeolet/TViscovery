package com.antoinebourgeolet.tviscovery;

import com.google.gson.annotations.SerializedName;

public class Genre {
    private int id = 0;

    @SerializedName("genre")
    private String genre;

    private int value = 0;


    public Genre(int id, String genre, int value) {
        this.id = id;
        this.genre = genre;
        this.value = value;
    }

    public Genre(String genre, int value) {
        this.genre = genre;
        this.value = value;
    }

    public Genre(String genre) {
        this.genre = genre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
