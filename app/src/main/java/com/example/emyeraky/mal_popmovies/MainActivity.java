package com.example.emyeraky.mal_popmovies;


import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static boolean flag = true;
    Display df;
    View fragmentDetail;
    static GridView gridView;
    static MovieData[] movieData;
    DBController dbController;
    static Context context;
    Intent ii;
    static String state ="";
    ArrayList<MovieData> movieDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context=getBaseContext();

        //////////////////////////////////////////////////////////////////////

       //check Internet connected
        if (isNetworkConnected()) {
            // notify user you are online
            if(state.equals("popular")){
                FetchImage fetchImage = new FetchImage();
                fetchImage.execute("popular?");
                state = "popular";
            }
            else if(state.equals("top_rated")){
                FetchImage fetchImage = new FetchImage();
                fetchImage.execute("top_rated?");
                state = "top_rated";
            }
            else if(state.equals("favorite")){
                favorit();
                state = "favorite";
            }else {
                FetchImage fetchImage = new FetchImage();
                fetchImage.execute("popular?");
                state = "popular";
            }

        } else {
            Toast.makeText(getBaseContext(), "No Internet connected!!", Toast.LENGTH_SHORT).show();
        }
        gridView=(GridView)findViewById(R.id.gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                actionLand(flag,position);
            }
        });

    }

    //will return true if the device is operating on a large screen.
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;

    }

    // use to tell connection network
    static  boolean isNetworkConnected(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

        @Override
        public void onStart() {
            super.onStart();
            //check Internet connected
            if (isNetworkConnected()) {
                // notify user you are online
                if(state.equals("popular")){
                    FetchImage fetchImage = new FetchImage();
                    fetchImage.execute("popular?");
                    state = "popular";
                    Toast.makeText(MainActivity.this, "popular", Toast.LENGTH_SHORT).show();
                }
                else if(state.equals("top_rated")){
                    FetchImage fetchImage = new FetchImage();
                    fetchImage.execute("top_rated?");
                    state = "top_rated";
                    Toast.makeText(MainActivity.this, "top_rated", Toast.LENGTH_SHORT).show();
                }
                else if(state.equals("favorite")){
                    favorit();
                    state = "favorite";
                    Toast.makeText(MainActivity.this, "favorite", Toast.LENGTH_SHORT).show();
                }else {
                    FetchImage fetchImage = new FetchImage();
                    fetchImage.execute("popular?");
                    state = "popular";
                    Toast.makeText(MainActivity.this, "popular", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getBaseContext(), "No Internet connected!!", Toast.LENGTH_SHORT).show();
            }
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            df = getWindowManager().getDefaultDisplay();
            if (df.getWidth() > df.getHeight()||isTablet(getBaseContext())) {
                flag = false;
                //landscape
                //   ft.replace(R.id.activity_main_root_container,new Land());
                fragmentDetail = findViewById(R.id.fragment2);
                fragmentDetail.setVisibility(View.INVISIBLE);
            } else {
                flag = true;
            }
            ft.commit();
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id= item.getItemId();
            if(id==R.id.popular_settings&&isNetworkConnected()){
                FetchImage fetchImage =new FetchImage();
                fetchImage.execute("popular?");
                state = "popular";
                Toast.makeText(MainActivity.this, "popular", Toast.LENGTH_SHORT).show();
            }
            else if(id==R.id.toprated_settings&&isNetworkConnected()){
                FetchImage fetchImage = new FetchImage();
                fetchImage.execute("top_rated?");
                state = "top_rated";
                Toast.makeText(MainActivity.this, "top_rated", Toast.LENGTH_SHORT).show();
            }

            //return favorite from DB
            else {
                if (isNetworkConnected()) {
                  favorit();
                    state = "favorite";
                    Toast.makeText(MainActivity.this, "favorite", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(MainActivity.this, "No Internet Connected", Toast.LENGTH_SHORT).show();
            }
            return super.onOptionsItemSelected(item);
        }

    public void favorit(){
        movieDataList = new ArrayList<>();
        MovieData movie;

        dbController = new DBController(this);
        dbController.open();
        try {
            Cursor cursor = dbController.get_dataselect();
            if (cursor.moveToFirst()) {
                do {
                    movie = new MovieData();
                    movie.setID(cursor.getString(0));
                    movie.setOriginal_title(cursor.getString(1));
                    movie.setTime(cursor.getString(2));
                    movie.setDate(cursor.getString(3));
                    movie.setPoster_path(cursor.getString(4));
                    movie.setvote_average(cursor.getString(5));

                    movieDataList.add(movie);
                } while (cursor.moveToNext());
                String[] poster = new String[movieDataList.size()];

                for (int i = 0; i < poster.length; i++) {
                    poster[i] = movieDataList.get(i).getPoster_path();
                }
                //set image in grid to display
                CustomImage customImage = new CustomImage(getBaseContext(), poster);
                gridView.setAdapter(customImage);
                movieData = new MovieData[movieDataList.size()];
                for (int i=0;i<movieDataList.size();i++){
                    movieData[i]= movieDataList.get(i);
                }
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        actionLand(flag, position);
                    }
                });

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        dbController.close_db();
    }


    //get data from Json
    private MovieData[] getImageDataFromJson(String imageJsonStr) throws JSONException {

        final String id = "id";
        final String poster_path = "poster_path";
        final String original_title = "original_title";
        final String vote_average = "vote_average";

        JSONObject imageJson = new JSONObject(imageJsonStr);
        JSONArray imageArray =  imageJson.getJSONArray("results");

        movieData= new MovieData[imageArray.length()];


        for(int i = 0; i < imageArray.length(); i++) {
            JSONObject image = imageArray.getJSONObject(i);

            movieData[i] = new MovieData();
            movieData[i].setID(image.getString(id));
            movieData[i].setPoster_path(image.getString(poster_path));
            movieData[i].setOriginal_title(image.getString(original_title));
            movieData[i].setvote_average(image.getString(vote_average));
        }

        return movieData;
    }

    public class FetchImage extends AsyncTask<String, ProgressDialog, MovieData[]> {
        private String LOG_TAG = FetchImage.class.getSimpleName();

        @Override
        protected MovieData[] doInBackground(String... params) {

            if (params.length == 0) {

                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String imageJsonStr = null;

            try {
                final String FORECAST_BASE_URL =
                        //top_rated?
                        //change
                        "https://api.themoviedb.org/3/movie/" + params[0];
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, "72659fcbe6b80e24ac36ddb5bbdbe316")
                        .build();

                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                imageJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getImageDataFromJson(imageJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MovieData[] movieDatas) {
            String[] posters = new String[movieDatas.length];

            if (movieDatas != null) {
                for (int i = 0; i < movieDatas.length; i++) {

                    posters[i] = movieDatas[i].getPoster_path();
                }

                CustomImage customImage = new CustomImage(getBaseContext(), posters);
                gridView.setAdapter(customImage);
            }
        }
    }
    public void actionLand (boolean flag, int position){

        if(flag==false){
            DetailsActivityFragment.movieData = movieData[position];
            fragmentDetail.setVisibility(View.VISIBLE);
            DetailsActivityFragment.oncreate();
        } else{
            ii = new Intent(MainActivity.this, DetailsActivity.class);
            DetailsActivityFragment.movieData = movieData[position];
            startActivity(ii);
        }
        }
    }





