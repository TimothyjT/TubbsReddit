package com.android.finalproject.tubbsreddit;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.finalproject.tubbsreddit.constants.Constants;
import com.android.finalproject.tubbsreddit.models.SubRedditListingModel;

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
    private SubRedditListAdapter mSubRedditAdapter;
    private ListView mSubRedditList;

    private ArrayList<String> mSubRedditLinks;
    private String mSubRedditLink;

    public String mSubReddit;

    public ActionBar mActionBar;

    private SubRedditListingModel mListingModel;

    private Button mBeforeButton;
    private Button mNextButton;

    public static Bitmap mBitmap;

    private String mBeforeId;
    private String mAfterId;

    //Simple boolean to figure out which Id to use for buttons.
    private Boolean mWhichId;

    private int mCount = 1;



    public SubRedditFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mSubReddit = getArguments().getString(Constants.KEY_SUB_REDDIT);
            getSubReddit(getArguments().getString(Constants.KEY_SUB_REDDIT), Constants.HOT_CATEGORY);
        } else {
            mSubReddit = Constants.ALL_SUBREDDITS;
            getSubReddit(Constants.ALL_SUBREDDITS, Constants.HOT_CATEGORY);
        }

        mActionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        mActionBar.setTitle(mSubReddit);
        mActionBar.setSubtitle(Constants.HOT_CATEGORY);




    }

    public static SubRedditFragment newInstance(String subReddit) {
        SubRedditFragment frag = new SubRedditFragment();
        Bundle args = new Bundle();
        args.putString(Constants.KEY_SUB_REDDIT, subReddit);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public void onResume() {
        if (getArguments() != null) {
            mSubReddit = getArguments().getString(Constants.KEY_SUB_REDDIT);
            getSubReddit(getArguments().getString(Constants.KEY_SUB_REDDIT), Constants.HOT_CATEGORY);
        } else {
            mSubReddit = Constants.ALL_SUBREDDITS;
            getSubReddit(Constants.ALL_SUBREDDITS, Constants.HOT_CATEGORY);
        }
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //This button when pressed will launch another AsyncTask that will go back in SubRedditListings

        mBeforeButton = (Button)rootView.findViewById(R.id.sub_reddit_before_button);
        mBeforeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mCount == 1){
                        Toast.makeText(getActivity(),"There are no previous listings.",Toast.LENGTH_SHORT).show();
                }else {
                    mCount--;
                    mWhichId = true;
                    getSubReddit(mSubReddit,Constants.HOT_CATEGORY);
                }



            }
        });




        //This button when pressed will launch another AsyncTask to load the next page of SubRedditListings
        mNextButton = (Button) rootView.findViewById(R.id.sub_reddit_next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAfterId == "null"){
                    Toast.makeText(getActivity(),"These are no current listings after these.",Toast.LENGTH_SHORT);
                }else {
                    mCount++;
                    mWhichId = false;
                    getSubReddit(mSubReddit,Constants.HOT_CATEGORY);
                }



            }
        });

        // Now that we have some dummy subreddit data, lets create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy subreddit data) and
        // use it to populate the ListView it's attached to.
        mSubRedditAdapter = new SubRedditListAdapter(getActivity()
                , R.layout.sub_reddit_list_view,
                new ArrayList<SubRedditListingModel>());
        mSubRedditList = (ListView) rootView.findViewById(R.id.subreddit_list_view);
        mSubRedditList.setAdapter(mSubRedditAdapter);

        mSubRedditList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mSubRedditLinks != null) {
                    mSubRedditLink = mSubRedditLinks.get(position);
                    mListingModel = (SubRedditListingModel) mSubRedditList.getItemAtPosition(position);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    SubRedditPostFragment mPostFragment = SubRedditPostFragment.newInstance(mSubReddit, mListingModel.getmId(), mListingModel.getmTitle(), mListingModel.getmUrl());
                    ft.replace(R.id.container, mPostFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {

            case R.id.action_hot:
                getSubReddit(mSubReddit, Constants.HOT_CATEGORY);
                mActionBar.setTitle(mSubReddit);
                mActionBar.setSubtitle("Hot");
                break;
            case R.id.action_new:
                getSubReddit(mSubReddit, Constants.NEW_CATEGORY);
                mActionBar.setSubtitle(mSubReddit);
                mActionBar.setSubtitle("New");
                break;
            case R.id.action_top:
                getSubReddit(mSubReddit, Constants.TOP_CATEGORY);
                mActionBar.setTitle(mSubReddit);
                mActionBar.setSubtitle("Top");
                break;

        }


        return super.onOptionsItemSelected(item);


    }

    public class FetchSubReddit extends AsyncTask<String, Void, SubRedditListingModel[]> {
        private final String TAG = FetchSubReddit.class.getSimpleName();

        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(getActivity(), null, "Loading...", true, true);

        }

        @Override
        protected SubRedditListingModel[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String subredditJsonString = null;
            Uri builtURi;
            String redditAmount = "t";
            String all = "all";
            String before = "before";
            String after = "after";
            String limit = "limit";
            String count = "count";
            try {
                final String SUBREDDIT_HOT_BASE_URL =
                        "http://www.reddit.com/r/";
                final String FORMAT_JSON = ".json";
                String SUBREDDIT = params[0];
                String SUBREDDIT_CATEGORY = params[1];


                if(mWhichId != null && mWhichId == false) {
                    Log.i(TAG,"inside if for after Id");
                    builtURi = Uri.parse(SUBREDDIT_HOT_BASE_URL).buildUpon()
                            .appendPath(SUBREDDIT)
                            .appendPath(SUBREDDIT_CATEGORY)
                            .appendPath(FORMAT_JSON)
                            .appendQueryParameter(after,mAfterId)
                            .appendQueryParameter(count, "25")
                            .appendQueryParameter(limit, "10")
                            .build();
                }else if(mWhichId != null && mWhichId == true){
                    Log.i(TAG,"inside before id if");
                    builtURi = Uri.parse(SUBREDDIT_HOT_BASE_URL).buildUpon()
                            .appendPath(SUBREDDIT)
                            .appendPath(SUBREDDIT_CATEGORY)
                            .appendPath(FORMAT_JSON)
                            .appendQueryParameter(before,mBeforeId)
                            .appendQueryParameter(count, "25")
                            .appendQueryParameter(limit, "10")
                            .build();
                }else{
                    builtURi = Uri.parse(SUBREDDIT_HOT_BASE_URL).buildUpon()
                            .appendPath(SUBREDDIT)
                            .appendPath(SUBREDDIT_CATEGORY)
                            .appendPath(FORMAT_JSON)
                            .appendQueryParameter(count, "25")
                            .appendQueryParameter(limit, "10")
                            .build();
                }







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
        protected void onPostExecute(SubRedditListingModel[] result) {
            mProgressDialog.dismiss();

            if (result != null) {
                if (mSubRedditAdapter != null) {
                    mSubRedditAdapter.clear();

                    for (SubRedditListingModel data : result) {
                        mSubRedditAdapter.add(data);
                    }

                }
            }
        }

        private SubRedditListingModel[] getSubRedditObjectFromJson(String subredditJsonStr) throws JSONException {

            //These are the names of the JSON objects that need to be extracted.
            final String JSON_CHILDREN = "children";
            final String JSON_TITLE = "title";
            final String JSON_PERMA_LINK = "permalink";
            final String JSON_DATA = "data";
            final String JSON_AUTHOR = "author";
            final String JSON_COMMENTS = "num_comments";
            final String JSON_THUMBNAIL = "thumbnail";
            final String JSON_ID = "id";
            final String JSON_URL = "url";
            final String JSON_AFTER = "after";
            final String JSON_BEFORE = "before";


            JSONObject subredditJson = new JSONObject(subredditJsonStr);


            JSONObject subredditJsonData = subredditJson.getJSONObject(JSON_DATA);

            mBeforeId = subredditJsonData.getString(JSON_BEFORE);
            mAfterId = subredditJsonData.getString(JSON_AFTER);




            JSONArray childrenArray = subredditJsonData.getJSONArray(JSON_CHILDREN);

            SubRedditListingModel[] resultObjs = new SubRedditListingModel[childrenArray.length()];
            mSubRedditLinks = new ArrayList<String>();


            for (int i = 0; i < childrenArray.length(); i++) {
                mListingModel = new SubRedditListingModel();

                JSONObject object = childrenArray.getJSONObject(i);

                JSONObject data = object.getJSONObject(JSON_DATA);



                mListingModel.setmTitle(data.getString(JSON_TITLE));
                mListingModel.setmAuthor(data.getString(JSON_AUTHOR));
                mListingModel.setmComments(data.getInt(JSON_COMMENTS));
                mListingModel.setmId(data.getString(JSON_ID));
                mListingModel.setmUrl(data.getString(JSON_URL));
                mListingModel.setmImageView(LoadImageFromUrl(data.getString(JSON_THUMBNAIL)));


                mSubRedditLinks.add(data.getString(JSON_PERMA_LINK));
                resultObjs[i] = mListingModel;

            }

            return resultObjs;
        }
    }


    public void getSubReddit(String subReddit, String subRedditCategory) {
        new FetchSubReddit().execute(subReddit,subRedditCategory);

     }


    public static Bitmap LoadImageFromUrl(String url) {
        try {
            InputStream in = (InputStream) new URL(url).getContent();
            mBitmap = BitmapFactory.decodeStream(in);
            return mBitmap;
        } catch (Exception e) {
            Log.d(TAG, "Error getting image");
            return null;
        }
    }


    public static void logInfo(String str){
        if(str.length() > 4000){
            Log.i(TAG,str.substring(0,4000));
            logInfo(str.substring(4000));
        }else{
            Log.i(TAG,str);
        }
    }


}