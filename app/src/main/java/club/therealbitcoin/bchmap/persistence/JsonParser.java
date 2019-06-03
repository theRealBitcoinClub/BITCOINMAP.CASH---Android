package club.therealbitcoin.bchmap.persistence;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import club.therealbitcoin.bchmap.R;
import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;
import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.VenueJson;

public class JsonParser {

    private static JSONArray jsonArrayPlaces;

    public static void close() {
        jsonArrayPlaces = null;
    }

    public static String getPlacesId(Context ctx, String paramId) throws JSONException, IOException {
        String placesIdContent = FileCache.getCachedContentTriggerInit(ctx, "placesId");
        if (jsonArrayPlaces == null && placesIdContent != null) {
            jsonArrayPlaces = new JSONArray(placesIdContent);
        }

        if (jsonArrayPlaces == null)
            jsonArrayPlaces = new JSONArray(WebService.convertStreamToString(ctx.getResources().openRawResource(R.raw.places_id)));

        for (int i = 0; i < jsonArrayPlaces.length(); i++) {
            JSONObject obj = jsonArrayPlaces.getJSONObject(i);
            String itemIdP = obj.getString(VenueJson.id.toString());

            if (itemIdP.equals(paramId))
                return obj.getString("placesId");
        }
        return null;
    }

    public static LatLng parseLatLng(JSONObject venue) throws JSONException {
        double lat = venue.getDouble(VenueJson.lat.toString());
        double lon = venue.getDouble(VenueJson.lon.toString());
        return new LatLng(lat, lon);
    }

    public static List<Venue> parseVenues(String responseData) throws JSONException {
        JSONArray jsonArray = new JSONArray(responseData);
        List<Venue> venues = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            venues.add(Venue.createInstance(jsonArray.getJSONObject(i)));
        }

        return venues;
    }
}
