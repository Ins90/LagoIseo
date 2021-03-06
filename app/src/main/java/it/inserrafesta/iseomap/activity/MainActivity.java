package it.inserrafesta.iseomap.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import it.inserrafesta.iseomap.ConnectionDetector;
import it.inserrafesta.iseomap.R;

import it.inserrafesta.iseomap.fragment.LinkFragment;
import it.inserrafesta.iseomap.fragment.MapFragment;
import it.inserrafesta.iseomap.fragment.PointFragment;
import it.inserrafesta.iseomap.fragment.StarFragment;
import it.inserrafesta.iseomap.fragment.WaterFragment;

public class MainActivity extends AppCompatActivity {


    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    boolean drawer=false; //var usata per vedere se il drawer e' aperto

    static public MapFragment mapFragment;
    static LinkFragment linkFragment;
    static PointFragment exploreFragment;
    static WaterFragment waterFragment;
    static StarFragment starFragment;
    SharedPreferences prefStar;
    AlertDialog.Builder alert=null;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = getApplicationContext();
        prefStar = context.getSharedPreferences("Stars", Context.MODE_PRIVATE);
        inizializeArrayStars();
        //prefStar.edit().putString("Stars", String.valueOf(location.getLatitude())).apply();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkFirstRun();
        // Initializing Toolbar and setting it as the actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing menu_drawer on item click
                drawerLayout.closeDrawers();
                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        displayMapFragment();
                        return true;

                    case R.id.explore:
                        displayPointFragment();
                        return true;

                    case R.id.star:
                        displayStarFragment();
                        return true;

                    case R.id.classification:
                        displayWaterFragment();
                        return true;

                    case R.id.link:
                        displayLinkFragment();
                        return true;

                    case R.id.settings:
                        startActivity(new Intent(navigationView.getContext(), MyPreferencesActivity.class));
                        return true;

                    case R.id.help:
                        startActivity(new Intent(navigationView.getContext(), HelpActivity.class));
                        //startActivity(new Intent(view.getContext(), HelpActivity.class));
                        return true;

