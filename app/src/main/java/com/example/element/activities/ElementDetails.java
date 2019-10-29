package com.example.element.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.element.R;
import com.example.element.models.Element;
import com.example.element.adapter.CustomInfoWindowAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.InputStream;

public class ElementDetails extends AppCompatActivity implements OnMapReadyCallback {
    private Element element;
    private TextView tvTitle;
    private TextView tvGeo;
    private TextView tvArtist;
    private TextView tvUnderground;
    private TextView tvMaterial;
    private TextView tvDescription;
    private ImageView ivElement;

    private MapView mapView;
    private GoogleMap gmap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element_details);

        Intent i = getIntent();
        element = (Element) i.getSerializableExtra("Element");

        ivElement = findViewById(R.id.ivElement);


        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvGeo = (TextView) findViewById(R.id.tvGeo);
        ivElement = (ImageView) findViewById(R.id.ivElement);
        tvArtist = (TextView) findViewById(R.id.tvKunstenaar);
        tvDescription = (TextView) findViewById(R.id.tvBeschrijving);
        tvMaterial = (TextView) findViewById(R.id.tvMateriaal);
        tvUnderground = (TextView) findViewById(R.id.tvOndergrond);

        tvTitle.setText(element.getTitle());
        tvGeo.setText(element.getGeographicalLocation());
        new ImageLoader(ivElement).execute(element.getImageUri());
        tvArtist.setText(element.getArtist());
        tvDescription.setText(element.getDescription());
        tvMaterial.setText(element.getMaterial());
        tvUnderground.setText(element.getUnderLayer());

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }


        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

    }


    @SuppressLint("StaticFieldLeak")
    private class ImageLoader extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public ImageLoader(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bitmap = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(14);
        gmap.setIndoorEnabled(true);
        UiSettings uiSettings = gmap.getUiSettings();
        uiSettings.setIndoorLevelPickerEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(false);

        LatLng latlon = new LatLng(element.getLongitude(), element.getLatitude());

        //Zet de marker weg op de plaats van het sierende element en toon daarbij het adres.
        Marker deMarker = gmap.addMarker(new MarkerOptions()
                .position(latlon)
                .title(element.getGeographicalLocation()));

        deMarker.showInfoWindow();

        CustomInfoWindowAdapter ci = new CustomInfoWindowAdapter(getLayoutInflater(), element);
        gmap.setInfoWindowAdapter(ci);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(latlon));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
