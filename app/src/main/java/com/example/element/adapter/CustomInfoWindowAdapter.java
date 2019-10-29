package com.example.element.adapter;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.example.element.R;
import com.example.element.models.Element;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

@SuppressLint("Registered")
public class CustomInfoWindowAdapter extends AppCompatActivity implements GoogleMap.InfoWindowAdapter {

    private LayoutInflater layoutInflater;
    private TextView tvTitle;
    private Element element;

    public CustomInfoWindowAdapter(LayoutInflater layoutInflater, Element element) {
        this.layoutInflater = layoutInflater;
        this.element = element;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        // Weergave van het lay-outbestand custom_info_content.xml
        @SuppressLint("InflateParams") View v = layoutInflater.inflate(R.layout.activity_custom_info_content, null);

        tvTitle = v.findViewById(R.id.gmAddress);
        tvTitle.setText(element.getGeographicalLocation());

        return v;
    }
}
