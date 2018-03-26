package club.therealbitcoin.bchmap.persistence;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;

public class VenueFacade {
    private static VenueFacade ourInstance = new VenueFacade();
    private Map<String, Venue> venuesMap = new HashMap<String,Venue>();
    private ArrayList<Venue> venuesList = new ArrayList<Venue>();
    private ArrayList<String> titles = new ArrayList<String>();
    public static final String MY_FAVORITES = "myFavorites";
    private List<Venue> favorites = new ArrayList<Venue>();
    private ArrayList<String> titlesFavo = new ArrayList<String>();

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

    public ArrayList<String> getVenueTitles() {
        Log.d("TRBC","getVenueTitles");
        if (!hasAddedNew && titles.size() > 0) {
            return titles;
        }

        titles.clear();
        Log.d("TRBC","titles start");
        for (Venue v: getVenuesList()
             ) {
            Log.d("TRBC","titlessssss" + v.name);
            titles.add(v.name);
        }
        Log.d("TRBC","titles end");
        hasAddedNew = false;
        return titles;
    }

    public ArrayList<String> getFavoTitles() {
        Log.d("TRBC","getFavoTitles");
        if (!hasChangedFavo && titles.size() > 0) {
            return titlesFavo;
        }

        titlesFavo.clear();
        Log.d("TRBC","titlesfavos start");
        for (Venue v: getFavoriteVenues()
                ) {
            Log.d("TRBC","favosssssssss" + v.name);
            titlesFavo.add(v.name);
        }
        Log.d("TRBC","titlesfavos end");
        hasChangedFavo = false;
        return titlesFavo;
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
    boolean hasChangedFavo = true;

    public void addFavoriteVenue(Venue v) {
        v.favoListIndex = favorites.size();
        Log.d("TRBC","addFavoriteVenue persist:" + v);
        favorites.add(v);
        hasChangedFavo = true;
    }

    public List<Venue> getFavoriteVenues () {
        Log.d("TRBC","getFavoriteVenues :");
        return favorites;
    }

    private VenueFacade() {
    }

    public void removeFavoriteVenue(Venue item) {
        Log.d("TRBC","removeFavoriteVenue :" + item + "index:" + item.favoListIndex);
        favorites.remove(item.favoListIndex);
        hasChangedFavo = true;
    }

    public Venue findVenueByIndex(int position) {
        return venuesList.get(position);
    }

    public Venue findFavoByIndex(int position) {
        return favorites.get(position);
    }
}
