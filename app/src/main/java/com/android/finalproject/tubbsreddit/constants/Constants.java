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


    //Constant for SunRedditFragment Bundle Arguement
    public static String ARGS_SUB_REDDIT = "subreddit";


    //SubReddits to be selected in Navigation Drawer
    public static ArrayList<String> createSubRedditList(){
        ArrayList<String> subRedditList = new ArrayList<String>();
        subRedditList.add("ProgrammerHumor");
        subRedditList.add("personalfinance");
        subRedditList.add("Showerthoughts");
        subRedditList.add("androiddev");

        return subRedditList;
    }


}
