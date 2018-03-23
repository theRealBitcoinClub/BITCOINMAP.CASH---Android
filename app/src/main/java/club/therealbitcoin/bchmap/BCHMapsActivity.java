package club.therealbitcoin.bchmap;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.VenueType;
import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;
import club.therealbitcoin.bchmap.interfaces.OnTaskDoneListener;
import club.therealbitcoin.bchmap.interfaces.UpdateActivityCallback;
import club.therealbitcoin.bchmap.persistence.VenueFacade;
import club.therealbitcoin.bchmap.persistence.WebService;

public class BCHMapsActivity extends AppCompatActivity implements UpdateActivityCallback, OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMarkerClickListener {


    private static final int MY_LOCATION_REQUEST_CODE = 233421353;
    public static final String COINMAP_ORG_VENUES_QUERY = "https://coinmap.org/api/v1/venues/?query=%23trbc";
    public static final String TRBC_VENUES_QUERY = "http://therealbitcoin.club/places.json";
    private GoogleMap mMap;
    private static final String TAG = "TRBC";
    private int currentMapStyle = 0;
    private int[] mapStyles = {R.raw.map_style_classic,R.raw.map_style_dark};
    private FragmentManager fm;
    private Map<String, Marker> markerMap;
    private Map<Integer,ArrayList<Marker>> markersList;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private int[] tabIcons = {
            R.drawable.ic_action_map,
            R.drawable.ic_action_list,
            R.drawable.ic_action_favorite_border
    };
    private SupportMapFragment mapFragment;
    private Toolbar tb;
    private boolean isMapReady = false;
    private PopupListFragment listFragment;
    private PopupListFragment favosFragment;

    @Override
    protected void onStop() {
        super.onStop();
        listFragment = null;
        favosFragment = null;
        mapFragment = null;
        tb = null;
        viewPager = null;
        tabLayout = null;
        fm = null;
        markersList = null;
        markerMap = null;
        mMap = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        setContentView(R.layout.activity_bchmaps);
        initMarkersList();
        findViewsById();

        fm = getSupportFragmentManager();

        setSupportActionBar(tb);
        getSupportActionBar().setTitle(R.string.toolbar);

        mapFragment = (SupportMapFragment) SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        //initTitleTouchListener();

        callWebservice();
        Log.d(TAG,"FINISH ON CREATE");
    }

    private void findViewsById() {
        tb = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
    }

