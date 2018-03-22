package club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import club.therealbitcoin.bchmap.R;
import club.therealbitcoin.bchmap.persistence.VenueFacade;
import club.therealbitcoin.bchmap.persistence.WebService;

public class Venue implements Parcelable{
    public String name;
    public int iconRes;
    public int type;
    public String placesId;
    public static String DIRECTIONS = "http://therealbitcoin.club/";
    public int reviews;
    public double stars;
    private Boolean isFavorite = null;
    LatLng coordinates;

    public void setFavorite(Boolean favorite, Context ctx) {
        isFavorite = favorite;

        SharedPreferences sharedPref = ctx.getSharedPreferences(
                ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        sharedPref.edit().putString(placesId,null);
    }

    public Boolean isFavorite(Context ctx) {
        if (isFavorite == null) {
            SharedPreferences sharedPref = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            return sharedPref.contains(placesId);
        }

        return isFavorite;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public Venue(String name, int iconRes, int type, String placesId, int rev, double stras, LatLng cord) {
        this.name = name;
        this.iconRes = iconRes;
        this.type = type;
        this.placesId = placesId;
        this.stars = stras;
        this.reviews = rev;
        this.coordinates = cord;
    }

    public static Venue createInstance(JSONObject venue) throws JSONException {
        String name = venue.getString(VenueJson.name.toString());
        double stars = venue.getDouble(VenueJson.score.toString());
        int rev = venue.getInt(VenueJson.reviews.toString());
        LatLng latLng = WebService.parseLatLng(venue);
        int type = venue.getInt(VenueJson.type.toString());
        String placesId = venue.getString(VenueJson.placesId.toString());
        return new Venue(name, VenueType.getIconResource(type), type, placesId, rev, stars, latLng);
    }

    protected Venue(Parcel in) {
        name = in.readString();
        iconRes = in.readInt();
        type = in.readInt();
        placesId = in.readString();
        reviews = in.readInt();
        stars = in.readDouble();
    }

    public static final Creator<Venue> CREATOR = new Creator<Venue>() {
        @Override
        public Venue createFromParcel(Parcel in) {
            return new Venue(in);
        }

        @Override
        public Venue[] newArray(int size) {
            return new Venue[size];
        }
    };

    public int getReviews() {
        return reviews;
    }

    public double getStars() {
        return stars;
    }

    public String getName() {
        return name;
    }

    public int getIconRes() {
        return iconRes;
    }

    public int getType() {
        return type;
    }

    public String getPlacesId() {
        return placesId;
    }

    @Override
    public String toString() {
        return "Venue{" +
                "name='" + name + '\'' +
                ", iconRes=" + iconRes +
                ", type=" + type +
                ", placesId='" + placesId + '\'' +
                ", reviews=" + reviews +
                ", stars=" + stars +
                ", coordinates=" + coordinates +
                '}';
    }
    //{"p":"ChIJEUo5JceipBIRlw3IsieB6Sg","x":"41.406599", "y":"2.1621726","n":"The Real Bitcoin Club", "t":"0","c":"1","s":"5.0"}
    public String toJson() {
        StringBuilder sb = new StringBuilder("{\"");
        appendData(sb, VenueJson.placesId.toString(), placesId);
        appendData(sb, VenueJson.lat.toString(), coordinates.latitude);
        appendData(sb, VenueJson.lon.toString(), coordinates.longitude);
        appendData(sb, VenueJson.name.toString(), name);
        appendData(sb, VenueJson.type.toString(), type);
        appendData(sb, VenueJson.reviews.toString(), reviews);
        appendData(sb, VenueJson.score.toString(), stars, true);
        sb.append("}");
        return sb.toString();
    }

    private void appendData (StringBuilder builder, String param, Object value) {
        appendData(builder,param,value,false);
    }

    private void appendData(StringBuilder sb, String param, Object value, boolean isLastValue) {
        sb.append(param);
        sb.append("\":\"");
        sb.append(value);

        if (isLastValue)
            sb.append("\"");
        else
            sb.append("\",\"");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(placesId);
        dest.writeString(name);
        dest.writeInt(iconRes);
        dest.writeInt(type);
        dest.writeInt(reviews);
        dest.writeDouble(stars);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Venue venue = (Venue) o;

        if (placesId.equals(venue.placesId))
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name != null ? name.hashCode() : 0;
        result = 31 * result + iconRes;
        result = 31 * result + type;
        result = 31 * result + (placesId != null ? placesId.hashCode() : 0);
        result = 31 * result + reviews;
        temp = Double.doubleToLongBits(stars);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (isFavorite != null ? isFavorite.hashCode() : 0);
        result = 31 * result + (coordinates != null ? coordinates.hashCode() : 0);
        return result;
    }
}