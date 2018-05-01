package club.therealbitcoin.bchmap.persistence;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import club.therealbitcoin.bchmap.interfaces.OnTaskDoneListener;
import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;
import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.VenueJson;

public class WebService extends AsyncTask<String, Void, String> {

    private OnTaskDoneListener onTaskDoneListener;
    private String urlStr = "";

    public WebService(String url, OnTaskDoneListener onTaskDoneListener) {
        this.urlStr = url;
        this.onTaskDoneListener = onTaskDoneListener;
    }

    public static LatLng parseLatLng(JSONObject venue) throws JSONException {
        double lat = venue.getDouble(VenueJson.lat.toString());
        double lon = venue.getDouble(VenueJson.lon.toString());
        return new LatLng(lat,lon);
    }

    public static String readJsonFromInputStream(InputStream is) throws IOException {
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            is.close();
        }

        return writer.toString();
    }

    @Override
    protected String doInBackground(String... params) {
        try {

            URL mUrl = new URL(urlStr);
            HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Content-length", "0");
            httpConnection.setUseCaches(false);
            httpConnection.setAllowUserInteraction(false);
            httpConnection.setConnectTimeout(100000);
            httpConnection.setReadTimeout(100000);

            httpConnection.connect();

            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (onTaskDoneListener != null && s != null) {
            onTaskDoneListener.onTaskDone(s);
        } else
            onTaskDoneListener.onError();
    }

         public static List<Venue> parseVenues(String responseData) throws JSONException {
             //Log.d("TRBC","parseVenues" + responseData);
             JSONArray jsonArray = new JSONArray(responseData);
             List<Venue> venues = new ArrayList<Venue>();

             for (int i=0; i<jsonArray.length(); i++) {
                 Log.d("TRBC","checka:"  +jsonArray.getJSONObject(i));
                 venues.add(Venue.createInstance(jsonArray.getJSONObject(i)));
             }

             return venues;
        }
}