    private void initTitleTouchListener() {
        try {
            final int abTitleId = getResources().getIdentifier("action_bar_title", "id", "android");

            findViewById(abTitleId).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://trbc.io")));
                }
            });
        } catch (Exception e) {
            Log.e(TAG,"ERROR: INIT TITLE LISTENER:" + e);
            e.printStackTrace();
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        Log.d(TAG,"VIEWPAGER");
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mapFragment,"BLA");
        Log.d(TAG,"FRAGMENT");
        listFragment = PopupListFragment.newInstance(false, this);
        adapter.addFragment(listFragment,"BLUB");
        Log.d(TAG,"FRAGMENT22");
        favosFragment = PopupListFragment.newInstance(true, this);
        adapter.addFragment(favosFragment,"FAVOS");
        Log.d(TAG,"ALL ADDED");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        Log.d(TAG,"setupTabIcons");
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void initMarkersList() {
        Log.d(TAG,"initMarkersList");
        markerMap = new HashMap<String,Marker>();
        markersList = new HashMap<Integer,ArrayList<Marker>>();
        for (int i=0; i<5; i++) {
            markersList.put(i, new ArrayList<Marker>());
        }
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
        isMapReady = true;
        Log.d(TAG, "onMapReady: ");
        configureMap(googleMap);
        getPermissionAccessFineLocation();
        setMapStyle(mapStyles[0]);
        setupTabIcons();

        try {
            Log.d(TAG,"ssssss");
            if (markerMap.isEmpty()) {
                addVenuesToMapAndMoveCamera();
            }
        } catch (Exception e) {
            Log.e(TAG,"YAYAYAYAAAAA");
            e.printStackTrace();
        }
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

    private void configureMap(GoogleMap googleMap) {
        mMap = googleMap;
        addMapListener();
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
    }

    private void addMapListener() {
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    private void setMapStyle(int x) {
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, x));
    }

    private void switchMapStyle(){
        currentMapStyle++;
        if (currentMapStyle>=mapStyles.length) {
            currentMapStyle = 0;
        }
        setMapStyle(mapStyles[currentMapStyle]);
    }



    void addVenuesToMapAndMoveCamera() throws JSONException {
        LatLng lastCoordinates = null;
        for (Venue v: VenueFacade.getInstance().getVenuesList()) {
            lastCoordinates = v.getCoordinates();
            Log.d(TAG, "venue: " + v);
            Marker marker = addMarker(v);
            markersList.get(v.type).add(marker);
            markerMap.put(v.placesId, marker);
        }
        if (lastCoordinates != null)
            mMap.animateCamera(CameraUpdateFactory.newLatLng(lastCoordinates));
    }

    @Override
    public void updateBothListViews() {
        Log.d(TAG,"updateListViews");
        updateFavosList();
        updateListView();
    }

    @Override
    public void updateFavosList() {
        initListFragment(2);
    }

    @Override
    public void updateListView() {
        initListFragment(1);
    }

    private void callWebservice() {
        new WebService(TRBC_VENUES_QUERY, new OnTaskDoneListener() {
            @Override
            public void onTaskDone(String responseData) {
                try {
                    List<Venue> venues = WebService.parseVenues(responseData);

                    for (Venue v: venues) {
                        VenueFacade.getInstance().addVenue(v);
                    }

                    Log.d(TAG, "responseData: " + responseData);
                    if(isMapReady)
                        addVenuesToMapAndMoveCamera();

                    updateBothListViews();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switchCheck(item);

        switch (item.getItemId()) {
            case R.id.menu_rating:
                return true;
            case R.id.menu_atm:
                switchVisibility(markersList.get(VenueType.ATM.getIndex()));
                return true;
            case R.id.menu_bar:
                switchVisibility(markersList.get(VenueType.Bar.getIndex()));
                return true;
            case R.id.menu_shops:
                switchVisibility(markersList.get(VenueType.Super.getIndex()));
                return true;
            case R.id.menu_spa:
                switchVisibility(markersList.get(VenueType.Spa.getIndex()));
                return true;
            case R.id.menu_food:
                switchVisibility(markersList.get(VenueType.Food.getIndex()));
                return true;
            case R.id.menu_switch:
                switchMapStyle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void switchCheck(MenuItem item) {
        if (item.isCheckable() && item.isChecked())
            item.setChecked(false);
        else
            item.setChecked(true);
    }

    private void switchVisibility(List<Marker> markers) {
        for (Marker m: markers) {
            if (m.isVisible())
                m.setVisible(false);
            else
                m.setVisible(true);
        }
    }

    private Marker addMarker(Venue v) {
        BitmapDescriptor ic = BitmapDescriptorFactory.fromResource(VenueType.getIconResource(v.type));
        Marker marker = mMap.addMarker(new MarkerOptions().position(v.getCoordinates()).alpha(1f).icon(ic).draggable(false));
        marker.setTag(v);
        return marker;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        try {
            Log.d(TAG,"dsfdsfds");
            if (mMap != null) {
                //mMap.setMyLocationEnabled(false);
            }
            Log.d(TAG,"dsfdsfds2333");
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.e(TAG,Log.getStackTraceString(e));
        } catch (Exception x) {
            Log.e(TAG,Log.getStackTraceString(x));
        }
        return false;
    }


    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Log.d(TAG,location.toString());
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG,"markerclick:" + marker.getId());

        Venue v = (Venue) marker.getTag();
        MarkerDetailsFragment.newInstance(v).show(fm,"MARKERDIALOG");
        return false;
    }
}