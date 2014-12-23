package com.android.finalproject.tubbsreddit;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tubbster on 12/19/14.
 */
public class FetchSubReddit extends AsyncTask<Void,Void,String[]> {
     private final static String TAG = FetchSubReddit.class.getSimpleName();

    @Override
    protected String[] doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String subredditJsonString = null;


        try{
            final String SUBREDDIT_HOT_BASE_URL =
                    "http://www.reddit.com/r/ProgrammerHumor/hot.json";

            Uri builtURi = Uri.parse(SUBREDDIT_HOT_BASE_URL).buildUpon().build();
            URL url = new URL(builtURi.toString());

            //Create the request to Reddit and open the connection
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Read th input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if(inputStream == null){
                //Nothing to do
                Log.d(TAG,"Nothing to display, InputStream is Null");
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while((line = reader.readLine()) != null){
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging alot easier if I print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if(buffer.length() == 0){
                //Stream was empty. No point in parsing.
                Log.d(TAG,"Stream is empty.");
                return null;
            }


            subredditJsonString = buffer.toString();



        }catch (IOException e) {
                Log.e(TAG,"Error getting input",e);
                return null;
        }finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }

            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG,"ERROR Closing Stream",e);
               }
            }
        }

        try {
            return getSubRedditDataFromJson(subredditJsonString);
        } catch (JSONException e) {
            Log.e(TAG,"Error getting data",e);
        }

        return null;


    }


    @Override
    protected void onPostExecute(String[] result) {
        if(result != null){

        }
    }

    private String[] getSubRedditDataFromJson(String subredditJsonStr) throws JSONException{

        //These are the names of the JSON objects that need to be extracted.
        final String JSON_CHILDREN = "children";
        final String JSON_TITLE = "title";
        final String JSON_DATA = "data";

        JSONObject subredditJson = new JSONObject(subredditJsonStr);
        JSONArray childrenArray = subredditJson.getJSONArray(JSON_CHILDREN);

        String[] resultStr = new String[childrenArray.length()];

        for(int i = 0; i < childrenArray.length(); i++){
            String title;

            JSONObject data = childrenArray.getJSONObject(i);
            title = data.getString(JSON_TITLE);

            resultStr[i] = title;

        }

        return resultStr;
    }
}
