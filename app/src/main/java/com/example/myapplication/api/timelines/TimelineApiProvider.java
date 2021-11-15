package com.example.myapplication.api.timelines;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.cluster.MyItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TimelineApiProvider {
    String backend_url;
    final String path = "/api/timelines";
    Context context;

    public TimelineApiProvider(Context context) {
        this.context = context;
        backend_url = context.getResources().getString(R.string.backend_url);
    }

    public void getTimelinesInRadius(double latitude, double longitude, double radius, int pastDays,Response.Listener<JSONArray> callback, Response.ErrorListener errorCallback) {
        StringBuilder getURL = new StringBuilder(backend_url).append(path)
                .append("/?lat=").append(latitude)
                .append("&lon=").append(longitude)
                .append("&radius=").append(radius);
        if (pastDays != -1) {
            getURL.append("&past-days=").append(pastDays);
        }
        Log.i("search", new String(getURL));
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, new String(getURL), null, callback, errorCallback);
        Volley.newRequestQueue(context).add(jsonArrayRequest);
    }

    public void getTimelines(Response.Listener<JSONArray> callback, Response.ErrorListener errorCallback) {
        StringBuilder getURL = new StringBuilder(backend_url).append(path);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, new String(getURL), null, callback, errorCallback);
        Volley.newRequestQueue(context).add(jsonArrayRequest);
    }

    /**
     * Get timelines by android id
     * @param user_id android id
     * @param callback response with JSONArray callback - [JSONObject,...] object with id, date, address
     * @param errorCallback error callback - handle the error
     */

    public void getTimelinesByUserId(String user_id, Response.Listener<JSONArray> callback, Response.ErrorListener errorCallback) {
        StringBuilder getURL = new StringBuilder(backend_url).append(path)
                .append("/").append(user_id);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, new String(getURL), null, callback, errorCallback);
        Volley.newRequestQueue(context).add(jsonArrayRequest);
    }

    /**
     * Create a timeline
     * @param user_id android id
     * @param date  date format yyyy-mm-dd
     * @param address
     * @param latitude
     * @param longitude
     * @param callback callback with JSONObject returning id, uid, date, address, latitude, longitude
     * @param errorCallback error callback
     */
    public void createTimeline(String user_id, String date, String address, double latitude, double longitude, Response.Listener<JSONObject> callback, Response.ErrorListener errorCallback) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("uid",user_id);
            postData.put("date", date);
            postData.put("address",address);
            postData.put("latitude", latitude);
            postData.put("longitude", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringBuilder postURL = new StringBuilder(backend_url).append(path);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, new String(postURL), postData, callback,errorCallback) {
            @Override
            public String getBodyContentType()
            {
                return "application/json";
            }
        };

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    /**
     * Updating a timeline by its id
     * @param id timeline id
     * @param date yyyy-mm-dd - new date
     * @param address new address
     * @param latitude new latitude
     * @param longitude new longitude
     * @param callback callback with JSONObject returning id, date, address, latitude, longitude
     * @param errorCallback
     */

    public void updateTimelineById(String id,String date, String address, double latitude, double longitude, Response.Listener<JSONObject> callback, Response.ErrorListener errorCallback) {
        JSONObject putData = new JSONObject();
        try {
            putData.put("date", date);
            putData.put("address",address);
            putData.put("latitude", latitude);
            putData.put("longitude", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringBuilder putURL = new StringBuilder(backend_url).append(path)
                .append("/").append(id);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, new String(putURL), putData, callback,errorCallback);
        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    /**
     * Delete a timeline by its id
     * @param id timeline id
     * @param callback success callback
     * @param errorCallback error callback
     */

    public void deleteTimelineById(String id, Response.Listener<String> callback, Response.ErrorListener errorCallback) {
        StringBuilder deleteURL = new StringBuilder(backend_url).append(path)
                .append("/").append(id);
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, new String(deleteURL), callback, errorCallback);
        Volley.newRequestQueue(context).add(stringRequest);
    }
}
