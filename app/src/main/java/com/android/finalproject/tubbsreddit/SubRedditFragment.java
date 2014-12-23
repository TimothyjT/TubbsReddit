package com.android.finalproject.tubbsreddit;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.finalproject.tubbsreddit.constants.Constants;

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

public class SubRedditFragment extends Fragment {
        private final static String TAG = SubRedditFragment.class.getSimpleName();
        private ArrayAdapter<String> mSubRedditAdapter;
        private ListView mSubRedditList;

        private ArrayList<String> mSubRedditLinks;
        private String mSubRedditLink;

        public String mSubReddit;

        public ActionBar mActionBar;


    public SubRedditFragment() {

        }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getArguments() != null){
            mSubReddit = getArguments().getString(Constants.ARGS_SUB_REDDIT);
            getSubReddit(getArguments().getString(Constants.ARGS_SUB_REDDIT),Constants.HOT_CATEGORY);
        }else {
            mSubReddit = Constants.ALL_SUBREDDITS;
            getSubReddit(Constants.ALL_SUBREDDITS, Constants.HOT_CATEGORY);
        }

        mActionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        mActionBar.setTitle(mSubReddit);
        mActionBar.setSubtitle(Constants.HOT_CATEGORY);


    }

    public static SubRedditFragment newInstance(String subReddit){
        SubRedditFragment frag = new SubRedditFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ARGS_SUB_REDDIT,subReddit);
        frag.setArguments(args);
        return frag;
    }

    


    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);





            // Now that we have some dummy subreddit data, lets create an ArrayAdapter.
            // The ArrayAdapter will take data from a source (like our dummy subreddit data) and
            // use it to populate the ListView it's attached to.
            mSubRedditAdapter = new ArrayAdapter<String>(getActivity()
                                                         ,android.R.layout.simple_list_item_1,
                                                         android.R.id.text1,
                                                        new ArrayList<String>());
            Log.i(TAG,"mSubRedditAdapater value..." + mSubRedditAdapter);
            mSubRedditList = (ListView)rootView.findViewById(R.id.subreddit_list_view);
            mSubRedditList.setAdapter(mSubRedditAdapter);

            mSubRedditList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(mSubRedditLinks != null) {
                        mSubRedditLink = mSubRedditLinks.get(position);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        WebViewFragment mWebViewFragment = WebViewFragment.newInstance(mSubRedditLink);
                        ft.replace(R.id.container,mWebViewFragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }
            });


            return rootView;
        }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){

            case R.id.action_hot:
                getSubReddit(mSubReddit,Constants.HOT_CATEGORY);
                mActionBar.setTitle(mSubReddit);
                mActionBar.setSubtitle("Hot");
                break;
            case R.id.action_new:
                getSubReddit(mSubReddit,Constants.NEW_CATEGORY);
                mActionBar.setSubtitle(mSubReddit);
                mActionBar.setSubtitle("New");
                break;
            case R.id.action_top:
                getSubReddit(mSubReddit,Constants.TOP_CATEGORY);
                mActionBar.setTitle(mSubReddit);
                mActionBar.setSubtitle("Top");
                break;

        }


        return super.onOptionsItemSelected(item);


    }

    public class FetchSubReddit extends AsyncTask<String,Void,String[]> {
        private final String TAG = FetchSubReddit.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String subredditJsonString = null;
            Uri builtURi;
            String redditAmount = "t";
            String all = "all";


            try{
                final String SUBREDDIT_HOT_BASE_URL =
                        "http://www.reddit.com/r/";
                final String FORMAT_JSON = ".json";
                String SUBREDDIT = params[0];
                String SUBREDDIT_CATEGORY = params[1];
                if(SUBREDDIT_CATEGORY == "top"){
                    builtURi = Uri.parse(SUBREDDIT_HOT_BASE_URL).buildUpon()
                            .appendPath(SUBREDDIT)
                            .appendPath(SUBREDDIT_CATEGORY)
                            .appendPath(FORMAT_JSON)
                            .appendQueryParameter(redditAmount,all)
                            .build();
                }else{
                    builtURi = Uri.parse(SUBREDDIT_HOT_BASE_URL).buildUpon()
                            .appendPath(SUBREDDIT)
                            .appendPath(SUBREDDIT_CATEGORY)
                            .appendPath(FORMAT_JSON).build();
                }

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
                    Log.d(TAG, "Nothing to display, InputStream is Null");
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
            if (result != null) {
                Log.i(TAG, "Result..." + result);
                if (mSubRedditAdapter != null) {
                    mSubRedditAdapter.clear();

                    for (String title : result) {
                        mSubRedditAdapter.add(title);
                    }

                }
            }
        }

        private String[] getSubRedditDataFromJson(String subredditJsonStr) throws JSONException{

            //These are the names of the JSON objects that need to be extracted.
            final String JSON_CHILDREN = "children";
            final String JSON_TITLE = "title";
            final String  JSON_URL= "url";
            final String JSON_DATA = "data";

            JSONObject subredditJson = new JSONObject(subredditJsonStr);
            JSONObject subredditJsonData = subredditJson.getJSONObject(JSON_DATA);
            JSONArray childrenArray = subredditJsonData.getJSONArray(JSON_CHILDREN);

            String[] resultStr = new String[childrenArray.length()];
            mSubRedditLinks = new ArrayList<String>();


            for(int i = 0; i < childrenArray.length(); i++){
                String title;

                JSONObject object = childrenArray.getJSONObject(i);
                JSONObject data = object.getJSONObject(JSON_DATA);
                title = data.getString(JSON_TITLE);

                mSubRedditLinks.add(data.getString(JSON_URL));
                resultStr[i] = title;

            }

            return resultStr;
        }
    }


    public void getSubReddit(String subReddit,String subRedditCategory){
       new FetchSubReddit().execute(subReddit,subRedditCategory);
    }
    }