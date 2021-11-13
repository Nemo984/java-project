package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.api.covid.CovidApi;
import com.example.myapplication.api.covid.ProvinceLocationHashMap;
import com.example.myapplication.api.timelines.TimelineApiProvider;
import com.example.myapplication.cluster.MyItem;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputLayout;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.collections.MarkerManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MapsFragment extends Fragment {

    AutocompleteSupportFragment autocompleteFragment;
    FrameLayout sliderLayout;
    Slider radiusSlider;
    Button searchButton;
    View resetCameraBtn;

    AutoCompleteTextView typeDropdown;
    TextInputLayout dateLayout;
    AutoCompleteTextView dateDropdown;

<<<<<<< Updated upstream

    public final String BACKEND_URL = getActivity().getString(R.string.backend_url);
=======
    public final String BACKEND_URL = "https://764e-125-24-184-202.ngrok.io";
>>>>>>> Stashed changes
    final int DEFAULT_UNIT = 1000;
    String onType = "Cases";

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
            //default location
            LatLngBounds thailandBounds = new LatLngBounds(
                    new LatLng(5.6130380, 97.3433960),
                    new LatLng(20.4651430, 105.6368120)
            );
            googleMap.setPadding(0, 150, 0, 0);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(thailandBounds, 0));
            googleMap.setPadding(0, 0, 0, 0);

            markerManager = new MarkerManager(googleMap);
            provinceMarkers = markerManager.newCollection();
            provinceMarkers.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(@NonNull Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(@NonNull Marker marker) {
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

            //Map each province
            try {
                mapProvinces(googleMap);
            } catch (JSONException | IOException | XmlPullParserException e) {
                e.printStackTrace();
            }

            resetCameraBtn.setOnClickListener(view1 -> {
                googleMap.setPadding(0, 150, 0, 0);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(thailandBounds, 0));
                googleMap.setPadding(0, 0, 0, 0);
            });

            // Set up a PlaceSelectionListener to handle the response -- see this real!!!.
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                private static final String TAG = "auto-complete fragment";

                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    // TODO: Get info about the selected place.
                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + place.getLatLng());
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(place.getViewport(), 100));
                    if (onType.equals("Timelines")) {
                        markerRadiusSelector(googleMap, place.getLatLng());
                    }
                }


                @Override
                public void onError(@NonNull Status status) {
                    // TODO: Handle the error.
                    Log.i(TAG, "An error occurred: " + status);
                }
            });

            //TODO: put timelines / cases into their own setup func.
            //On item selected listener for spinner
            typeDropdown.setOnItemClickListener((adapterView, view, i, l) -> {
                String choice = adapterView.getItemAtPosition(i).toString();
                if (choice.equals("Timelines")) {
                    onType = "Timelines";
                    provinceMarkers.hideAll();
                    sliderLayout.setVisibility(View.VISIBLE);
                    mapTimelines(googleMap);
                    searchButton.setVisibility(View.VISIBLE);
                    dateLayout.setVisibility(View.VISIBLE);
                    // TODO: put this in a func. call together with timelines setup
                    radiusSlider.addOnChangeListener((slider, value, fromUser) -> {
                        if (prevMarker != null && prevCircle == null) {
                            CircleOptions circleOptions = new CircleOptions()
                                    .center(new LatLng(prevMarker.getPosition().latitude, prevMarker.getPosition().longitude))
                                    .radius(DEFAULT_UNIT * value) // In meters
                                    .fillColor(0x33FF0000)
                                    .strokeColor(Color.RED)
                                    .strokeWidth(0);

                            prevCircle = googleMap.addCircle(circleOptions);
                            if (value != 0) {
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        circleOptions.getCenter(), getZoomLevel(prevCircle)));
                            }

                        } else if (prevMarker != null) {
                            prevCircle.setRadius(DEFAULT_UNIT * value);
                            if (value != 0) {
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        prevCircle.getCenter(), getZoomLevel(prevCircle)));
                            }

                        }
                    });

                } else {
                    onType = "Cases";
                    provinceMarkers.showAll();
                    timelinesMarkers.clear();
                    if (prevMarker != null) {
                        prevMarker.remove();
                    }
                    radiusSlider.setValue(0);
                    if (prevCircle != null) {
                        prevCircle.remove();
                    }
                    clusterManager.clearItems();
                    googleMap.setOnMapClickListener(null);
                    searchButton.setVisibility(View.GONE);
                    sliderLayout.setVisibility(View.INVISIBLE);
                    dateLayout.setVisibility(View.INVISIBLE);
                }
            });

            //search button
            // verify + api call
            clusterManager = new ClusterManager<>(getContext(), googleMap);
            searchButton.setOnClickListener(view -> {
                if (prevMarker != null && prevCircle != null && prevCircle.getRadius() > 0) {
                    // Point the map's listeners at the listeners implemented by the cluster
                    // manager.
                    googleMap.setOnCameraIdleListener(clusterManager);
                    googleMap.setOnMarkerClickListener(clusterManager);


                    double latitude = prevMarker.getPosition().latitude;
                    double longitude = prevMarker.getPosition().longitude;
                    double radius = prevCircle.getRadius() / DEFAULT_UNIT;
                    StringBuilder endpoint = new StringBuilder(BACKEND_URL).append("/api/timelines/?lat=").append(latitude)
                            .append("&lon=").append(longitude)
                            .append("&radius=").append(radius);
                    String URL = new String(endpoint);
                    Log.i("search", new String(endpoint));

                    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            for (int n = 0; n < response.length(); n++) {
                                try {
                                    JSONObject Object = response.getJSONObject(n);
                                    String address = Object.getString("address");
                                    String date = Object.getString("date");
                                    double latitude = Object.getDouble("latitude");
                                    double longitude = Object.getDouble("longitude");
                                    clusterManager.addItem(new MyItem(latitude, longitude, address, date));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            clusterManager.cluster();
                        }

                    }, error -> Log.e("Timelines", error.toString()));
                    Volley.newRequestQueue(getContext()).add(jsonArrayRequest);

                }
            });
        }
    };
    ClusterManager<MyItem> clusterManager = null;
    MarkerManager markerManager = null;

    public int getZoomLevel(Circle circle) {
        int zoomLevel = 11;
        if (circle != null) {
            double radius = circle.getRadius() + circle.getRadius() / 2;
            double scale = radius / 500;
            zoomLevel = (int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }

    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    List<Marker> provincesMarkers = new ArrayList<>();
    List<Marker> timelinesMarkers = new ArrayList<>();

    private void mapProvinces(GoogleMap googleMap) throws JSONException, IOException, XmlPullParserException {

        HashMap<String, Double[]> provinceLocationMap = ProvinceLocationHashMap.getMap();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, CovidApi.TODAY_CASES_PROVINCES, null, response -> {
            for (int n = 0; n < response.length(); n++) {
                try {
                    JSONObject Object = response.getJSONObject(n);
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

                    provinceMarkers.addMarker(new MarkerOptions().icon(markerHue)
                            .position(provincePos)
                            .title(province)
                            .snippet(new String(covidInfo)));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> Log.e("Timelines", error.toString()));
        Volley.newRequestQueue(getContext()).add(jsonArrayRequest);

    }

    Marker prevMarker;
    Circle prevCircle;

    public void mapTimelines(GoogleMap googleMap) {
        googleMap.setOnMapClickListener(point -> {
            markerRadiusSelector(googleMap, point);
        });
    }

    public void markerRadiusSelector(GoogleMap googleMap, LatLng point) {
        if (prevMarker != null) {
            prevMarker.remove();
        }
        radiusSlider.setValue(0);
        if (prevCircle != null) {
            prevCircle.remove();
            prevCircle = null;
        }
        prevMarker = googleMap.addMarker(new MarkerOptions().position(point).icon(getMarkerIcon("#800080")));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    MarkerManager.Collection provinceMarkers;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
//        BACKEND_URL = getActivity().getString(R.string.backend_url);

        // Initialize the AutocompleteSupportFragment.
        Places.initialize(getActivity().getApplicationContext(), "AIzaSyAQDtDk9VFC_mTpq16k5PvTvSD-WHC7RLY");

        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return. -->
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.VIEWPORT));

        //set location bounds -> Thailand
        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(15, 101),
                new LatLng(15, 101)
        ));

        autocompleteFragment.setCountry("TH");

        //spinners - drop down menu
        typeDropdown = (AutoCompleteTextView) getActivity().findViewById(R.id.typeDropdown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.map_types, R.layout.dropdown_item);
        adapter.setDropDownViewResource(R.layout.dropdown_item);
        typeDropdown.setAdapter(adapter);

        dateLayout = (TextInputLayout) getActivity().findViewById(R.id.dateLayout);
        dateDropdown = (AutoCompleteTextView) getActivity().findViewById(R.id.dateDropdown);
        ArrayAdapter<CharSequence> dateAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.date_types, R.layout.dropdown_item);
        adapter.setDropDownViewResource(R.layout.dropdown_item);
        dateDropdown.setAdapter(dateAdapter);
        dateLayout.setVisibility(View.INVISIBLE);


        //get slider layout
        sliderLayout = (FrameLayout) getActivity().findViewById(R.id.sliderLayout);
        sliderLayout.setVisibility(View.GONE);


        //get radius slider
        radiusSlider = getActivity().findViewById(R.id.radiusSlider);

        //reset camera btn;
        resetCameraBtn = getActivity().findViewById(R.id.resetCameraBtn);

        searchButton = getActivity().findViewById(R.id.searchButton);

        searchButton.setVisibility(View.INVISIBLE);

        //timeline api provider test
        TimelineApiProvider timelineApiProvider = new TimelineApiProvider(BACKEND_URL, getContext());
        timelineApiProvider.createTimeline("2fa3g", "2020-02-25", "Dreamworld", 131.1142, 151.412, response -> {
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

        timelineApiProvider.deleteTimelineById("c015b498-6ea4-43e8-93a3-bf662787868c", response -> {
            //Deleted OK, do something here...
            Log.i("deleteTimeline","This is deleted");
        }, error -> {
            Log.e("deleteTimeline", error.toString());
        });

        timelineApiProvider.updateTimelineById("85d5508d-7f78-421e-8004-d9ff6af8a6f4","2021-03-13","disney land",453.34312,23.1242, response -> {
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
        }, error -> {
            Log.e("putTimeline", error.toString());
        });

        timelineApiProvider.getTimelinesByUserId("2fa3g", response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject Object = response.getJSONObject(i);
                    String id = Object.getString("id");
                    Log.i("getArray", id);
                    String address = Object.getString("address");
                    Log.i("getArray", address);
                    String date = Object.getString("date");
                    Log.i("getArray", date);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> Log.e("getArray", error.toString()));

    }


}