package com.example.emyeraky.mal_popmovies;

/**
 * Created by Emy on 8/3/2016.
 */

public class MovieData {
    private String ID;
    private String poster_path ;
    private String time ;
    private String original_title ;
    private String popularity ;
    private String date;

    public void setID(String ID) {
        this.ID = ID;
    }
    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getID() {
        return ID;
    }
    public String getPoster_path() {
        return poster_path;
    }

    public String getTime() {
        return time;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getPopularity() {
        return popularity;
    }

    public String getDate() {
        return date;
    }
}
