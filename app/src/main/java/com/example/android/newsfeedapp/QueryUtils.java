package com.example.android.newsfeedapp;

import java.net.URL;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving news data from THE GUARDIAN.
 */
public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    @NonNull
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    @Nullable
    private static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        List<News> newses = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            if (baseJsonResponse.has("response")) {
                JSONObject responseObj = baseJsonResponse.getJSONObject("response");
                if (responseObj.has("results")) {
                    // Extract the JSONArray associated with the key called "results",
                    // which represents a list of news.
                    JSONArray newsArray = responseObj.getJSONArray("results");

                    // For each news in the newsArray, create an {@link News} object
                    for (int i = 0; i < newsArray.length(); i++) {

                        // Get a single news at position i within the list of news
                        JSONObject currentNews = newsArray.getJSONObject(i);

                        // Extract the value for the key called "sectionName"
                        String sectionName;
                        if(currentNews.has("sectionName")){
                            sectionName = currentNews.getString("sectionName");
                        } else {
                            sectionName = "No section name";
                        }

                        // Extract the value for the key called "webPublicationDate"
                        String webDate;
                        if(currentNews.has("webPublicationDate")){
                            webDate = currentNews.getString("webPublicationDate");
                        } else {
                            webDate = "No publication date";
                        }

                        // Extract the value for the key called "webTitle"
                        String webTitle;
                        if(currentNews.has("webTitle")){
                            webTitle = currentNews.getString("webTitle");
                        } else {
                            webTitle = "No title";
                        }

                        // Extract the value for the key called "webUrl"
                        String webUrl;
                        if(currentNews.has("webUrl")){
                            webUrl = currentNews.getString("webUrl");
                        } else {
                            webUrl = "No news link";
                        }

                        JSONArray authorsArray;
                        String author = "";
                        String firstname = "";
                        String secondname = "";
                        if (currentNews.has("tags")) {
                            authorsArray = currentNews.getJSONArray("tags");
                            if(authorsArray.length() != 0)
                                for (int j = 0; j < authorsArray.length(); j++) {
                                    JSONObject nameObject = authorsArray.getJSONObject(j);
                                    firstname = nameObject.getString("firstname");
                                    secondname = nameObject.getString("secondname");
                                    author = firstname +" "+ secondname;
                                }
                            else
                                author = "Uknown Author";
                        } else {
                            author = "Uknown Author";
                        }

                        // Extract the value for the key called "imgUrl"
                        JSONObject imageLinks;
                        String imgUrl;
                        if(currentNews.has("fields")){
                            //Extract the imageLinks JSONObject
                            imageLinks = currentNews.getJSONObject("thumbnail");
                            if(imageLinks.has("thumbnail"))
                                imgUrl = imageLinks.getString("thumbnail");
                            else
                                imgUrl = "No image";
                        } else {
                            imgUrl = "No image";
                        }

                        // Create a new {@link News} object with the info,
                        // and url from the JSON response.
                        News news = new News(imgUrl, webTitle, author, sectionName, webDate, webUrl);

                        // Add the new {@link News} to the list of news.
                        newses.add(news);
                    }
                }
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }

        // Return the list of news
        return newses;
    }

    /**
     * Query the Guardian News dataset and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link News}s
        List<News> news = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link News}s
        return news;
    }
}
