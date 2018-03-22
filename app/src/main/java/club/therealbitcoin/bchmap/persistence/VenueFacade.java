package club.therealbitcoin.bchmap.persistence;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;

public class VenueFacade {
    private static VenueFacade ourInstance = new VenueFacade();
    private Map<String, Venue> venuesMap = new HashMap<String,Venue>();
    private ArrayList<Venue> venuesList = new ArrayList<Venue>();
    private ArrayList<String> titles = new ArrayList<String>();
    public static final String MY_FAVORITES = "myFavorites";
    private ConcurrentHashMap<String, Venue> favoVenueMap = new ConcurrentHashMap<String, Venue>();
    private List<Venue> favorites;


    /*
    THIS IS FOR MAKING TESTING EASIER ONLY USE IN TESTS
     */
    public static VenueFacade createNewFacadeForTesting() {
        ourInstance = new VenueFacade();
        return ourInstance;
    }

    public static VenueFacade getInstance() {
        return ourInstance;
    }

    public ArrayList<Venue> getVenuesList() {
        return venuesList;
    }

    public ArrayList<String> getVenueTitles(Context ctx) {
        if (!hasAddedNew && titles.size() > 0) {
            return titles;
        }

        Log.d("TRBC","titles start");
        for (Venue v: getVenuesList()
             ) {
            Log.d("TRBC","titlessssss" + v.getName());
            titles.add(v.getName());
        }
        Log.d("TRBC","titles end");
        hasAddedNew = false;
        return titles;
    }

    public ArrayList<String> getFavoTitles(Context ctx) {
        if (!hasAddedNewFavo && titles.size() > 0) {
            return titles;
        }

        Log.d("TRBC","titlesfavos start");
        for (Venue v: getFavoriteVenues(ctx)
                ) {
            Log.d("TRBC","favosssssssss" + v.getName());
            titles.add(v.getName());
        }
        Log.d("TRBC","titlesfavos end");
        hasAddedNewFavo = false;
        return titles;
    }

    public void addVenue(Venue v) {
        hasAddedNew = true;
        Log.d("TRBC","addVenue" + v);
       venuesMap.put(v.placesId, v);
       venuesList.add(v);
    }

    public Venue findVenueById(String id) {
        return venuesMap.get(id);
    }

    boolean hasAddedNew = true;
    boolean hasAddedNewFavo = true;

    public void addFavoriteVenue(Venue v, Context ctx) {
        Log.d("TRBC","addFavoriteVenue persist:");
        favoVenueMap.put(v.placesId,v);
        v.setFavorite(true,ctx);
        hasAddedNewFavo = true;
    }

    public List<Venue> getFavoriteVenues (Context ctx) {
        if (!hasAddedNewFavo)
            return favorites;

        Iterator<Venue> iterator = favoVenueMap.values().iterator();
        favorites = new ArrayList<Venue>();

        while (iterator.hasNext()) {
            favorites.add(iterator.next());
        }

        hasAddedNewFavo = false;
        return favorites;
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
