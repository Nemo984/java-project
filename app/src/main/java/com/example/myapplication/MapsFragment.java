package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.api.covid.CovidApi;
import com.example.myapplication.api.JsonReader;
import com.example.myapplication.api.covid.ProvinceLocationHashMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class MapsFragment extends Fragment {

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng sydney = new LatLng(-34, 151);
            LatLng bangkok = new LatLng(13.7563,100.5018);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bangkok,7f));
            try {
                mapDailyCases(googleMap);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private void mapDailyCases(GoogleMap googleMap) throws JSONException, IOException {

        new AsyncTask<String, Integer, Void>(){
            JSONArray json;
            @Override
            protected Void doInBackground(String... params) {
                try {
                    json = JsonReader.readJsonArrayFromUrl(CovidApi.TODAY_CASES_PROVINCES);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                HashMap<String,Double[]> provinceLocationMap = ProvinceLocationHashMap.getMap();
                for(int n = 0; n < json.length(); n++) {
                    try {
                        JSONObject Object = json.getJSONObject(n);
                        String province = Object.getString("province");
                        if (province.equals("ไม่ระบุ")) {
                            continue;
                        }
                        Double[] coordinates = provinceLocationMap.get(province);
                        LatLng provincePos = new LatLng(coordinates[0], coordinates[1]);

                        int new_case = Object.getInt("new_case");
                        int new_death = Object.getInt("new_death");
                        int total_case = Object.getInt("total_case");
                        int total_death = Object.getInt("total_death");
                        String update_date = Object.getString("update_date");
                        System.out.println("new cases: " + new_case + " new deaths" + new_death);
                        googleMap.addMarker(new MarkerOptions().position(provincePos).title(province));
                        // do some stuff....
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}