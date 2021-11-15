package com.example.myapplication.News;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;

import java.util.ArrayList;

public class NewsListAdapter extends ArrayAdapter<News> {

    private static final String TAG = "NewsListAdapter";

    private Context mContext;

    int mResource;

    public NewsListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<News> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String title = getItem(position).getTitle();
        String urlimage = getItem(position).getUrlimage();
        String urlnews = getItem(position).getUrlnews();

        News news = new News(title, urlimage, urlnews);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent,false);
        TextView title_textview = (TextView) convertView.findViewById(R.id.title_news);
        title_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlnews));
                mContext.startActivity(browserIntent);
            }
        });
        title_textview.setText(title);
        return convertView;
    }
}