                    case R.id.about:
                        // getSupportActionBar().setTitle(R.string.about_string);
                        startActivity(new Intent(navigationView.getContext(), AboutActivity.class));
                        return true;

                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the menu_drawer closes as we dont want anything to happen so we leave this blank
                drawer=false;
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the menu_drawer open as we dont want anything to happen so we leave this blank
                drawer=true;
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to menu_drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        if (savedInstanceState == null) {
            // on first time display view for first nav item (map of Lake Iseo )
            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            boolean OptSatellite = SP.getBoolean("satellite", false);
            Bundle bundl = new Bundle();
            bundl.putBoolean("OptSatellite", OptSatellite);

            waterFragment = new WaterFragment();
            starFragment = new StarFragment();

            mapFragment = new MapFragment();
            mapFragment.setArguments(bundl);
            linkFragment = new LinkFragment();
            exploreFragment = new PointFragment();
            displayMapFragment();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void inizializeArrayStars() {

    }

    public void checkFirstRun() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun){
            // Place your dialog code here to display the dialog
            checkStatusConnection();
        }
    }

    protected void checkStatusConnection(){
     /*check if is present internet connection */
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        Boolean isInternetPresent = cd.isConnectingToInternet();

        if (!isInternetPresent) {
            // make HTTP requests
            showAlertDialog(getResources().getString(R.string.titleDialog), getResources().getString(R.string.descDialog));
        }else{
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
    }

    public void displayMapFragment() {
        getSupportActionBar().setTitle(R.string.app_name);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mapFragment.isAdded()) { // if the fragment is already in container
            ft.show(mapFragment);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.frame, mapFragment, "MAP");
        }

        if (linkFragment.isAdded()) {
            ft.hide(linkFragment);
        }
        if (exploreFragment.isAdded()) {
            ft.hide(exploreFragment);
        }
        if (waterFragment.isAdded()) {
            ft.hide(waterFragment);
        }
        if (starFragment.isAdded()) {
            ft.hide(starFragment);
        }
        ft.commit();
    }

    protected void displayLinkFragment() {
        getSupportActionBar().setTitle(R.string.link_string);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (linkFragment.isAdded()) { // if the fragment is already in container
            ft.show(linkFragment);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.frame, linkFragment, "LINK");
        }
        if (mapFragment.isAdded()) {
            ft.hide(mapFragment);
        }
        if (exploreFragment.isAdded()) {
            ft.hide(exploreFragment);
        }
        if (waterFragment.isAdded()) {
            ft.hide(waterFragment);
        }
        if (starFragment.isAdded()) {
            ft.hide(starFragment);
        }
        ft.commit();
    }

    protected void displayStarFragment() {
        getSupportActionBar().setTitle(R.string.star_string);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (starFragment.isAdded()) { // if the fragment is already in container
            ft.show(starFragment);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.frame, starFragment, "STAR");
        }
        if (mapFragment.isAdded()) {
            ft.hide(mapFragment);
        }
        if (linkFragment.isAdded()) {
            ft.hide(linkFragment);
        }
        if (exploreFragment.isAdded()) {
            ft.hide(exploreFragment);
        }
        if (waterFragment.isAdded()) {
            ft.hide(waterFragment);
        }
        ft.commit();
    }


    protected void displayPointFragment() {
        getSupportActionBar().setTitle(R.string.explore_string);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (exploreFragment.isAdded()) { // if the fragment is already in container
            ft.show(exploreFragment);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.frame, exploreFragment, "EXPLORE");
        }
        if (linkFragment.isAdded()) {
            ft.hide(linkFragment);
        }
        if (mapFragment.isAdded()) {
            ft.hide(mapFragment);
        }
        if (waterFragment.isAdded()) {
            ft.hide(waterFragment);
        }
        if (starFragment.isAdded()) {
            ft.hide(starFragment);
        }
        ft.commit();
    }

    protected void displayWaterFragment() {
        getSupportActionBar().setTitle(R.string.classification_string);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (waterFragment.isAdded()) { // if the fragment is already in container
            ft.show(waterFragment);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.frame, waterFragment, "WATER");
        }
        if (linkFragment.isAdded()) {
            ft.hide(linkFragment);
        }
        if (exploreFragment.isAdded()) {
            ft.hide(exploreFragment);
        }
        if (mapFragment.isAdded()) {
            ft.hide(mapFragment);
        }
        if (starFragment.isAdded()) {
            ft.hide(starFragment);
        }
        ft.commit();
    }

    @Override
    public void onResume() {
        //TODO Testare!!!
        //displayMapFragment();
        super.onResume();
    }

    // Necessario per cambiare il comportamento del pulsante indietro, in modo che ogni qual volta lo si preme si riporta l utente alla mappa!
    //Integrato la seconda pressione del tasto indietro per consentire l uscita dall app

    @Override
    public void onBackPressed() {
        if (drawer) { //replace this with actual function which returns if the drawer is open
            drawerLayout.closeDrawers();     // replace this with actual function which closes drawer
        } else {
            if (PointFragment.ricercaCreata) {
                if (PointFragment.searchItem.isActionViewExpanded()) {
                    PointFragment.searchItem.collapseActionView(); //controllo se la barra di ricerca è aperta
                } else {
                    checkSatus();
                }
            }else{
                    checkSatus();
            }
        }
    }

    // TODO sistemare il fatto che premendo il tasto menu (presente sui galaxy) esce una schifezza!!!
    public void checkSatus(){
        if (mapFragment.isHidden()) {
            //displayMapFragment();
            drawerLayout.openDrawer(GravityCompat.START);
        } else {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                finish();
                System.exit(0);
            } else {
                Toast.makeText(getBaseContext(),getResources().getString(R.string.exitToast), Toast.LENGTH_SHORT).show();
            }
            mBackPressed = System.currentTimeMillis();
        }
    }
    /**
     * Function to display simple Alert Dialog
     * @param title - alert dialog title
     * @param message - alert message
     * */
    public void showAlertDialog(String title, String message) {
        alert = new AlertDialog.Builder(this);
        alert.setTitle(title);
        alert.setIcon(R.drawable.ic_no_connection);
        alert.setMessage(message);
        alert.setCancelable(false);

        alert.setNegativeButton(R.string.settings_string, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                finish();
                //System.exit(0);
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
            }
        });

        alert.setPositiveButton(getResources().getString(R.string.connButton), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                finish();
                System.exit(0);
            }
        });
        alert.show();
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }

    @Override
    protected void onPause() {
       // dbPlace.close();
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_MENU:
                return true;
        }

        return super.onKeyDown(keycode, e);
    }

    @Override
    protected void onStop() {
       //TODO testare se così si evita il problema delle sovrapposizioni
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
       // ft.remove(linkFragment);
        //ft.remove(waterFragment);
        //ft.remove(exploreFragment);
        //ft.remove(mapFragment);
        //finish();
        super.onStop();
    }

}
