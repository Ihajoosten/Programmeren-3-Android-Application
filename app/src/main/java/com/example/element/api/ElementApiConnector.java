package com.example.element.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.example.element.adapter.ElementAdapter;
import com.example.element.models.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static android.content.ContentValues.TAG;

public class ElementApiConnector extends AsyncTask<String, Void, String> {

    private ElementAdapter listener;
    private ProgressDialog pg;
    private String Taal;
    private Context context;


    // All JSON attributes
    private final static String IDENTIFICATION = "IDENTIFICATIE";
    private final static String TITLE = "AANDUIDINGOBJECT";
    private final static String GEO = "GEOGRAFISCHELIGGING";
    private final static String ARTIST = "KUNSTENAAR";
    private final static String MATERIAL = "MATERIAAL";
    private final static String DESCRIPTION = "OMSCHRIJVING";
    private final static String UNDERLAYER = "ONDERGROND";
    private final static String IMAGEURL = "URL";

    public ElementApiConnector(ElementAdapter listener, ProgressDialog dialog, String taal) {
        this.listener = listener;
        this.pg = dialog;
        this.Taal = taal;
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground aangeroepen");
        InputStream inputStream = null;
        int responsCode = -1;
        //De URL die we via de .execute() meegeleverd krijgen
        String elementUrl = strings[0];
        //Het resultaat dat we gaan retourneren
        String response = "";

        try {
            //Maak een URL object
            URL url = new URL(elementUrl);
            //Open een connection op de URL
            URLConnection urlConnection = url.openConnection();

            if (!(urlConnection instanceof HttpURLConnection)) {
                return null;
            }

            //Initialiseer een HTTP connectie
            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
            httpConnection.setAllowUserInteraction(false);
            httpConnection.setInstanceFollowRedirects(true);
            httpConnection.setRequestMethod("GET");

            //Voer het request uit via de HTTP connectie op de URL
            httpConnection.connect();

            //Kijk of het gelukt is door de response code te checken
            responsCode = httpConnection.getResponseCode();
            if (responsCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpConnection.getInputStream();
                response = getStringFromInputStream(inputStream);
            } else {
            }
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }

        //Hier eindigt deze methode
        //Het resultaat gaat naar de onPostExecute methode

        Log.i(TAG, "doInBackground: " + response);

        return response;
    }

    protected void onPostExecute(String response) {
        Log.d(TAG, "onPostExecute aangeroepen");

        if (pg.isShowing()) {
            pg.dismiss();
        }

        // Check of er een response is
        if(response == null || response == "") {
            Log.e(TAG, "No response");

            return;
        }

        //Het resultaat is in ons geval een stuk tekst in JSON formaat
        //Daar moeten we de info die we willen tonen uit filteren (parsen)
        //Dat kan met een JSONObject

        try {
            // Topniveau json-object
            JSONObject object = new JSONObject(response);
            JSONArray features = object.getJSONArray("features");

            //Haal alle Elementen op en start een lus

            for(int id = 0; id < features.length(); id++) {
                //Objecten op features arrayniveau en Elementen ophalen
                JSONObject element = features.getJSONObject(id);

                JSONObject attributes = element.getJSONObject("attributes");

                Log.d(TAG, "Object info: " + attributes);


                String identification = attributes.getString(IDENTIFICATION);
                String title = attributes.getString(TITLE);
                String geo = attributes.getString(GEO);
                String artist = attributes.getString(ARTIST);
                String description = attributes.getString(DESCRIPTION);
                String material = attributes.getString(MATERIAL);
                String underLayer = attributes.getString(UNDERLAYER);
                String imageUri = attributes.getString(IMAGEURL);

                // Retrieve geo location lat lon
                JSONObject geometry = element.getJSONObject("geometry");
                double lat = geometry.getDouble("x");
                double lon = geometry.getDouble("y");

                Log.e(TAG, "Coordinaten zijn: " + lat + lon );

                //Get first image url
                String theFullUrl = "https://gis.breda.nl/documenten/sierende-elementen/" + identification + ".jpg";


                //Maak een nieuw Element object
                Element e = new Element(identification, title, geo, artist, description, material, underLayer, imageUri, lat, lon);

                e.setTitle(title);
                e.setGeographicalLocation(geo);
                e.setIdentification(identification);
                e.setArtist(artist);
                e.setDescription(description);
                e.setMaterial(material);
                e.setUnderLayer(underLayer);
                e.setLatitude(lat);
                e.setLongitude(lon);
                e.setImageUri(theFullUrl);

                Log.i(TAG, e.toString());

                listener.elementAdapter(e);

            }
        } catch( JSONException e) {
            Log.e(TAG, "onPostExecute: " + e);
            e.printStackTrace();
        }
    }

    // Data uitlezen uit de inputstream
    // BufferedReader = efficient lezen van karakters
    private static String getStringFromInputStream(InputStream is) {
        Log.d(TAG, "GetStringFromInputStream aangeroepen: " + is);

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            Log.e(TAG, "GetStringFromInputStream: " + e);
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Log.e(TAG, "GetStringFromInputStream IOException: " + e);
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    @Override
    protected void onPreExecute() {
        Log.e(TAG, "onPreExecute aangeroepen ");
        pg.setMessage("Update Elements...");
        pg.show();
    }
}
