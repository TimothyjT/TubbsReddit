package com.android.finalproject.tubbsreddit;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.android.finalproject.tubbsreddit.constants.Constants;

/**
 * Created by Tubbster on 12/30/14.
 */
public class WebViewActivity extends ActionBarActivity {
     private static String mUrl;
    //test
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);

        getSupportActionBar().setHomeButtonEnabled(true);

        if(getIntent().getExtras() != null){
                mUrl = getIntent().getExtras().getString(Constants.KEY_URL_WEBVIEW);
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.webview_container, WebViewFragment.newInstance(mUrl))
                    .commit();
        }
    }
}
