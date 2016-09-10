package com.example.emyeraky.mal_popmovies;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {
    static TextView title;
    static TextView rate;
    static TextView date;
    static TextView time;
    static ImageView poster;
    static Button bnt_favorite;
    static String poster_url;
    static DBController dbController;
    static MovieData movieData;
    static String [] Keys;
    static String flag;
    static String key ;
    static Context context;
    static Intent intent;
    View view;
    static ListView listView_overView;
    static ListView listView_trailer;
    static ArrayList<String> poster_List;
    public DetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_details , container, false);

         listView_trailer = (ListView) view.findViewById(R.id.list_item);
         listView_overView = (ListView) view.findViewById(R.id.overview);

        bnt_favorite = (Button)view.findViewById(R.id.favorite);
        title = (TextView)view.findViewById(R.id.original_title);
        date = (TextView)view.findViewById(R.id.date);
        time = (TextView)view.findViewById(R.id.time);
        context = getActivity();
        rate = (TextView)view.findViewById(R.id.rate);
        poster = (ImageView)view.findViewById(R.id.poster_path);
        oncreate();
        poster_List = new ArrayList<>();
        listView_trailer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(poster_List.get(i)));
                startActivity(intent);
            }
        });


        return view;
    }

    static void oncreate( ){
        if(movieData ==null){

        }
        else {
            if (MainActivity.isNetworkConnected()) {
                // notify user you are online
                FetchVideo video = new FetchVideo();
                video.execute(movieData.getID(), "/videos?");
                flag = "trailer";
            } else {
                Toast.makeText(context, "No Internet connected!!", Toast.LENGTH_SHORT).show();
            }

            title.setText(movieData.getOriginal_title());
            rate.setText(movieData.getPopularity());

            String baseUrl = "http://image.tmdb.org/t/p/w185";
            poster_url = baseUrl + movieData.getPoster_path();
            Picasso.with(context).load(poster_url).into(poster);
        }
        bnt_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String t = title.getText().toString();
                String r = rate.getText().toString();
                String d = date.getText().toString();
                String tt = time.getText().toString();

                dbController = new DBController(context);

                long id = dbController.insert_db(movieData.getID(),t,poster_url,r,d,tt);
                if (id >= 0){

                    Toast.makeText(context,"already insert",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context,"dont insert",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private static String[] getVediosKeyFromJson(String imageJsonStr) throws JSONException {


        if (flag.equals("trailer")) {
            key = "key";
        }
        else if(flag.equals("reviews")){
            key = "content";
        }
        JSONObject imageJson = new JSONObject(imageJsonStr);
        JSONArray videoArray = imageJson.getJSONArray("results");

        Keys = new String[videoArray.length()];
        for (int i = 0; i < videoArray.length(); i++) {
            JSONObject videos = videoArray.getJSONObject(i);
            String keys_poster = videos.getString(key);
            Keys[i] = keys_poster;
        }

        return Keys;
    }

    public static class FetchVideo extends AsyncTask<String, ProgressDialog, String[]> {
        private String LOG_TAG = FetchVideo.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0) {

                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String videoJsonStr = null;

            try {
                final String FORECAST_BASE_URL =
                        "http://api.themoviedb.org/3/movie/" + params[0] + params[1];
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
                videoJsonStr = buffer.toString();
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
                return getVediosKeyFromJson(videoJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] keys_Poster) {

            if (keys_Poster == null){
                return;
            }
            if (flag.equals("trailer")) {
                String[] trailerText = new String[keys_Poster.length];

                if (keys_Poster != null) {
                    for (int i = 0; i < keys_Poster.length; i++) {
                        poster_List.add("http://www.youtube.com/watch?v=" + keys_Poster[i]);
                        trailerText[i] = "Trailer " + i;
                    }

                    ListTrailerAdapter listTrailer = new ListTrailerAdapter(context, trailerText);
                    listView_trailer.setAdapter(listTrailer);

                }
                FetchVideo video = new FetchVideo();
                video.execute(movieData.getID(), "/reviews?");
                flag = "reviews";
            }
            else if(flag.equals("reviews")){
                if (keys_Poster != null) {



                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            context,
                            android.R.layout.simple_list_item_1,
                            keys_Poster);

                    listView_overView.setAdapter(adapter);

                }
            }
        }}
}


