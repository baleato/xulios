package com.example.webxulio.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppKeyHandler {

    private static final String KEY_FILE_NAME = "com.xulios";
    private final String URL_KEY = "com.xulios.URL";
    private final String URL_POST_KEY = "com.xulios.URL_POST";
    private SharedPreferences sharedPref;
    
    public AppKeyHandler(Context context){
        sharedPref = context.getSharedPreferences(KEY_FILE_NAME, Context.MODE_PRIVATE);
    }
    
    public String getURL(){
        return sharedPref.getString(URL_KEY, "http://www.google.com");
    }

    public String getErrorPostURL() {
        return sharedPref.getString(URL_POST_KEY, null);
    }

    public void setURL(String url) {
        sharedPref.edit().putString(URL_KEY, url).commit();
    }
    
    public void setErrorPostURL(String url) {
        sharedPref.edit().putString(URL_POST_KEY, url).commit();
    }
}
