package club.therealbitcoin.bchmap;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//@EActivity(R.layout.activity_bchmaps)
public class BCHMapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private static final int MY_LOCATION_REQUEST_CODE = 233421353;
    public static final String COINMAP_ORG_VENUES_QUERY = "https://coinmap.org/api/v1/venues/?query=%23bch";
    private GoogleMap mMap;
    private static final String TAG = "TAG23h9s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bchmaps);

        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        //ActionBar ab = getSupportActionBar();
        //ab.setDisplayUseLogoEnabled(true);
        //ab.setDisplayShowTitleEnabled(false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

    /**
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        Log.d(TAG, "onMapReady: ");
        googleMap.setIndoorEnabled(false);
        googleMap.setBuildingsEnabled(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            Log.d(TAG, "setMyLocationEnabled: true ");
        } else {
            Log.d(TAG, "setMyLocationEnabled false: ");
            getPermissions();
            // Show rationale and request permission.
        }


        callWebservice();
    }

    private void callWebservice() {
        new WebService(COINMAP_ORG_VENUES_QUERY, new OnTaskDoneListener() {
            LatLng latLng;
            @Override
            public void onTaskDone(String responseData) {
                try {
                    Log.d(TAG, "responseData: " + responseData);
                    JSONArray venues = WebService.parseVenues(responseData);

                    for (int x=0; x<venues.length();x++) {
                        JSONObject venue = venues.getJSONObject(x);
                        Log.d(TAG, "venue: " + venue);
                        latLng = WebService.parseMarker(venue);
                        addMarker(latLng, venue.getString("name"));
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                } catch (JSONException e) {
                    Log.e(TAG, "exception: " + Log.getStackTraceString(e));
                    e.printStackTrace();

                }
            }


            @Override
            public void onError() {
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
        switch (item.getItemId()) {
            case R.id.menu_contact:
                return true;
            case R.id.menu_add_place:
                return true;
            case R.id.menu_rating:
                return true;
            case R.id.menu_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addMarker(LatLng latLng, String text) {
        mMap.addMarker(new MarkerOptions().position(latLng).title(text));
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
}