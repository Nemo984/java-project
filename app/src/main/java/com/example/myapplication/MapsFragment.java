package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.api.timelines.TimelineApiProvider;
import com.example.myapplication.cluster.MarkerClusterRenderer;
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
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputLayout;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsFragment extends Fragment {

    AutocompleteSupportFragment autocompleteFragment;
    FrameLayout sliderLayout;
    Slider radiusSlider;
    Button searchButton;
    View resetCameraBtn;

    AutoCompleteTextView typeDropdown;
    TextInputLayout dateLayout;
    AutoCompleteTextView dateDropdown;

    public String BACKEND_URL;
    final int DEFAULT_UNIT = 1000; // in meters = 1 km
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
            /**
             * default view bounds
             */
            LatLngBounds thailandBounds = new LatLngBounds(
                    new LatLng(5.6130380, 97.3433960),
                    new LatLng(20.4651430, 105.6368120)
            );
            googleMap.setPadding(0, 150, 0, 0);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(thailandBounds, 0));
            googleMap.setPadding(0, 0, 0, 0);

            //Timelines Heat Map
            addHeatMap(googleMap);

            //reset view button
            resetCameraBtn.setOnClickListener(view1 -> {
                googleMap.setPadding(0, 150, 0, 0);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(thailandBounds, 0));
                googleMap.setPadding(0, 0, 0, 0);
            });

            // Set up a PlaceSelectionListener to handle the response
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                private static final String TAG = "auto-complete fragment";

                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + place.getLatLng());
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(place.getViewport(), 100));
                    if (onType.equals("Search")) {
                        markerRadiusSelector(googleMap, place.getLatLng());
                    }
                }

                @Override
                public void onError(@NonNull Status status) {
                    Log.i(TAG, "An error occurred: " + status);
                }
            });

            //On item selected listener for Type Dropdown
            typeDropdown.setOnItemClickListener((adapterView, view, i, l) -> {
                String choice = adapterView.getItemAtPosition(i).toString();
                if (choice.equals("Search")) {
                    searchTypeSetup(googleMap);
                } else {
                    clusterManager.clearItems();
                    heatMapTypeSetup(googleMap);
                }
            });

            //search button
            // verify + api call
            clusterManager = new ClusterManager<>(getContext(), googleMap);
            clusterManager.setRenderer(new MarkerClusterRenderer(getContext(),googleMap,clusterManager));
            searchButton.setOnClickListener(view -> {
                if (prevMarker != null && prevCircle != null && prevCircle.getRadius() > 0) {
                    // Point the map's listeners at the listeners implemented by the cluster
                    // manager.
                    clusterManager.clearItems();
                    clusterManager.cluster();
                    googleMap.setOnCameraIdleListener(clusterManager);
                    googleMap.setOnMarkerClickListener(clusterManager);
                    double latitude = prevMarker.getPosition().latitude;
                    double longitude = prevMarker.getPosition().longitude;
                    double radius = prevCircle.getRadius() / DEFAULT_UNIT;
                    int days = -1;

                    String pastDays = dateDropdown.getText().toString();
                    if (pastDays.equals("Today")) {
                        days = 1;
                    } else if (pastDays.equals("Last 7 days")) {
                        days = 7;
                    } else if (pastDays.equals("Last 30 days")) {
                        days = 30;
                    }

                    TimelineApiProvider timelineApiProvider = new TimelineApiProvider(getContext());
                     timelineApiProvider.getTimelinesInRadius(latitude,longitude,radius, days, response -> {
                        for (int n = 0; n < response.length(); n++) {
                            try {
                                JSONObject Object = response.getJSONObject(n);
                                String address = Object.getString("address");
                                String date = Object.getString("date");
                                double latitude1 = Object.getDouble("latitude");
                                double longitude1 = Object.getDouble("longitude");
                                clusterManager.addItem(new MyItem(latitude1, longitude1, address, date));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        clusterManager.cluster();
                    }, error -> Log.e("Timelines", error.toString()));

                }
            });
        }
    };

    /**
     * Setup method for search type
     */
    private void searchTypeSetup(GoogleMap googleMap) {
        onType = "Search";
        sliderLayout.setVisibility(View.VISIBLE);
        dateLayout.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.VISIBLE);
        overlay.setVisible(false);

        googleMap.setOnMapClickListener(point -> {
            markerRadiusSelector(googleMap, point);
        });

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
    }

    /**
     * Setup method for Heatmap
     */
    private void heatMapTypeSetup(GoogleMap googleMap) {
        overlay.setVisible(true);
        onType = "Heatmap";
        clusterManager.clearItems();
        clusterManager.cluster();
        if (prevMarker != null) {
            prevMarker.remove();
        }
        radiusSlider.setValue(0);
        if (prevCircle != null) {
            prevCircle.remove();
        }
        googleMap.setOnMapClickListener(null);
        searchButton.setVisibility(View.GONE);
        dateLayout.setVisibility(View.INVISIBLE);
        sliderLayout.setVisibility(View.INVISIBLE);
    }

    /**
     * Method that adds heatmap with date from all existing timelines
     */
    TileOverlay overlay;
    public void addHeatMap(GoogleMap googleMap) {
        List<LatLng> latLngs = new ArrayList<>();

        TimelineApiProvider timelineApiProvider = new TimelineApiProvider(getContext());
        timelineApiProvider.getTimelines(response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject Object = response.getJSONObject(i);
                    double lat = Object.getDouble("latitude");
                    double lon = Object.getDouble("longitude");
                    Log.i("latlon", lat + " " + lon);
                    latLngs.add(new LatLng(lat,lon));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            // Create a heat map tile provider, passing it the latlngs of the police stations.
            HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                    .data(latLngs)
                    .build();
            provider.setRadius(50);

            // Add a tile overlay to the map, using the heat map tile provider.
            overlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
        }, error -> {
            Log.e("heatmap", error.toString());
        });
    }


    ClusterManager<MyItem> clusterManager = null;

    /**
     * return zoom level based relative to circle radius
     */
    public int getZoomLevel(Circle circle) {
        int zoomLevel = 11;
        if (circle != null) {
            double radius = circle.getRadius() + circle.getRadius() / 2;
            double scale = radius / 500;
            zoomLevel = (int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }

    /**
     * return customer marker color from hex code
     */
    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }


    Marker prevMarker;
    Circle prevCircle;

    /**
     *  Marker event listener in search - remove circle and remove previous marker
     */
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


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        BACKEND_URL = getActivity().getString(R.string.backend_url);

        // Initialize the AutocompleteSupportFragment.
        Places.initialize(getActivity().getApplicationContext(), "AIzaSyAQDtDk9VFC_mTpq16k5PvTvSD-WHC7RLY");

        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.VIEWPORT));

        //set location bias and bounds to Thailand
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

        //reset camera btn
        resetCameraBtn = getActivity().findViewById(R.id.resetCameraBtn);

        //search btn
        searchButton = getActivity().findViewById(R.id.searchButton);

        searchButton.setVisibility(View.INVISIBLE);
    }
}