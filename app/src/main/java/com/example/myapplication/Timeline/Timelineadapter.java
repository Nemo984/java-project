package com.example.myapplication.Timeline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;

import java.util.ArrayList;

public class Timelineadapter extends ArrayAdapter<Timeline> {
    private Context mContext;
    int mResource;
    public Timelineadapter(@NonNull Context context, int resource, @NonNull ArrayList<Timeline> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getName();
        String Date = getItem(position).getDate();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView tvName = (TextView) convertView.findViewById(R.id.TextName);
        TextView tvDate = (TextView) convertView.findViewById(R.id.TextDate);

        tvName.setText(name);
        tvDate.setText(Date);

        return convertView;
    }
}
