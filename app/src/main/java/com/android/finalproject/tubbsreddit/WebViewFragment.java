package com.android.finalproject.tubbsreddit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class WebViewFragment extends Fragment {
    private static final String TAG = WebViewFragment.class.getSimpleName();
    public static final String ARG_URL = "url";

    WebView myWebView;
    private String mURL;


    public static WebViewFragment newInstance(String mUrl) {
        WebViewFragment fragment = new WebViewFragment();
        Log.i(TAG,"mrul..." + mUrl);

        if(mUrl != null) {
            Bundle args = new Bundle();
            args.putString(ARG_URL, mUrl);
            fragment.setArguments(args);
        }
        return fragment;
    }

    public WebViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mURL = getArguments().getString(ARG_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
     View rootView = inflater.inflate(R.layout.webview,container,false);


      myWebView = (WebView)rootView.findViewById(R.id.webview);

     WebSettings webSettings = myWebView.getSettings();
     webSettings.setJavaScriptEnabled(true);
     webSettings.setLoadWithOverviewMode(true);
     webSettings.setUseWideViewPort(true);
     webSettings.setBuiltInZoomControls(true);
     myWebView.setWebViewClient(new WebViewClient());
     myWebView.loadUrl(mURL);











     return rootView;
    }



    public boolean canGoBack(){
        return myWebView.canGoBack();
    }

    public void goBack(){
        myWebView.goBack();
    }







}
