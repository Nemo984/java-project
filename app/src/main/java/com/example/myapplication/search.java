package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.api.timelines.TimelineApiProvider;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class search extends Fragment  {
    public static ArrayList<Timeline> histroy= new ArrayList<>();
    public static Timelineadapter adapter;
    boolean isloadted = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container,false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = (ListView) view.findViewById(R.id.view1);
        adapter = new Timelineadapter(getActivity(),
                R.layout.adapter_view,histroy);
        listView.setAdapter(adapter);
        if(histroy.isEmpty()){
            load_timeline(getContext());
        }

        /*if(!isloadted){
            load_timeline(getContext());
            isloadted = true;
        }*/
    }




    public static void createTimeline(String name, String Date, Double lat, Double Long, Context context){
        Timeline one = new Timeline(name,Date);
        histroy.add(one);
        adapter.notifyDataSetChanged();
        TimelineApiProvider timelineApiProvider = new TimelineApiProvider(context);
        timelineApiProvider.createTimeline(Mainpage.android_id, Date, name, lat, Long, response -> {
            try {
                //stored the id
                String id = response.getString("id");
                Log.i("jsonresponse", id);
                String user_id = response.getString("uid");
                Log.i("jsonresponse", user_id);
                String address = response.getString("address");
                Log.i("jsonresponse", address);
                String date = response.getString("date");
                Log.i("jsonresponse", date);
                double latitude = response.getDouble("latitude");
                Log.i("jsonresponse", String.valueOf(latitude));
                double longitude = response.getDouble("longitude");
                Log.i("jsonresponse", String.valueOf(longitude));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("postTimeline", error.toString()));
    }

    public void load_timeline(Context context){
        TimelineApiProvider timelineApiProvider = new TimelineApiProvider(context);
        timelineApiProvider.getTimelinesByUserId(Mainpage.android_id, response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject Object = response.getJSONObject(i);
                    String id = Object.getString("id");
                    Log.i("getArray", id);
                    String address = Object.getString("address");
                    Log.i("getArray", address);
                    String date = Object.getString("date");
                    Log.i("getArray", date);
                    Timeline one = new Timeline(address,date);
                    histroy.add(one);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> Log.e("getArray", error.toString()));
    }

}
