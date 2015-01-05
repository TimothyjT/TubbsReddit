package com.android.finalproject.tubbsreddit;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.finalproject.tubbsreddit.models.SubRedditListingModel;

import java.util.List;

/**
 * Created by Tubbster on 12/23/14.
 */
public class SubRedditListAdapter extends ArrayAdapter<SubRedditListingModel> {
    private static final String TAG = SubRedditListAdapter.class.getSimpleName();

    public SubRedditListAdapter(Context context, int resource, List<SubRedditListingModel> objects) {
        super(context, resource, objects);
    }

    private class ViewHolder {
        ImageView imageView;
        TextView mTitle;
        TextView mAuthor;
        TextView mComments;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        SubRedditListingModel mItem = getItem(position);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.sub_reddit_list_view, null);
            holder = new ViewHolder();

            holder.mTitle = (TextView) convertView.findViewById(R.id.sub_reddit_title);
            holder.mAuthor = (TextView) convertView.findViewById(R.id.sub_reddit_author);
           holder.imageView = (ImageView) convertView.findViewById(R.id.sub_reddit_thumbnail);
            holder.mComments = (TextView) convertView.findViewById(R.id.sub_reddit_comments);



        if(mItem.getmImageView() != null) {
            holder.imageView.setImageBitmap(mItem.getmImageView());
        }
        holder.mTitle.setText(mItem.getmTitle());
        holder.mAuthor.setText("Author: " + mItem.getmAuthor());
        holder.mComments.setText("Comments: " +Integer.toString(mItem.getmComments()));


        return convertView;


    }



}
