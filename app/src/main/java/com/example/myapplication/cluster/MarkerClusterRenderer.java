package com.example.myapplication.cluster;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class MarkerClusterRenderer extends DefaultClusterRenderer<MyItem> {

    public MarkerClusterRenderer(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected String getClusterText(int bucket) {
        return String.valueOf(bucket);
    }

    @Override
    protected int getBucket(Cluster<MyItem> cluster) {
        return cluster.getSize();
    }
}