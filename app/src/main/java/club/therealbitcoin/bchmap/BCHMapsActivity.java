package club.therealbitcoin.bchmap;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.enums.VenueJson;
import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.enums.VenueType;

public class BCHMapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMarkerClickListener {

    private static final int MY_LOCATION_REQUEST_CODE = 233421353;
    public static final String COINMAP_ORG_VENUES_QUERY = "https://coinmap.org/api/v1/venues/?query=%23trbc";
    public static final String TRBC_VENUES_QUERY = "http://therealbitcoin.club/places.json";
    private GoogleMap mMap;
    private static final String TAG = "TRBC";
    private int currentMapStyle = 0;
    private int[] mapStyles = {R.raw.map_style_classic,R.raw.map_style_dark};
    private Map<String, Venue> venuesMap = new HashMap<String,Venue>();
    private FragmentManager fm;
    private Map<String, Marker> markerMap;
    private Map<Integer,ArrayList<Marker>> markersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bchmaps);
        initMarkersList();

        fm = getSupportFragmentManager();

        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle(R.string.toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initMarkersList() {
        markerMap = new HashMap<String,Marker>();
        markersList = new HashMap<Integer,ArrayList<Marker>>();
        for (int i=0; i<5; i++) {
            markersList.put(i, new ArrayList<Marker>());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
        configureMap(googleMap);
        getPermissionAccessFineLocation();
        setMapStyle(mapStyles[0]);

        try {
            Log.d(TAG,"ssssss");
            callWebservice();
        } catch (Exception e) {
            e.printStackTrace();
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



    void addVenuesToMapAndMoveCamera(JSONArray venues) throws JSONException {
        venuesMap = new HashMap<String, Venue>();
        LatLng latLng = null;
        for (int x=0; x<venues.length();x++) {
            JSONObject venue = venues.getJSONObject(x);
            Log.d(TAG, "venue: " + venue);
            latLng = WebService.parseLatLng(venue);

            int type = venue.getInt(VenueJson.type.toString());
            String placesId = venue.getString(VenueJson.placesId.toString());
            addVenueToCache(venue, type, placesId);
            Marker marker = addMarker(latLng, type, placesId);
            markersList.get(type).add(marker);
            markerMap.put(placesId, marker);
        }
        if (latLng != null)
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }


    private void addVenueToCache(JSONObject venue, int type, String placesId) throws JSONException {
        String name = venue.getString(VenueJson.name.toString());
        double stars = venue.getDouble(VenueJson.score.toString());
        int rev = venue.getInt(VenueJson.reviews.toString());
        venuesMap.put(placesId, new Venue(name, VenueType.getIconResource(type), type, placesId, rev, stars));
    }

    private void callWebservice() {
        new WebService(TRBC_VENUES_QUERY, new OnTaskDoneListener() {
            @Override
            public void onTaskDone(String responseData) {
                try {
                    Log.d(TAG, "responseData: " + responseData);
                    JSONArray venues = WebService.parseVenues(responseData);

                    addVenuesToMapAndMoveCamera(venues);
                } catch (JSONException e) {
                    Log.e(TAG, "exception: " + Log.getStackTraceString(e));
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                Toast.makeText(BCHMapsActivity.this,R.string.error_con_webservice,Toast.LENGTH_LONG);
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

    private Marker addMarker(LatLng latLng, int type, String markerId) {
        BitmapDescriptor ic = BitmapDescriptorFactory.fromResource(VenueType.getIconResource(type));
        return mMap.addMarker(new MarkerOptions().position(latLng).alpha(1f).icon(ic).draggable(false).snippet(markerId));
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
        Log.d(TAG,"markdfdsfdsfdsdfserclick:" + marker.getSnippet());

        Venue v = venuesMap.get(marker.getSnippet());
        MarkerDetailsFragment.newInstance(v).show(fm,"MARKERDIALOG");
        return false;
    }
}