package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.api.covid.CovidApi;
import com.example.myapplication.api.JsonReader;
import com.example.myapplication.api.covid.ProvinceLocationHashMap;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MapsFragment extends Fragment {

    AutocompleteSupportFragment autocompleteFragment;

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
            LatLng bangkok = new LatLng(13.7563, 100.5018);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bangkok, 6f));
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
                mapProvinces(googleMap);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            // Set up a PlaceSelectionListener to handle the response -- see this real!!!.
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                private static final String TAG = "auto-complete fragment";

                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    // TODO: Get info about the selected place.
                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + place.getLatLng()); // Real !!! <- get LatLng
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 10f));
                }


                @Override
                public void onError(@NonNull Status status) {
                    // TODO: Handle the error.
                    Log.i(TAG, "An error occurred: " + status);
                }
            });

            //On item selected listener for spinner
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    String choice = adapterView.getItemAtPosition(position).toString();
                    Toast.makeText(getActivity(),choice,Toast.LENGTH_LONG);
                    if (choice.equals("Timelines")) {
                        showProvincesMarker(false);
                    } else {
                        showProvincesMarker(true);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapter) {}
            });
        }
    };


    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    List<Marker> provincesMarkers = new ArrayList<>();

    private void mapProvinces(GoogleMap googleMap) throws JSONException, IOException {

        new AsyncTask<String, Integer, Void>() {
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
                HashMap<String, Double[]> provinceLocationMap = ProvinceLocationHashMap.getMap();
                for (int n = 0; n < json.length(); n++) {
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


                        BitmapDescriptor markerHue;
                        if (total_case > 100000) {
                            markerHue = getMarkerIcon("#b20000");
                        } else if (total_case > 50000) {
                            markerHue = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                        } else if (total_case > 30000) {
                            markerHue = getMarkerIcon("#FF5733");
                        } else if (total_case > 10000) {
                            markerHue = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                        } else if (total_case > 5000) {
                            markerHue = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                        } else {
                            markerHue = getMarkerIcon("#ff2299");
                        }

                        provincesMarkers.add(
                                googleMap.addMarker(new MarkerOptions().icon(markerHue)
                                .position(provincePos)
                                .title(province)
                                .snippet(new String(covidInfo)))
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();

    }

    public void showProvincesMarker(boolean bool) {
        for (Marker province : provincesMarkers) {
            province.setVisible(bool);
        }
    }

    private Spinner spinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        // Initialize the AutocompleteSupportFragment.
        Places.initialize(getActivity().getApplicationContext(),"AIzaSyAQDtDk9VFC_mTpq16k5PvTvSD-WHC7RLY");

        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return. --> see this real!! -> return Lat_lng
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));

        //set location bounds -> Thailand
        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(15,101),
                new LatLng(15,101)
        ));

        autocompleteFragment.setCountry("TH");

        //spinner - drop down menu
        spinner = (Spinner) getActivity().findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.map_types,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


    }
}