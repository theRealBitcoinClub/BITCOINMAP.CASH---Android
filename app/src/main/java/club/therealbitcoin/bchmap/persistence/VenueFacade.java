package club.therealbitcoin.bchmap.persistence;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;

public class VenueFacade {
    private static final VenueFacade ourInstance = new VenueFacade();
    private Map<String, Venue> venuesMap = new HashMap<String,Venue>();
    private ArrayList<Venue> venuesList = new ArrayList<Venue>();
    private ArrayList<String> titles = new ArrayList<String>();
    private ArrayList<Venue> favoVenueList = new ArrayList<Venue>();
    public static final String MY_FAVORITES = "myFavorites";

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

    public void addFavoriteVenue(Venue v, Context ctx, boolean persist) throws IOException {
        Log.d("TRBC","addFavoriteVenue persist:" + persist);
        favoVenueList.add(v);
        if (persist)
            persistFavoriteVenues(ctx);
    }

    public List<Venue> getFavoriteVenues (Context ctx) {
        if (favoVenueList.isEmpty()) {
            try {
                FileInputStream fileInput = ctx.openFileInput(MY_FAVORITES);

                String data = WebService.readJsonFromInputStream(fileInput);
                return WebService.parseVenues(data);
            } catch (FileNotFoundException e) {
                Log.e("TRBC","file not found favorites");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("TRBC","IO EXCEPTION favorites");
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e("TRBC","JSON EXCEPTION favorites");
                e.printStackTrace();
            }
        }

        return favoVenueList;
    }

    private void persistFavoriteVenues(Context ctx) throws IOException {
        StringBuilder sb = new StringBuilder("[");
        boolean isFirstRun = true;

        for (Venue v: favoVenueList) {
            if (!isFirstRun) {
                sb.append(",");
            }
            sb.append(v.toJson());
            isFirstRun = false;
        }

        sb.append("]");
        saveFavoriteVenues(ctx, sb.toString());
    }

    private void saveFavoriteVenues(Context ctx, String fileContents) throws IOException {
        FileOutputStream outputStream = null;

        try {
            outputStream = ctx.openFileOutput(MY_FAVORITES, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null)
                outputStream.close();
        }
    }

    private VenueFacade() {
    }
}
