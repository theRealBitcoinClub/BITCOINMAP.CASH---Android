package club.therealbitcoin.bchmap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;
import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.VenueType;
import club.therealbitcoin.bchmap.interfaces.UpdateActivityCallback;
import club.therealbitcoin.bchmap.persistence.FileCache;
import club.therealbitcoin.bchmap.persistence.JsonParser;
import club.therealbitcoin.bchmap.persistence.VenueFacade;
import club.therealbitcoin.bchmap.persistence.WebService;

public class BCHMapsActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener, UpdateActivityCallback, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    public static final float MIN_ZOOM_WHEN_LOCATION_SERVICES_ARE_ENABLED = 8f;
    public static final String URI_CLICK_LOGO = "http://bitcoinmap.cash";
    public static final int NON_CHECKABLE_MENU_ITEMS_BEFORE_FILTER_ITEMS = 0;
    private static final int MY_LOCATION_REQUEST_CODE = 233421353;
    private static final float ZOOM_LEVEL_DETAIL_CLICK = 17f;
    private static final String TAG = "TRBC";
    private static final String SHARED_PREF_CAM_POSITION = "SHARED_PREF_CAM_POSITION";
    private static final String KEY_CAM_POS = "KEY_CAM_POS";
    long backPressLastClick;
    private GoogleMap mMap;
    private int[] mapStyles = {R.raw.map_style_classic, R.raw.map_style_dark};
    private FragmentManager fm;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_action_map,
            R.drawable.ic_action_list,
            R.drawable.ic_action_favorite_white
    };
    private SupportMapFragment mapFragment;
    private Toolbar tb;
    private VenuesListFragment listFragment;
    private VenuesListFragment favosFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_bchmaps);
        findViewsById();

        fm = getSupportFragmentManager();

        initActionBar();

        initLastKnowLocation();

        mapFragment = SupportMapFragment.newInstance();
        mapFragment.setRetainInstance(true);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        loadAssets();
        checkConnectionShowToast();

    }

    @Override
    protected void onDestroy() {
       /* FileCache.close();
        JsonParser.close();
        VenueFacade.close();*/
        super.onDestroy();
    }

    private void checkConnectionShowToast() {
        if (!ConnectionChecker.hasInternetConnection(this)) {
            showToast(R.string.toast_no_internet);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //BUGFIX BECAUSE OTHERWISE ILLEGALSTATEExCEPTION ON MARKERCLICK
        //https://stackoverflow.com/questions/7575921/illegalstateexception-can-not-perform-this-action-after-onsaveinstancestate-wit
        //https://developer.android.com/guide/topics/resources/runtime-changes#RetainingAnObject
        //super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMap == null)
            mapFragment.getMapAsync(this);
    }

    private boolean isCacheEmpty() {
        return VenueFacade.getInstance().getVenuesList().size() == 0;
    }

    private void initActionBar() {
        setSupportActionBar(tb);
        ActionBar bar = getSupportActionBar();
        if (bar == null)
            return;

        bar.setIcon(R.drawable.logo_action_bar);
        bar.setTitle("");
    }

    private void findViewsById() {
        tb = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mapFragment, "BLA");
        listFragment = VenuesListFragment.newInstance(latLng, false, this);
        adapter.addFragment(listFragment, "BLUB");
        favosFragment = VenuesListFragment.newInstance(latLng, true, this);
        adapter.addFragment(favosFragment, "FAVOS");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]).setCustomView(R.layout.tabs_icon);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]).setCustomView(R.layout.tabs_icon);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]).setCustomView(R.layout.tabs_icon);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            showToast(R.string.toast_restart_app_for_permission_take_effect);
            finish();
            Intent intent = new Intent(this, BCHMapsActivity.class);
            startActivity(intent);
        }
    }

    @TargetApi(23)
    private void getPermissions() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        initMap(googleMap);
        getPermissionAccessFineLocation();
        setMapStyle(mapStyles[VenueFacade.getInstance().getTheme(this)]);
        setupTabIcons();

        syncVenueMarkersDataWithMap(true);

        if (!isCacheEmpty())
            initAllListViews();
    }

    private void initListFragment(int index) {
        if (index == 1 && listFragment != null) {
            listFragment.initAdapter(false);
        } else if (index == 2 && favosFragment != null) {
            favosFragment.initAdapter(true);
        }
    }

    private void getPermissionAccessFineLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            getPermissions();
        }
    }

    private void initMap(GoogleMap googleMap) {
        mMap = googleMap;
        initMapListener();
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setCompassEnabled(false);
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setScrollGesturesEnabled(true);
    }

    private void initMapListener() {
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
    }

    private void setMapStyle(int x) {
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, x));
    }

    private void switchMapStyle() {
        VenueFacade facade = VenueFacade.getInstance();
        int theme = facade.getTheme(this);
        theme++;
        if (theme >= mapStyles.length) {
            theme = 0;
        }
        facade.setTheme(theme, this);
        setMapStyle(mapStyles[theme]);
        initAllListViews();
    }

    void syncVenueMarkersDataWithMap(boolean moveCamera) {
        if (mMap == null)
            return;

        mMap.clear();
        for (Venue v : VenueFacade.getInstance().getVenuesList()) {
            addMarker(v);
        }
        if (moveCamera)
            moveCameraToLastLocation();
    }

    LatLng latLng;

    private void moveCameraToLastLocation() {
        try {
            LocationServices.getFusedLocationProviderClient(this).getLastLocation()
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location lastCoordinates = task.getResult();
                            persistLastKnowLocation(lastCoordinates);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, MIN_ZOOM_WHEN_LOCATION_SERVICES_ARE_ENABLED));
                            showToastMovingLocation();
                        } else {
                            showToast(R.string.toast_enable_location_enjoy_all_features);
                        }
                    });
        } catch (SecurityException e) {
        }
    }

    private void initLastKnowLocation() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_CAM_POSITION, MODE_PRIVATE);
        String latestKnownPos = sharedPreferences.getString(KEY_CAM_POS, "");
        if (latestKnownPos.equals(""))
            return;

        latLng = new LatLng(getPartialLatLng(latestKnownPos, 0), getPartialLatLng(latestKnownPos, 1));
    }

    private double getPartialLatLng(String latestKnownPos, int pos) {
        return Double.parseDouble(latestKnownPos.split(";")[pos]);
    }

    private void persistLastKnowLocation(Location lastCoordinates) {
        latLng = new LatLng(lastCoordinates.getLatitude(), lastCoordinates.getLongitude());
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_CAM_POSITION, MODE_PRIVATE);
        sharedPreferences.edit().putString(KEY_CAM_POS, lastCoordinates.getLatitude() + ";" + lastCoordinates.getLongitude()).apply();
    }

    @Override
    public void initAllListViews() {
        initFavosList();
        initListView();
    }

    @Override
    public void initFavosList() {
        initListFragment(2);
    }

    @Override
    public void initListView() {
        initListFragment(1);
    }

    @Override
    public void updateCameraPosition(LatLng coordinates) {
        if (mMap != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
    }

    @Override
    public void switchTabZoomCamera() {
        viewPager.setCurrentItem(0);
        try {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL_DETAIL_CLICK));
        } catch (java.lang.NullPointerException e) {
            /*java.lang.NullPointerException: CameraUpdateFactory is not initialized
            at com.google.android.gms.common.internal.Preconditions.checkNotNull(Unknown Source)
            at com.google.android.gms.maps.CameraUpdateFactory.zzc(Unknown Source)
            at com.google.android.gms.maps.CameraUpdateFactory.zoomTo(Unknown Source)
            at club.therealbitcoin.bchmap.BCHMapsActivity.switchTabZoomCamera(BCHMapsActivity.java:313)
            */
        }
    }

    private void loadAssets() {
        try {
            //this call goes to github.com/therealbitcoinclub/flutter_coinector/asset directory
            String s = FileCache.getCachedContentTriggerInit(getBaseContext(),"places");
            if (s == null || s.isEmpty())
                s = WebService.convertStreamToString(getResources().openRawResource(R.raw.places));

            List<Venue> venues = JsonParser.parseVenues(s);
            VenueFacade.getInstance().initVenues(venues, BCHMapsActivity.this, latLng);

            restoreFilters();

            if (mMap != null)
                syncVenueMarkersDataWithMap(true);

            initAllListViews();
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e);
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(TAG, "JSONException: " + e);
            e.printStackTrace();
        }
        /*new WebService(TRBC_VENUES_QUERY, new OnTaskDoneListener() {
            @Override
            public void onTaskDone(String responseData) {
                try {
                    List<Venue> venues = JsonParser.parseVenues(responseData);
                    VenueFacade.getInstance().initVenues(venues, BCHMapsActivity.this);

                    restoreFilters();

                    if(mMap != null)
                        syncVenueMarkersDataWithMap(moveCam);

                    initAllListViews();
                } catch (JSONException e) {
                    Log.e(TAG, "exception: " + Log.getStackTraceString(e));
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                Toast.makeText(BCHMapsActivity.this,R.string.error_con_webservice,Toast.LENGTH_LONG).show();
                Log.e(TAG,"errrrror");
            }
        }).execute();*/
    }

    private void restoreFilters() {
        for (VenueType t : VenueType.getFilterableTypes()
        ) {
            if (VenueFacade.getInstance().isTypeFiltered(t.getIndex(), BCHMapsActivity.this)) {
                VenueFacade.getInstance().filterListByType(t, BCHMapsActivity.this);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        //updateSwitchThemeIcon(menu.getItem(0));

        int size = menu.size();
        for (int i = NON_CHECKABLE_MENU_ITEMS_BEFORE_FILTER_ITEMS; i < size; i++) {
            int realIndex = i - NON_CHECKABLE_MENU_ITEMS_BEFORE_FILTER_ITEMS;
            MenuItem item = menu.getItem(i);
            boolean isChecked = !VenueFacade.getInstance().isTypeFiltered(realIndex, this);

            item.setChecked(isChecked);
        }

        return true;
    }
/*
    private void updateSwitchThemeIcon(MenuItem item) {
        if (VenueFacade.getInstance().getTheme(this) == 0) {
            item.setIcon(R.drawable.ic_action_luna);
        } else {
            item.setIcon(R.drawable.ic_action_sun);
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (viewPager.getCurrentItem() == 2 && item.getItemId() != android.R.id.home && item.getItemId() != R.id.menu_switch) {
            viewPager.setCurrentItem(1);
            showToast(R.string.toast_favorites_not_affected_by_filter);
        }

        switchCheck(item);

        switch (item.getItemId()) {
            case R.id.menu_browser:
                openWebsite();
                return true;
            case android.R.id.home:
                openWebsite();
                return true;
            case R.id.menu_bar:
                applyFilters(item, VenueType.Bar);
                return true;
            case R.id.menu_shops:
                applyFilters(item, VenueType.Super);
                return true;
            case R.id.menu_food:
                applyFilters(item, VenueType.Food);
                return true;
            case R.id.menu_fashion:
                applyFilters(item, VenueType.Fashion);
                return true;
            case R.id.menu_sweet:
                applyFilters(item, VenueType.Sweet);
                return true;
            case R.id.menu_hotel:
                applyFilters(item, VenueType.Hotel);
                return true;
            case R.id.menu_switch:
                //viewPager.setCurrentItem(0);
                switchMapStyle();

                //updateSwitchThemeIcon(item);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void applyFilters(MenuItem item, VenueType type) {
        if (item.isChecked()) {
            VenueFacade.getInstance().restoreFilteredVenues(type, this);
        } else {
            VenueFacade.getInstance().filterListByType(type, this);
        }

        syncVenueMarkersDataWithMap(false);
        initAllListViews();
    }

    private void openWebsite() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URI_CLICK_LOGO)));
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - 1000L < backPressLastClick) {
            super.onBackPressed();
        }
        backPressLastClick = System.currentTimeMillis();
    }

    private void switchCheck(MenuItem item) {
        if (item.isCheckable() && item.isChecked())
            item.setChecked(false);
        else
            item.setChecked(true);
    }

    private void addMarker(Venue v) {
        BitmapDescriptor ic = BitmapDescriptorFactory.fromResource(v.iconRes);

        Marker marker = mMap.addMarker(new MarkerOptions().position(v.getCoordinates()).alpha(1f).icon(ic).draggable(false));
        marker.setTag(v);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Venue v = (Venue) marker.getTag();
        if (v == null) {
            return false;
        }

        MarkerDetailsFragment.newInstance(v, this, true).show(fm, "MARKERDIALOG");
        return false;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        try {
            LocationServices.getFusedLocationProviderClient(this).getLastLocation()
                    .addOnCompleteListener(this, task -> {
                        if (task.getResult() != null) {
                            showToastMovingLocation();
                        } else {
                            showToast(R.string.toast_enable_location);
                        }
                    });
        } catch (SecurityException e) {
        }
        return false;
    }

    private void showToastMovingLocation() {
        showToast(R.string.toast_moving_location);
        checkConnectionShowToast();
    }

    private void showToast(int p) {
        Toast.makeText(BCHMapsActivity.this, p, Toast.LENGTH_LONG).show();
    }
/*
    @Override
    public void onCameraMove() {
      TODO save the current position in shared preferences to initialize from this value on app restart

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_CAM_POSITION, MODE_PRIVATE);
        sharedPreferences.edit().putString(KEY_CAM_POS, mMap.getCameraPosition().target.toString()).commit();
        Toast.makeText(this,"dsfds" + mMap.getCameraPosition().target.toString(),Toast.LENGTH_SHORT).show();
    }*/
}