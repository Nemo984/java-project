package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.api.timelines.TimelineApiProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class search extends Fragment {
    public static ArrayList<Timeline> histroy = new ArrayList<>();
    public static HashMap<Timeline, String> TimelineId = new HashMap<>();
    public static Timelineadapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = (ListView) view.findViewById(R.id.view1);
        adapter = new Timelineadapter(getActivity(),
                R.layout.adapter_view, histroy);
        listView.setAdapter(adapter);
        if (histroy.isEmpty()) {
            load_timeline(getContext());
        }
        listView.setOnItemLongClickListener((adapterView, view1, i, l) -> {
            String Id = TimelineId.get((Timeline) histroy.get(i));
            Log.i("deleteid", Id);
            TimelineId.remove(histroy.get(i));
            delete_timeline(Id);
            adapter.notifyDataSetChanged();
            histroy.remove(i);
            return true;
        });
    }

    /**
     * Delete Timeline
     */
    public void delete_timeline(String id) {
        TimelineApiProvider timelineApiProvider = new TimelineApiProvider(getContext());
        Log.i("id", id);
        timelineApiProvider.deleteTimelineById(id, response -> {
            //Deleted OK, do something here...
            Log.i("deleteTimeline", "This is deleted");
        }, error -> {
            Log.e("deleteTimeline", error.toString());
        });
    }

    /**
     * Create Timeline
     */
    public static void createTimeline(String name, String Date, Double lat, Double Long, Context context) {
        Log.i("postData", Mainpage.android_id);
        Log.i("postData", name);
        Log.i("postData", Date);
        Log.i("postData", String.valueOf(lat));
        Log.i("postData", String.valueOf(Long));

        Timeline one = new Timeline(name, Date);
        histroy.add(one);
        adapter.notifyDataSetChanged();
        TimelineApiProvider timelineApiProvider = new TimelineApiProvider(context);

        timelineApiProvider.createTimeline(Mainpage.android_id, Mainpage.day1, name, lat, Long, response -> {
            try {
                /**
                 * stored the id
                 */
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
                TimelineId.put(one, id);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("postTimeline", error.toString()));
    }

    /**
     * Load Timeline from backend
     */
    public void load_timeline(Context context) {
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
                    Timeline one = new Timeline(address, date);
                    histroy.add(one);
                    TimelineId.put(one, id);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> Log.e("getArray", error.toString()));
    }

}
