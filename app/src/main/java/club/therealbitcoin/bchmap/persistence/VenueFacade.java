package club.therealbitcoin.bchmap.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import club.therealbitcoin.bchmap.R;
import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;

public class VenueFacade {
    private static final VenueFacade ourInstance = new VenueFacade();
    private Map<String, Venue> venuesMap = new HashMap<String,Venue>();
    private ArrayList<Venue> venuesList = new ArrayList<Venue>();
    private ArrayList<String> titles = new ArrayList<String>();
    public static final String MY_FAVORITES = "myFavorites";
    private ConcurrentHashMap<String, Venue> favoVenueMap = new ConcurrentHashMap<String, Venue>();
    private List<Venue> favos;

    public static VenueFacade getInstance() {
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
        Log.d("TRBC","addVenue" + v);
       venuesMap.put(v.placesId, v);
       venuesList.add(v);
    }

    public Venue findVenueById(String id) {
        return venuesMap.get(id);
    }

    boolean hasAddedNew = true;

    public void addFavoriteVenue(Venue v) {
        Log.d("TRBC","addFavoriteVenue persist:");
        favoVenueMap.put(v.placesId,v);
        hasAddedNew = true;
    }

    public List<Venue> getFavoriteVenues (Context ctx) {
        if (!hasAddedNew)
            return favos;

        Iterator<Venue> iterator = favoVenueMap.values().iterator();
        favos = new ArrayList<Venue>();

        while (iterator.hasNext()) {
            favos.add(iterator.next());
        }

        return favos;
    }

    private VenueFacade() {
    }

    public void removeFavoriteVenue(Venue item) {
        favoVenueMap.remove(item);
    }

    public Venue findVenueByIndex(int position) {
        return venuesList.get(position);
    }
}
