package club.therealbitcoin.bchmap.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import club.therealbitcoin.bchmap.BCHMapsActivity;
import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;
import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.VenueType;

public class VenueFacade {
    public static final String TAG = "TRBC";
    public static final String THEME = "THEME";
    private static VenueFacade ourInstance = new VenueFacade();
    private Map<String, Venue> venuesMap = new HashMap<String,Venue>();
    private ArrayList<Venue> venuesList = new ArrayList<Venue>();
    private ArrayList<String> titles = new ArrayList<String>();
    private List<Venue> favorites = new ArrayList<Venue>();
    private ArrayList<String> titlesFavo = new ArrayList<String>();
    private Map<String,ArrayList<Venue>> filteredVenuesMap = new HashMap<String,ArrayList<Venue>>();
    private int theme = -1;
    public static final int ONE_HOUR = 60 * 60 * 1000;
    public static final int ONE_DAY = ONE_HOUR * 24;
    private static long nextUpdateClearCache = System.currentTimeMillis()+ ONE_HOUR;

    public static VenueFacade getInstance() {
        /*if (nextUpdateClearCache < System.currentTimeMillis()) {
            ourInstance = new VenueFacade();
            nextUpdateClearCache = System.currentTimeMillis()+ONE_HOUR;
        }*/
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
    }

    public void restoreFilteredVenues(VenueType t) {
        Log.d(TAG,"restoreFilteredVenues" + t);
        ArrayList<Venue> venues = filteredVenuesMap.remove(""+t.getIndex());
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

    private void addVenue(Venue v, Context ctx) {
        hasChangedList = true;
        Log.d("TRBC","addVenue" + v);
       if (venuesMap.put(v.placesId, v) == null) {
           venuesList.add(v);
       }

        if (v.isFavorite(ctx))
            favorites.add(v);
    }

    public Venue findVenueById(String id) {
        return venuesMap.get(id);
    }

    boolean hasChangedList = true;
    boolean hasChangedFavoList = true;

    private static String SHARED_PREF= "SDfdsfds";

    public void addFavoriteVenue(Venue v, Context ctx) {
        v.favoListIndex = favorites.size();
        Log.d("TRBC","addFavoriteVenue persist:" + v);
        favorites.add(v);
        v.setFavorite(true,ctx);
        //SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        //sharedPreferences.edit().putBoolean(v.placesId, true).commit();

        hasChangedFavoList = true;
    }

    public void clearCache(Context ctx) {
        venuesMap.clear();
        venuesList.clear();
        favorites.clear();
        titlesFavo.clear();
        titles.clear();
        filteredVenuesMap.clear();
        hasChangedFavoList = true;
        hasChangedList = true;
    }

    public List<Venue> getFavoriteVenues () {
        Log.d("TRBC","getFavoriteVenues :");
        return favorites;
    }

    private VenueFacade() {
    }

    public void removeFavoriteVenue(Venue item, Context ctx) {
        Log.d("TRBC","removeFavoriteVenue :" + item + "index:" + item.favoListIndex);
        favorites.remove(item.favoListIndex);
        //SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        //sharedPreferences.edit().remove(item.placesId).commit();
        item.setFavorite(false,ctx);
        hasChangedFavoList = true;
    }

    public Venue findVenueByIndex(int position) {
        return venuesList.get(position);
    }

    public Venue findFavoByIndex(int position) {
        return favorites.get(position);
    }

    public void setTheme(int theme, Context ctx) {
        this.theme = theme;
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(THEME, theme).commit();
    }

    public int getTheme(Context ctx) {
        if (theme == -1) {
            SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            theme = sharedPreferences.getInt(THEME, 0);
        }

        return theme;
    }

    /*
    THIS IS FOR MAKING TESTING EASIER ONLY USE IN TESTS
     */
    public static VenueFacade createNewFacadeForTesting() {
        ourInstance = new VenueFacade();
        return ourInstance;
    }

    public void initVenues(List<Venue> venues, Context ctx) {
        VenueFacade.getInstance().clearCache(ctx);

        for (Venue v: venues) {
            VenueFacade.getInstance().addVenue(v, ctx);
        }
    }
}
