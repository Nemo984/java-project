package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.api.covid.CovidApi;
import com.example.myapplication.api.JsonReader;
import com.example.myapplication.api.covid.ProvinceLocationHashMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    Context mContext = getContext();
                    LinearLayout info = new LinearLayout(mContext);
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(mContext);
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(mContext);
                    snippet.setTextColor(Color.GRAY);
                    snippet.setText(marker.getSnippet());

                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });
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


                        StringBuilder covidInfo = new StringBuilder(100);
                        covidInfo.append("New cases: ").append(new_case)
                                 .append("\nNew deaths: ").append(new_death)
                                 .append("\nTotal cases: ").append(total_case)
                                 .append("\nTotal deaths: ").append(total_death);

                        float markerHue;
                        if (total_case > 100000) {
                            markerHue = BitmapDescriptorFactory.HUE_RED;
                        } else if (total_case > 50000) {
                            markerHue = BitmapDescriptorFactory.HUE_ORANGE;
                        } else if (total_case > 30000) {
                            markerHue = BitmapDescriptorFactory.HUE_YELLOW;
                        } else if (total_case > 10000){
                            markerHue = BitmapDescriptorFactory.HUE_AZURE;
                        } else if (total_case > 5000) {
                            markerHue = BitmapDescriptorFactory.HUE_MAGENTA;
                        } else {
                            markerHue = BitmapDescriptorFactory.HUE_GREEN;
                        }
                        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(markerHue))
                                .position(provincePos)
                                .title(province)
                                .snippet(new String(covidInfo)));
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