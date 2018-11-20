package club.therealbitcoin.bchmap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;

import java.util.List;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.VenueType;
import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;
import club.therealbitcoin.bchmap.interfaces.OnTaskDoneListener;
import club.therealbitcoin.bchmap.interfaces.UpdateActivityCallback;
import club.therealbitcoin.bchmap.persistence.VenueFacade;
import club.therealbitcoin.bchmap.persistence.WebService;

public class BCHMapsActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener, UpdateActivityCallback, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    private static final int MY_LOCATION_REQUEST_CODE = 233421353;
    public static final String TRBC_VENUES_QUERY = "https://realbitcoinclub.firebaseapp.com/places8.json";
    public static final float MIN_ZOOM_WHEN_LOCATION_SERVICES_ARE_ENABLED = 8f;
    public static final String URI_CLICK_LOGO = "https://bitcoinmap.cash";
    public static final String CAM = "cam";
    public static final String SHARED_PREF_CAM_POSITION = "hjadsbfzurzu23";
    public static final String KEY_CAM_POS = "fdgnjerngui3w";
    private static final float ZOOM_LEVEL_DETAIL_CLICK = 17f;
    private GoogleMap mMap;
    private static final String TAG = "TRBC";
    private int[] mapStyles = {R.raw.map_style_classic,R.raw.map_style_dark};
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
    private boolean isLocationAvailable = false;
    public static final int NON_CHECKABLE_MENU_ITEMS_BEFORE_FILTER_ITEMS = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Log.d(TAG,"onCreate");
        setContentView(R.layout.activity_bchmaps);
        findViewsById();

        fm = getSupportFragmentManager();

        initActionBar();

        mapFragment = (SupportMapFragment) SupportMapFragment.newInstance();
        mapFragment.setRetainInstance(true);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        callWebservice(true);

        Log.d(TAG,"FINISH ON CREATE");
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
        Log.d(TAG,"onResume isMapReady:" + mMap);
        if (mMap == null)
            mapFragment.getMapAsync(this);
    }

    private boolean isCacheEmpty() {
        return VenueFacade.getInstance().getVenuesList().size() == 0;
    }

    private void initActionBar() {
        setSupportActionBar(tb);
        ActionBar bar = getSupportActionBar();
        bar.setIcon(R.drawable.logo_action_bar);
        //bar.setHomeButtonEnabled(true);
        //bar.setHomeAsUpIndicator(R.drawable.ic_action_home);
        //bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("");
    }

    private void findViewsById() {
        tb = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
    }

    private void setupViewPager(ViewPager viewPager) {
        Log.d(TAG,"VIEWPAGER");
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mapFragment,"BLA");
        Log.d(TAG,"FRAGMENT");
        listFragment = VenuesListFragment.newInstance(false, this);
        adapter.addFragment(listFragment,"BLUB");
        Log.d(TAG,"FRAGMENT22");
        favosFragment = VenuesListFragment.newInstance(true, this);
        adapter.addFragment(favosFragment,"FAVOS");
        Log.d(TAG,"ALL ADDED");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        Log.d(TAG,"setupTabIcons");
        tabLayout.getTabAt(0).setIcon(tabIcons[0]).setCustomView(R.layout.tabs_icon);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]).setCustomView(R.layout.tabs_icon);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]).setCustomView(R.layout.tabs_icon);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG,"onRequestPermissionsResult");
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                try {
                    mMap.setMyLocationEnabled(true);
                } catch (SecurityException e) {
                    e.printStackTrace();
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            } else {
                Log.d(TAG, "permission denied");
            }
        }
    }


    @TargetApi(23)
    private void getPermissions() {
        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},MY_LOCATION_REQUEST_CODE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        initMap(googleMap);
        getPermissionAccessFineLocation();
        setMapStyle(mapStyles[VenueFacade.getInstance().getTheme(this)]);
        setupTabIcons();

        syncVenueMarkersDataWithMap(true);

        if (!isCacheEmpty())
            initAllListViews();
    }

    private void initListFragment(int index) {
        Log.d(TAG,"initListFragment index" + index);
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
            Log.d(TAG, "setMyLocationEnabled: true ");
        } else {
            Log.d(TAG, "setMyLocationEnabled false: ");
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

    private void switchMapStyle(MenuItem item){
        VenueFacade facade = VenueFacade.getInstance();
        int theme = facade.getTheme(this);
        theme++;
        if (theme >=mapStyles.length) {
            theme=0;
        }
        Log.d(TAG,"switchMapStyle" + theme);
        facade.setTheme(theme, this);
        setMapStyle(mapStyles[theme]);
        initAllListViews();
    }

    void syncVenueMarkersDataWithMap(boolean moveCamera) {
        if (mMap == null)
            return;

        mMap.clear();
        //initMarkersList();
        for (Venue v: VenueFacade.getInstance().getVenuesList()) {
            Log.d(TAG, "venue: " + v);
            Marker marker = addMarker(v);
            //markersListMap.get(v.type).add(marker);
            //markerMap.put(v.placesId, marker);
        }
        if (moveCamera)
           moveCameraToLastLocation();
        //moveCameraToQuinoa();
    }

    private void moveCameraToQuinoa() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.4027984,2.1600427),MIN_ZOOM_WHEN_LOCATION_SERVICES_ARE_ENABLED));
    }

    private void moveCameraToLastLocation() {
        try {
            LocationServices.getFusedLocationProviderClient(this).getLastLocation()
                    .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                Location lastCoordinates = task.getResult();
                                LatLng latLng = new LatLng(lastCoordinates.getLatitude(), lastCoordinates.getLongitude());
                                Log.d(TAG,latLng.latitude + "" + latLng.longitude);
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,MIN_ZOOM_WHEN_LOCATION_SERVICES_ARE_ENABLED));
                                Toast.makeText(BCHMapsActivity.this,R.string.toast_moving_location, Toast.LENGTH_LONG).show();

                                //TODO FIX LOCATION BUG, if location is not available on startup but afterwards it keeps saying enable location
                                //isLocationAvailable = true;
                            } else {
                                Toast.makeText(BCHMapsActivity.this,R.string.toast_enable_location, Toast.LENGTH_LONG).show();
                                Log.d(TAG, "getLastLocation:exception", task.getException());
                            }
                        }
                    });
        } catch (SecurityException e){
            Log.d(TAG,"SECURITYEXCEPTION");
        }
    }



    @Override
    public void initAllListViews() {
        Log.d(TAG,"updateListViews");
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
        mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL_DETAIL_CLICK));
    }

    private void callWebservice(boolean moveCam) {
        new WebService(TRBC_VENUES_QUERY, new OnTaskDoneListener() {
            @Override
            public void onTaskDone(String responseData) {
                try {
                    Log.d(TAG, "onTaskDone: ");
                    List<Venue> venues = WebService.parseVenues(responseData);
                    VenueFacade.getInstance().initVenues(venues, BCHMapsActivity.this);

                    Log.d(TAG, "responseData: " + responseData);

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
        }).execute();
    }

    private void restoreFilters() {
        for (VenueType t: VenueType.getFilterableTypes()
                ) {
            if (VenueFacade.getInstance().isTypeFiltered(t.getIndex(),BCHMapsActivity.this)) {
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
        Log.d(TAG,"menu size:" + size);
        for (int i = NON_CHECKABLE_MENU_ITEMS_BEFORE_FILTER_ITEMS; i< size; i++) {
            int realIndex = i - NON_CHECKABLE_MENU_ITEMS_BEFORE_FILTER_ITEMS;
            MenuItem item = menu.getItem(i);
            Log.d(TAG,"item:" + item.getTitle());
            boolean isChecked = !VenueFacade.getInstance().isTypeFiltered(realIndex, this);

            Log.d(TAG,"onCreateOptionsMenu:" + i + ", isChecked:" + isChecked);
            item.setChecked(isChecked);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (viewPager.getCurrentItem() == 2 && item.getItemId() != android.R.id.home && item.getItemId() != R.id.menu_switch) {
            viewPager.setCurrentItem(1);
            Toast.makeText(this, R.string.toast_favorites_not_affected_by_filter, Toast.LENGTH_LONG).show();
        }

        switchCheck(item);

        switch (item.getItemId()) {
            case R.id.menu_browser:
                openWebsite();
                return true;
            case android.R.id.home:
                //Toast.makeText(this, R.string.toast_go_to_website, Toast.LENGTH_LONG).show();
                openWebsite();
                return true;
            case R.id.menu_bar:
                applyFilters(item,VenueType.Bar);
                return true;
            case R.id.menu_shops:
                applyFilters(item,VenueType.Super);
                return true;
            case R.id.menu_food:
                applyFilters(item,VenueType.Food);
                return true;
            case R.id.menu_fashion:
                applyFilters(item,VenueType.Fashion);
                return true;
            case R.id.menu_sweet:
                applyFilters(item,VenueType.Sweet);
                return true;
            case R.id.menu_hotel:
                applyFilters(item,VenueType.Hotel);
                return true;
            case R.id.menu_switch:
                //viewPager.setCurrentItem(0);
                switchMapStyle(item);

                //updateSwitchThemeIcon(item);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
/*
    private void updateSwitchThemeIcon(MenuItem item) {
        if (VenueFacade.getInstance().getTheme(this) == 0) {
            item.setIcon(R.drawable.ic_action_luna);
        } else {
            item.setIcon(R.drawable.ic_action_sun);
        }
    }*/

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

    long backPressLastClick;

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

    private Marker addMarker(Venue v) {
        BitmapDescriptor ic = BitmapDescriptorFactory.fromResource(v.iconRes);

        Log.d(TAG,"addMarker lat:" + v.getCoordinates().latitude + " lon:" + v.getCoordinates().longitude);
        Marker marker = mMap.addMarker(new MarkerOptions().position(v.getCoordinates()).alpha(1f).icon(ic).draggable(false));
        marker.setTag(v);
        return marker;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG,"markerclick:" + marker.getId());

        Venue v = (Venue) marker.getTag();
        if (v == null) {
            Log.d(TAG,"venue:" + v);
            return false;
        }

        MarkerDetailsFragment.newInstance(v, this, true).show(fm,"MARKERDIALOG");
        return false;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Log.d(TAG,"onMyLocationButtonClick");
        //if (!isLocationAvailable) { //TODO FIX LOCATION NOT AVAILABLE MESSAGE if necessary at all because maybe the location button is simply not available
           // Toast.makeText(this, R.string.toast_enable_location, Toast.LENGTH_SHORT).show();
        //} else {
            Toast.makeText(this, R.string.toast_moving_location, Toast.LENGTH_LONG).show();
        //}
        return false;
    }
/*
    @Override
    public void onCameraMove() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_CAM_POSITION, MODE_PRIVATE);
        sharedPreferences.edit().putString(KEY_CAM_POS, mMap.getCameraPosition().target.toString()).commit();
        Toast.makeText(this,"dsfds" + mMap.getCameraPosition().target.toString(),Toast.LENGTH_SHORT).show();
    }*/
}