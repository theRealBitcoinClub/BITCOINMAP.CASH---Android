package club.therealbitcoin.bchmap.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import club.therealbitcoin.bchmap.VenuesListFragment;
import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;
import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.VenueType;

public class VenueFacade {
    public static final String TAG = "TRBC";
    public static final String THEME = "THEME";
    public static final int ONE_HOUR = 60 * 60 * 1000;
    public static final int ONE_DAY = ONE_HOUR * 24;
    private static VenueFacade ourInstance = new VenueFacade();
    private static long nextUpdateClearCache = System.currentTimeMillis() + ONE_HOUR;
    private static String SHARED_PREF = "SDfdsfds";
    boolean hasChangedList = true;
    boolean hasChangedFavoList = true;
    private Map<String, Venue> venuesMap = new HashMap<String, Venue>();
    private List<Venue> venuesList = new ArrayList<Venue>();
    private List<String> titles = new ArrayList<String>();
    private List<Venue> favorites = new ArrayList<Venue>();
    private List<String> titlesFavo = new ArrayList<String>();
    private Map<String, List<Venue>> filteredVenuesMap = new HashMap<String, List<Venue>>();
    private int theme = -1;

    private VenueFacade() {
    }

    public static VenueFacade getInstance() {
        return ourInstance;
    }

    /*
    THIS IS FOR MAKING TESTING EASIER ONLY USE IN TESTS
     */
    public static VenueFacade createNewFacadeForTesting() {
        ourInstance = new VenueFacade();
        return ourInstance;
    }

    public List<Venue> getVenuesList() {
        return venuesList;
    }

    public boolean isTypeFiltered(int t, Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("filter" + t, false);
    }

    public void restoreFilteredVenues(VenueType t, Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("filter" + t.getIndex(), false).apply();
        List<Venue> venues = filteredVenuesMap.remove("" + t.getIndex());
        if (venues == null || venues.size() == 0)
            return;

        venuesList.addAll(0, venues);
        hasChangedBothLists();
    }

    private void hasChangedBothLists() {
        hasChangedFavoList = true;
        hasChangedList = true;
    }

    public void filterListByType(VenueType t, Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("filter" + t.getIndex(), true).apply();

        int initialSize = venuesList.size();
        for (int i = 0; i < initialSize; i++) {
            if (venuesList.size() == i) {
                break;
            }

            int typeIndex = t.getIndex();
            if (venuesList.get(i).type == typeIndex) {
                List<Venue> filteredVenues = filteredVenuesMap.get("" + typeIndex);
                if (filteredVenues == null || filteredVenues.size() == 0) {
                    filteredVenues = new ArrayList<Venue>();
                    filteredVenuesMap.put("" + typeIndex, filteredVenues);
                }
                Venue filteredVenue = venuesList.remove(i);
                if (filteredVenue != null) {
                    filteredVenues.add(filteredVenue);
                }
                i--;
            }
        }

        hasChangedBothLists();
    }

    private List<Venue> sortListByDistance(LatLng userPosition, List<Venue> venuesList) {
        if (userPosition == null || userPosition.latitude == -1 || userPosition.longitude == -1)
            return venuesList;

        List<Venue> results = new ArrayList<Venue>();
        synchronized (results) {
            for (int i = 0; i < venuesList.size(); i++) {
                Venue currentVenue = venuesList.get(i);
                int y = 0;
                for (; y < results.size(); ) {
                    Venue currentResult = results.get(y);
                    if (calcDistance(userPosition, currentVenue) < calcDistance(userPosition, currentResult)) {
                        break;
                    }
                    y++;
                }
                results.add(y, currentVenue);
            }
        }
        return results;
    }

    private Float calcDistance(LatLng userPosition, Venue currentVenue) {
        return VenuesListFragment.calcDistancToUserLocation(userPosition, currentVenue.getCoordinates());
    }

    public List<String> getVenueTitles(LatLng coords) {
        venuesList = sortListByDistance(coords, venuesList);

        if (hasCachedContent(hasChangedList, titles)) {
            return titles;
        }

        clearAndRefillList(titles, venuesList);
        hasChangedList = false;
        return titles;
    }

    private void clearAndRefillList(List<String> list, List<Venue> sourceList) {
        list.clear();
        for (Venue v : sourceList
        ) {
            list.add(v.name);
        }
    }

    private boolean hasCachedContent(boolean hasChangedList, List<String> titles) {
        return !hasChangedList && titles.size() > 0;
    }

    public List<String> getFavoTitles(LatLng coords) {
        favorites = sortListByDistance(coords, favorites);

        if (hasCachedContent(hasChangedFavoList, titlesFavo)) {
            return titlesFavo;
        }

        clearAndRefillList(titlesFavo, favorites);
        hasChangedFavoList = false;
        return titlesFavo;
    }


    private void addVenue(Venue v, Context ctx, int favoCounter) {
        hasChangedList = true;
        if (venuesMap.put(v.id, v) == null) {
            venuesList.add(v);
        }

        if (v.isFavorite(ctx)) {
            v.favoListIndex = favoCounter;
            favorites.add(v);
        }
    }

    public void addFavoriteVenue(Venue v, Context ctx) {
        v.favoListIndex = favorites.size();
        favorites.add(v);
        v.setFavorite(true, ctx);
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

    public List<Venue> getFavoriteVenues() {
        return favorites;
    }

    public void removeFavoriteVenue(Venue item, Context ctx) {
        if (favorites.size() != 0)
            favorites.remove(item.favoListIndex);
        item.setFavorite(false, ctx);
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

    public void initVenues(List<Venue> venues, Context ctx) {
        VenueFacade.getInstance().clearCache(ctx);

        int favoCounter = -1;
        for (Venue v : venues) {
            if (v.isFavorite(ctx))
                favoCounter++;

            VenueFacade.getInstance().addVenue(v, ctx, favoCounter);
        }
    }
}
