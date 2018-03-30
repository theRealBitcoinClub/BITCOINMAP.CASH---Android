package club.therealbitcoin.bchmap.persistence;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;
import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.VenueType;

public class VenueFacade {
    public static final String TAG = "TRBC";
    private static VenueFacade ourInstance = new VenueFacade();
    private Map<String, Venue> venuesMap = new HashMap<String,Venue>();
    private ArrayList<Venue> venuesList = new ArrayList<Venue>();
    private ArrayList<String> titles = new ArrayList<String>();
    public static final String MY_FAVORITES = "myFavorites";
    private List<Venue> favorites = new ArrayList<Venue>();
    private ArrayList<String> titlesFavo = new ArrayList<String>();
    private Map<String,ArrayList<Venue>> filteredVenuesMap = new HashMap<String,ArrayList<Venue>>();

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

    public boolean isTypeFiltered(int t) {
        ArrayList<Venue> venues = filteredVenuesMap.get(""+t);
        Log.d(TAG,"isTypeFiltered venues:" + venues);
        if (venues == null || venues.size() == 0)
            return false;

        return true;
        //return filteredVenuesMap.get(t) != null || ;
    }

    public void restoreFilteredVenues(VenueType t) {
        Log.d(TAG,"restoreFilteredVenues" + t);
        ArrayList<Venue> venues = filteredVenuesMap.remove(""+t.getIndex());
        Log.d(TAG,"restoreFilteredVenues size" + venues.size());
        if (venues == null || venues.size() == 0)
            return;

        Log.d(TAG,"restoreFilteredVenues Yep" + t);
        venuesList.addAll(0,venues);
        hasChangedBothLists();
    }

    private void hasChangedBothLists() {
        hasChangedFavoList = true;
        hasChangedList = true;
    }

    public void filterListByType(VenueType t) {
        int initialSize = venuesList.size();
        for (int i = 0; i< initialSize; i++) {
            Log.d("TRBC","check type:" + t.toString() + " index:" + i);
            if (venuesList.size() == i) {
                Log.d("TRBC","break:" + t.toString() + " index:" + i);
                break;
            }

            int typeIndex = t.getIndex();
            if (venuesList.get(i).type == typeIndex) {
                Log.d("TRBC","remove item type:" + t.toString() + " index:" + i);
                ArrayList<Venue> filteredVenues = filteredVenuesMap.get(""+typeIndex);
                if (filteredVenues == null || filteredVenues.size() == 0) {
                    for (VenueType x: VenueType.values()) {
                        filteredVenuesMap.put(""+typeIndex, new ArrayList<Venue>());
                    }
                    filteredVenues = filteredVenuesMap.get(""+typeIndex);
                }
                filteredVenues.add(venuesList.remove(i));
                i--;
            }
        }

        hasChangedBothLists();
    }

    public ArrayList<String> getVenueTitles() {
        Log.d("TRBC","getVenueTitles");
        if (!hasChangedList && titles.size() > 0) {
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
        hasChangedList = false;
        return titles;
    }

    public ArrayList<String> getFavoTitles() {
        Log.d("TRBC","getFavoTitles");
        if (!hasChangedFavoList && titlesFavo.size() > 0) {
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
        hasChangedFavoList = false;
        return titlesFavo;
    }

    public void addVenue(Venue v) {
        hasChangedList = true;
        Log.d("TRBC","addVenue" + v);
       if (venuesMap.put(v.placesId, v) == null) {
           venuesList.add(v);
       }
    }

    public Venue findVenueById(String id) {
        return venuesMap.get(id);
    }

    boolean hasChangedList = true;
    boolean hasChangedFavoList = true;

    public void addFavoriteVenue(Venue v) {
        v.favoListIndex = favorites.size();
        Log.d("TRBC","addFavoriteVenue persist:" + v);
        favorites.add(v);
        hasChangedFavoList = true;
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
        hasChangedFavoList = true;
    }

    public Venue findVenueByIndex(int position) {
        return venuesList.get(position);
    }

    public Venue findFavoByIndex(int position) {
        return favorites.get(position);
    }
}
