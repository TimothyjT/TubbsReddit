package com.android.finalproject.tubbsreddit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.finalproject.tubbsreddit.constants.Constants;
import com.android.finalproject.tubbsreddit.models.SubRedditPostModel;

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

public  class SubRedditPostFragment extends Fragment {
    private final static String TAG = SubRedditPostFragment.class.getSimpleName();

    //Needed for building Uri
    private String mSubReddit;
    private String mId;
    private String mTitle;
    private ImageView mImageView;
    private String mUrl;
//test



    private ArrayList<String> resultComments;



    private ArrayAdapter<String> mCommentsAdapter;
    private ListView mSubRedditPostComments;


    
    private SubRedditPostModel mSubRedditPostModel;
    public SubRedditPostFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSubReddit = getArguments().getString(Constants.KEY_SUB_REDDIT);
            mId = getArguments().getString(Constants.KEY_SUB_REDDIT_ID);
            mTitle = getArguments().getString(Constants.KEY_SUB_REDDIT_TITLE);
            mUrl = getArguments().getString((Constants.KEY_SUB_REDDIT_URL));
            new FetchSubRedditPost().execute(mSubReddit,mId,mTitle);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sub_reddit_post, container, false);

        TextView mTitlePost = (TextView)rootView.findViewById(R.id.post_text);

        if(getArguments() != null){
            mTitlePost.setText(mTitle);
        }

        mTitlePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),WebViewActivity.class);
                i.putExtra(Constants.KEY_URL_WEBVIEW,mUrl);
                startActivity(i);
            }
        });


        mCommentsAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1,
                new ArrayList<String>());

        mSubRedditPostComments = (ListView) rootView.findViewById(R.id.comments_list);
        mSubRedditPostComments.setAdapter(mCommentsAdapter);
        return rootView;
    }




    public static SubRedditPostFragment newInstance(String subreddit, String Id, String title,String url) {
        SubRedditPostFragment frag = new SubRedditPostFragment();
        Bundle args = new Bundle();
        args.putString(Constants.KEY_SUB_REDDIT, subreddit);
        args.putString(Constants.KEY_SUB_REDDIT_ID, Id);
        args.putString(Constants.KEY_SUB_REDDIT_TITLE, title);
        Log.i(TAG,"url..." + url);
        args.putString(Constants.KEY_SUB_REDDIT_URL,url);
        frag.setArguments(args);
        return frag;

    }


    public class FetchSubRedditPost extends AsyncTask<String, Void, String[]> {
        private final String TAG = FetchSubRedditPost.class.getSimpleName();
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(getActivity(),null,"Loading...",true);
        }

        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String subredditJsonString = null;
            Uri builtURi;



            try {
                final String SUBREDDIT_HOT_BASE_URL =
                        "http://www.reddit.com/r/";
                final String FORMAT_JSON = ".json";
                String SUBREDDIT = params[0];
                String SUBREDDIT_ID = params[1];
                String SUBREDDIT_TITLE = params[2];
                String limit = "limit";
                String amount = "500";

                builtURi = Uri.parse(SUBREDDIT_HOT_BASE_URL).buildUpon()
                        .appendPath(SUBREDDIT)
                        .appendPath("comments")
                        .appendPath(SUBREDDIT_ID)
                        .appendPath(SUBREDDIT_TITLE)
                        .appendPath(FORMAT_JSON)
                        .appendQueryParameter(limit,amount)
                        .build();


                URL url = new URL(builtURi.toString());

                //Create the request to Reddit and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Read th input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    //Nothing to do
                    Log.d(TAG, "Nothing to display, InputStream is Null");
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging alot easier if I print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    //Stream was empty. No point in parsing.
                    Log.d(TAG, "Stream is empty.");
                    return null;
                }


                subredditJsonString = buffer.toString();


            } catch (IOException e) {
                Log.e(TAG, "Error getting input", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "ERROR Closing Stream", e);
                    }
                }
            }

            try {
                return getSubRedditObjectFromJson(subredditJsonString);
            } catch (JSONException e) {
                Log.e(TAG, "Error getting data", e);
            }

            return null;


        }


        @Override
        protected void onPostExecute(String[] result) {
            mProgressDialog.dismiss();
            if (result != null) {
                if (result != null) {
                    mCommentsAdapter.clear();
                    for (String comment: result) {
                        mCommentsAdapter.add(comment);
                    }

                }
            }
        }

        private String[] getSubRedditObjectFromJson(String subredditJsonStr) throws JSONException {

            //These are the names of the JSON objects that need to be extracted.
            final String JSON_CHILDREN = "children";
            final String JSON_DATA = "data";
            final String JSON_BODY = "body";
            final String JSON_TITLE = "title";




            JSONArray subredditJson = new JSONArray(subredditJsonStr);
            resultComments = new ArrayList<>();
            for(int i = 0; i < subredditJson.length();i++){
                JSONObject obj = subredditJson.getJSONObject(i);
                JSONObject data = obj.getJSONObject(JSON_DATA);
                JSONArray array = data.getJSONArray(JSON_CHILDREN);
                for(int v = 0; v < array.length(); v++){
                    JSONObject childData = array.getJSONObject(v);
                    JSONObject dataObj = childData.getJSONObject(JSON_DATA);
                    if(dataObj.has(JSON_BODY)){
                        resultComments.add(dataObj.getString(JSON_BODY));
                    }

                }
            }
            String[] result = resultComments.toArray(new String[resultComments.size()]);
            return result;
        }
    }

//
//    public SubRedditPostModel createModelObject(String title){
//        SubRedditPostModel mPostModel = new SubRedditPostModel();
//        mPostModel.setmTitle(title);
//        return mPostModel;
//    }

}