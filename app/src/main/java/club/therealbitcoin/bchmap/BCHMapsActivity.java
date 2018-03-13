package club.therealbitcoin.bchmap;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.location.Location;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;

//@EActivity(R.layout.activity_bchmaps)
public class BCHMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private static final int MY_LOCATION_REQUEST_CODE = 233421353;
    public static final String COINMAP_ORG_VENUES_QUERY = "https://coinmap.org/api/v1/venues/?query=%23bch";
    private GoogleMap mMap;
    private static final String TAG = "TAG23h9s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bchmaps);

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
                    JSONArray venues = parseResults(responseData);

                    for (int x=0; x<venues.length();x++) {
                        parseMarker(venues, x);
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                } catch (JSONException e) {
                    Log.e(TAG, "exception: " + Log.getStackTraceString(e));
                    e.printStackTrace();

                }
            }

            private void parseMarker(JSONArray venues, int x) throws JSONException {
                JSONObject venue = venues.getJSONObject(x);
                Log.d(TAG, "onTaskDone: " + venue);
                latLng = new LatLng(venue.getDouble("lat"),venue.getDouble("lon"));
                addMarker(latLng, venue.getString("name"));
            }

            private JSONArray parseResults(String responseData) throws JSONException {
                return new JSONObject(responseData).getJSONArray("venues");
            }

            @Override
            public void onError() {
                Log.e(TAG,"errrrror");
            }
        }).execute();
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
