package com.example.element.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.element.R;
import com.example.element.adapter.ElementAdapter;
import com.example.element.adapter.ElementRecyclerViewAdapter;
import com.example.element.api.ElementApiConnector;
import com.example.element.models.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ElementAdapter {

    private ArrayList<Element> elementArrayList;
    private RecyclerView elementRecyclerview;
    private ElementRecyclerViewAdapter elementAdapter = null;

    public SwipeRefreshLayout mySwipeRefreshLayout;
    private String currentLanguage = "nl", currentLang;
    private Menu menu;
    private String url = "";
    private String mMateriaalFilter = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Start de app op de mainactivity. Deze wordt opgehaald uit de map resources (R) in het mapje layout.
        setContentView(R.layout.activity_main);

        elementArrayList = new ArrayList<>();

        elementRecyclerview = findViewById(R.id.rvElement);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        elementRecyclerview.setLayoutManager(llm);

        elementAdapter = new ElementRecyclerViewAdapter(getApplicationContext(), getLayoutInflater(), elementArrayList);
        elementRecyclerview.setAdapter(elementAdapter);
        if(savedInstanceState != null){
            // scroll to existing position which exist before rotation.
            elementRecyclerview.scrollToPosition(savedInstanceState.getInt("position"));
        }


        mySwipeRefreshLayout = findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                elementAdapter.reloadAllData(elementArrayList);
                getData();
            }
        });

        currentLanguage = getIntent().getStringExtra(currentLang);

        Locale myLocale = getResources().getConfiguration().locale;
        if(myLocale.getLanguage().equals("en")){
            setDefaultLanguage("nl");
            currentLanguage = "nl";
        } else {
            setDefaultLanguage("en");
            currentLanguage = "en";
        }

        getData();

    }

    public void getData() {

        if (!mMateriaalFilter.equals("")) {
            this.url = "https://services7.arcgis.com/21GdwfcLrnTpiju8/arcgis/rest/services/Sierende_elementen/FeatureServer/0/query?f=json&outFields=*&where=UPPER(MATERIAAL)%20like%20%27%25" + mMateriaalFilter + "%25%27&inSR=4326";
            elementAdapter.reloadAllData(elementArrayList);
        } else {
            this.url = "https://services7.arcgis.com/21GdwfcLrnTpiju8/arcgis/rest/services/Sierende_elementen/FeatureServer/0/query?where=1%3D1&outFields=*&outSR=4326&f=json";
        }

        mySwipeRefreshLayout.setRefreshing(true);
        try {
            mySwipeRefreshLayout.setRefreshing(false);
            ElementApiConnector getApi = new ElementApiConnector(this, new ProgressDialog(this), currentLanguage);
            getApi.execute(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void elementAdapter(Element blindwall) {
        elementArrayList.add(blindwall);

        Locale myLocale = getResources().getConfiguration().locale;
        if(currentLanguage == "nl"){
            Toast.makeText(getApplicationContext(),"Aantal items: " + elementArrayList.size(), Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getApplicationContext(),"Amount of items: " + elementArrayList.size(), Toast.LENGTH_LONG).show();
        }
        elementAdapter.notifyDataSetChanged();
    }

    public void setDefaultLanguage(String lang){
        // Maak een niew lokaal object aan
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        // Maak een nieuw configuratieobject
        Configuration config = new Configuration();
        // Stel de landinstelling van de nieuwe configuratie in
        config.locale = locale;
        // Werk de configuratie van de toepassingscontext bij
        getResources().updateConfiguration(
                config,
                getResources().getDisplayMetrics()
        );
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.btnLangSwitcher){
            // Refresh activiteit na het klikken op het vlaggetje
            Intent refresh = new Intent(this, MainActivity.class);
            startActivity(refresh);

            finish();
        }

        switch (item.getItemId()) {
            case R.id.label_materiaal_alles:
                mMateriaalFilter = "";
                Log.d("filter", "Geen filter");
                elementAdapter.reloadAllData(elementArrayList);
                mySwipeRefreshLayout.setRefreshing(true);
                getData();
                break;
            case R.id.label_materiaal_steen:
                mMateriaalFilter = "Steen";
                Log.d("filter", "Steen filter");
                getData();
                break;
            case R.id.label_materiaal_staal:
                mMateriaalFilter = "Staal";
                Log.d("filter", "Staal filter");
                getData();
                break;
            case R.id.label_materiaal_metaal:
                mMateriaalFilter = "Metaal";
                Log.d("filter", "Metaal filter");
                getData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        //Verander de vlag in het menu
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu, menu);

        if(currentLanguage.equalsIgnoreCase("en")){
            menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.uk));
        } else {
            menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.dutch));
        }

        MenuItem searchItem = menu.findItem(R.id.action_search);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String userInput = s.toLowerCase();
                List<Element> newList = new ArrayList<>();

                for(Element e : elementArrayList) {

                    if(e.getTitle().contains(userInput)) {
                        newList.add(e);
                    }
                }

                elementAdapter.updateList(newList);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        onRestart();
    }
    @Override
    protected void onPause() {
        super.onPause();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isFinishing();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


}
