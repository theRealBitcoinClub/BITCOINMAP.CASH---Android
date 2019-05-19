package club.therealbitcoin.bchmap.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public boolean isTypeFiltered(int t, Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("filter"+t, false);
        /*ArrayList<Venue> venues = filteredVenuesMap.get(""+t);
        Log.d(TAG,"isTypeFiltered venues:" + venues);
        if (venues == null || venues.size() == 0)
            return false;

        return true;*/
    }

    public void restoreFilteredVenues(VenueType t, Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("filter"+t.getIndex(), false).apply();
        Log.d(TAG,"restoreFilteredVenues" + t);
        ArrayList<Venue> venues = filteredVenuesMap.remove(""+t.getIndex());
        if (venues == null || venues.size() == 0)
            return;

        /*for (Venue v: venues
             ) {
            v.setFiltered(false);
        }*/

        Log.d(TAG,"restoreFilteredVenues Yep" + t);
        venuesList.addAll(0,venues);
        hasChangedBothLists();
    }

    private void hasChangedBothLists() {
        hasChangedFavoList = true;
        hasChangedList = true;
    }

    public void filterListByType(VenueType t, Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("filter"+t.getIndex(), true).apply();

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
                    //for (VenueType x: VenueType.values()) {
                    filteredVenues = new ArrayList<Venue>();
                        filteredVenuesMap.put(""+typeIndex, filteredVenues);
                    //}
                    //filteredVenues = filteredVenuesMap.get(""+typeIndex);
                }
                Venue filteredVenue = venuesList.remove(i);
                if (filteredVenue != null) {
                    //filteredVenue.setFiltered(true);
                    filteredVenues.add(filteredVenue);
                }
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

    private void addVenue(Venue v, Context ctx, int favoCounter) {
        hasChangedList = true;
        Log.d("TRBC","addVenue" + v);
       if (venuesMap.put(v.id, v) == null) {
           venuesList.add(v);
       }

        if (v.isFavorite(ctx)) {
            Log.d(TAG,"isFavorite true favorcounter:" + favoCounter);
            v.favoListIndex = favoCounter;
            favorites.add(v);
        } else {
            Log.d(TAG,"isFavorite false");
        }
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
        //sharedPreferences.edit().putBoolean(v.id, true).commit();

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
        if (favorites.size() != 0)
            favorites.remove(item.favoListIndex);
        //SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        //sharedPreferences.edit().remove(item.id).commit();
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
        sharedPreferences.edit().putInt(THEME, theme).apply();
    }

    public int getTheme(Context ctx) {
        if (ctx == null)
            return 0;

        if (theme == -1) {
            SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            if (sharedPreferences == null)
                return 0;

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
        Log.d(TAG,"initVenues");
        VenueFacade.getInstance().clearCache(ctx);

        int favoCounter = -1;
        for (Venue v: venues) {
            if (v.isFavorite(ctx))
                favoCounter++;

            VenueFacade.getInstance().addVenue(v, ctx, favoCounter);
        }
    }
}
