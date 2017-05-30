package com.example.android.newsfeedapp;

import java.util.ArrayList;

public class News {

    private String mImage;

    private String mTitle;

    private String mAuthor;

    private String mSection;

    private String mDate;

    /** Website URL of the earthquake */
    private String mUrl;

    public News(String image, String title, String author, String section, String date, String url){
        mImage = image;
        mTitle = title;
        mAuthor = author;
        mSection = section;
        mDate = date;
        mUrl = url;
    }

    public String getImage(){return  mImage;}

    public String getTitle(){
        return mTitle;
    }

    public String getAuthor(){
        return mAuthor;
    }

    public String getSection() {
        return mSection;
    }

    public String getDate() {
        return mDate;
    }

    public String getUrl() {
        return mUrl;
    }
}

