package it.inserrafesta.iseomap.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import it.inserrafesta.iseomap.ConnectionDetector;
import it.inserrafesta.iseomap.R;

import it.inserrafesta.iseomap.fragment.LinkFragment;
import it.inserrafesta.iseomap.fragment.MapFragment;
import it.inserrafesta.iseomap.fragment.PointFragment;
import it.inserrafesta.iseomap.fragment.WaterFragment;

import static android.content.DialogInterface.*;

public class MainActivity extends AppCompatActivity {

    //Defining Variables

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    boolean drawer=false; //var usata per vedere se il drawer e' aperto

    static public MapFragment mapFragment;
    static LinkFragment linkFragment;
    static PointFragment exploreFragment;
    static WaterFragment waterFragment;

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        checkStatusConnection();
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


                    //Replacing the main content with PointFragment Which is our Inbox View;

                    //Replacing the main content with PointFragment Which is our Inbox View;
                    case R.id.home:
                        displayMapFragment();
//android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        //ft.replace(R.id.frame, mapFragment);
                        //ft.commit();

                        // replaceFragment(mapFragment);
                        //MapFragment fragment = new MapFragment();
                        // android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        // fragmentTransaction.replace(R.id.frame, fragment);
                        // fragmentTransaction.commit();

                        //MapFragment.zoomAnimateLevelToFitMarkers(120);


                        // For rest of the options we just show a toast on click
                        return true;

                    case R.id.explore:
                        displayPointFragment();

                       /* exploreFragment = new PointFragment();
                        android.support.v4.app.FragmentTransaction explorefragmentTransaction = getSupportFragmentManager().beginTransaction();
                        explorefragmentTransaction.replace(R.id.frame, exploreFragment);
                        explorefragmentTransaction.commit();
                       // String strUserName = SP.getString("satellite", "NA");
                        //String downloadType = SP.getString("downloadType","1"); */
                        return true;

                    case R.id.classification:
                        displayWaterFragment();

                       /* waterFragment = new WaterFragment();
                        android.support.v4.app.FragmentTransaction waterFragmentTransaction = getSupportFragmentManager().beginTransaction();
                        waterFragmentTransaction.replace(R.id.frame, waterFragment);
                        waterFragmentTransaction.commit();*/
                        return true;

                    case R.id.link:

                        displayLinkFragment();
                       /* linkFragment = new LinkFragment();
                        android.support.v4.app.FragmentTransaction linkFragmentTransaction = getSupportFragmentManager().beginTransaction();
                        linkFragmentTransaction.replace(R.id.frame, linkFragment);
                        linkFragmentTransaction.commit();*/
                        return true;
                    case R.id.settings:
                        startActivity(new Intent(navigationView.getContext(), MyPreferencesActivity.class));
                        return true;
                    case R.id.help:
                        //startActivity(new Intent(view.getContext(), HelpActivity.class));
                        return true;
                    case R.id.about:
                        // getSupportActionBar().setTitle(R.string.about_string);
                        startActivity(new Intent(navigationView.getContext(), AboutActivity.class)); //TODO rimettere About
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
            boolean OptSatellite = SP.getBoolean("satellite", true);
            Bundle bundl = new Bundle();
            bundl.putBoolean("OptSatellite", OptSatellite);
/*
            mapFragment = new MapFragment();
            mapFragment.setArguments(bundl);
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, mapFragment);
            fragmentTransaction.addToBackStack("MAP");
            fragmentTransaction.commit(); */
            waterFragment = new WaterFragment();

            mapFragment = new MapFragment();
            mapFragment.setArguments(bundl);
            linkFragment = new LinkFragment();
            exploreFragment = new PointFragment();
            displayMapFragment();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


    }

    protected void checkStatusConnection(){
     /*check if is present internet connection */
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        Boolean isInternetPresent = cd.isConnectingToInternet();

        if (!isInternetPresent) {
            // make HTTP requests
            showAlertDialog(this, "Connessione Internet assente", "Necessaria una connessione internet per usare l'applicazione", true);
        }
    }

    protected void displayMapFragment() {
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
        ft.commit();
    }

    /*
    / metodo che controlla se il fragment della mappa e gia presente o meno, in caso non lo sia ne crea uno nuovo, invece se esiste gia non fa niente in modo che non ricarica di nuovo la mappa se e gia presente!
     */
    private void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) { //fragment not in back stack, create it.
            android.support.v4.app.FragmentTransaction ft = manager.beginTransaction();
            // ft.add(R.id.frame,fragment,tag);
            ft.replace(R.id.frame, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

   /* public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/


    /*
    * Qui sono presenti tutti i listener per gli onclick dei file xml dei vari fragment, necessario farlo qui perche i fragmetn non possono ricevere chiamate dai onclick dei button
     */
    public void goToDetails(View view) {
        Intent myIntent = new Intent(view.getContext(), DetailsActivity.class);
        startActivity(myIntent);
    }

    @Override
    public void onResume() {
        //checkStatusConnection();
        super.onResume();

        // TODO necessario per nascondere la navigation bar, problema la mapFragment pero non si estende sotto dove non c'e piu la navigation bar
       /* View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= 19) {
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
         }*/
    }

    // Necessario per cambiare il comportamento del pulsante indietro, in modo che ogni qual volta lo si preme si riporta l utente alla mappa!
    //Integrato la seconda pressione del tasto indietro per consentire l uscita dall app

    @Override
    public void onBackPressed() {
        if(drawer){ //replace this with actual function which returns if the drawer is open
            drawerLayout.closeDrawers();     // replace this with actual function which closes drawer
        }else {
            if (mapFragment.isHidden()) {
                displayMapFragment();
            } else {
                if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                    finish();
                    System.exit(0);
                } else {
                    Toast.makeText(getBaseContext(), "Premere di nuovo il tasto INDIETRO per uscire dall'app", Toast.LENGTH_SHORT).show();
                }
                mBackPressed = System.currentTimeMillis();
            }
        }
    }

    /**
     * Function to display simple Alert Dialog
     * @param context - application context
     * @param title - alert dialog title
     * @param message - alert message
     * @param status - success/failure (used to set icon)
     * */
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
       final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);
        alert.setIcon(R.drawable.ic_no_connection);
        alert.setMessage(message);
        alert.setCancelable(false);

        alert.setNegativeButton("Impostazioni", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                //finish();
                //System.exit(0);
            //TODO funziona ma se si preme il tasto indietro torna all app normalmente
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
            }
        });

        alert.setPositiveButton("Esci", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                finish();
                System.exit(0);
            }
        });
        alert.show();
    }
}
