package club.therealbitcoin.bchmap;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VenueCache {
    private static final VenueCache ourInstance = new VenueCache();
    private Map<String, Venue> venuesMap = new HashMap<String,Venue>();
    private ArrayList<Venue> venuesList = new ArrayList<Venue>();
    private ArrayList<String> titles = new ArrayList<String>();

    public static VenueCache getInstance() {
        return ourInstance;
    }

    public ArrayList<Venue> getVenuesList() {
        return venuesList;
    }

    public ArrayList<String> getVenueTitles() {
        if (titles.size() > 0) {
            return titles;
        }

        Log.d("TRBC","titles start");
        for (Venue v: getVenuesList()
             ) {
            Log.d("TRBC","titlessssss" + v.getName());
            titles.add(v.getName());
        }
        Log.d("TRBC","titles end");
        return titles;
    }

    public void addVenue(Venue v) {
       venuesMap.put(v.placesId, v);
       venuesList.add(v);
    }

    public Venue findVenueById(String id) {
        return venuesMap.get(id);
    }

    private VenueCache() {
    }
}
