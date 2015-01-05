package com.android.finalproject.tubbsreddit.constants;

import java.util.ArrayList;

/**
 * Created by Tubbster on 12/22/14.
 */
public class Constants {


    //Category strings for each SubReddit
    public static String TOP_CATEGORY = "top";
    public static String NEW_CATEGORY = "new";
    public static String HOT_CATEGORY = "hot";


    //Default SubReddit to show on first launch which is Front Page
    public static String ALL_SUBREDDITS = "all";


    //Constant for SubRedditFragment Bundle Arguement && SubRedditPostFragment
    public static String KEY_SUB_REDDIT = "subreddit";

    //Constants for SubRedditPostFragment
    public static String KEY_SUB_REDDIT_CATEGORY = "category";
    public static String KEY_SUB_REDDIT_ID = "id";
    public static String KEY_SUB_REDDIT_TITLE = "title";
    public static String KEY_SUB_REDDIT_URL = "url";


    //Constants for intent extras
    public static String KEY_URL_WEBVIEW = "URL";


    //SubReddits to be selected in Navigation Drawer
    public static ArrayList<String> createSubRedditList(){
        ArrayList<String> subRedditList = new ArrayList<String>();
        subRedditList.add("ProgrammerHumor");
        subRedditList.add("personalfinance");
        subRedditList.add("Showerthoughts");
        subRedditList.add("androiddev");
        subRedditList.add("funny");
        subRedditList.add("AdviceAnimals");
        subRedditList.add("pics");
        subRedditList.add("WTF");
        subRedditList.add("MidlyInteresting");
        subRedditList.add("movies");
        subRedditList.add("jokes");


        return subRedditList;
    }


}
