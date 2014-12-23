package com.android.finalproject.tubbsreddit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;


public class WebViewFragment extends Fragment {
    public static final String ARG_URL = "url";

    private String mURL;


    public static WebViewFragment newInstance(String mUrl) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL,mUrl);
        fragment.setArguments(args);
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


     WebView myWebView = (WebView)rootView.findViewById(R.id.webview);
     WebSettings webSettings = myWebView.getSettings();
     webSettings.setJavaScriptEnabled(true);
     webSettings.setLoadWithOverviewMode(true);
     webSettings.setUseWideViewPort(true);
     myWebView.loadUrl(mURL);


     return rootView;
    }



}
