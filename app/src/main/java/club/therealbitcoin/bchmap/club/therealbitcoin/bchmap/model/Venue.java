package club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import club.therealbitcoin.bchmap.persistence.JsonParser;

public class Venue implements Parcelable {
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
    private static final String SHARED_PREF_ID = "SHARED_PREF_ID";
    private static String BASE_URI = "https://bitcoinmap.cash/";
    public static String IMG_FOLDER = BASE_URI + "img/app/";
    public int favoListIndex = -1;
    public String name;
    public int iconRes;
    public int type;
    public String id;
    public int reviews;
    public double stars;
    public String location;
    public int listIndex = -1;
    public View listItem;
    private Boolean isFavorite = null;
    private LatLng coordinates;
    private int discountLevel;
    private String[] attributes;

    public static Venue createInstance(JSONObject venue) throws JSONException {
        String loc = venue.getString(VenueJson.location.toString());
        String name = venue.getString(VenueJson.name.toString());
        double stars = venue.getDouble(VenueJson.score.toString());
        int rev = venue.getInt(VenueJson.reviews.toString());
        LatLng latLng = JsonParser.parseLatLng(venue);
        int type = venue.getInt(VenueJson.type.toString());
        String id = venue.getString(VenueJson.id.toString());
        int dscnt = venue.getInt(VenueJson.discount.toString());
        String[] atribs = parseAttributes(venue);
        return new Venue(name, VenueType.getIconResource(type), type, id, rev, stars, latLng, dscnt, atribs, loc);
    }

    //constructor is public for testing purpose only
    public Venue(String name, int iconRes, int type, String placesId, int rev, double stras, LatLng cord, int dscnt, String[] attr, String loc) {
        this.location = loc;
        this.discountLevel = dscnt;
        this.name = name;
        this.iconRes = iconRes;
        this.type = type;
        this.id = placesId;
        this.stars = stras;
        this.reviews = rev;
        this.coordinates = cord;
        this.attributes = attr;
    }

    protected Venue(Parcel in) {
        location = in.readString();
        in.readStringArray(attributes);
        discountLevel = in.readInt();
        favoListIndex = in.readInt();
        listIndex = in.readInt();
        name = in.readString();
        iconRes = in.readInt();
        type = in.readInt();
        id = in.readString();
        reviews = in.readInt();
        stars = in.readDouble();
        byte tmpIsFavorite = in.readByte();
        isFavorite = tmpIsFavorite == 0 ? null : tmpIsFavorite == 1;
        coordinates = in.readParcelable(LatLng.class.getClassLoader());
    }

    private static String[] parseAttributes(JSONObject vJson) {
        String attribs;
        try {
            attribs = vJson.getString(VenueJson.attributes.toString());
            if (attribs == null)
                return null;
        } catch (JSONException e) {
            return null;
        }

        return attribs.split(",");
    }

    public String[] getAttributes() {
        return attributes;
    }

    public void setFavorite(Boolean favorite, Context ctx) {
        isFavorite = favorite;

        SharedPreferences sharedPref = ctx.getSharedPreferences(
                SHARED_PREF_ID, Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean(id, favorite).apply();
    }

    public boolean isFavorite(Context ctx) {
        if (isFavorite == null && ctx != null) {
            SharedPreferences sharedPref = ctx.getSharedPreferences(
                    SHARED_PREF_ID, Context.MODE_PRIVATE);
            isFavorite = sharedPref.getBoolean(id, false);
        }

        return isFavorite != null ? isFavorite : false;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    @Override
    public String toString() {
        return toJson();
    }

    private String toJson() {
        StringBuilder sb = new StringBuilder("{\"");
        appendData(sb, VenueJson.id.toString(), id);
        appendData(sb, VenueJson.lat.toString(), coordinates.latitude);
        appendData(sb, VenueJson.lon.toString(), coordinates.longitude);
        appendData(sb, VenueJson.name.toString(), name);
        appendData(sb, VenueJson.type.toString(), type);
        appendData(sb, VenueJson.reviews.toString(), reviews);
        appendData(sb, VenueJson.score.toString(), stars);
        appendData(sb, VenueJson.discount.toString(), discountLevel, true);
        appendData(sb, VenueJson.attributes.toString(), attributes, true);
        appendData(sb, VenueJson.location.toString(), location, true);
        sb.append("}");
        return sb.toString();
    }

    private void appendData(StringBuilder builder, String param, Object value) {
        appendData(builder, param, value, false);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Venue venue = (Venue) o;

        return id.equals(venue.id);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name != null ? name.hashCode() : 0;
        result = 31 * result + iconRes;
        result = 31 * result + type;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + reviews;
        temp = Double.doubleToLongBits(stars);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (isFavorite != null ? isFavorite.hashCode() : 0);
        result = 31 * result + (coordinates != null ? coordinates.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(location);
        dest.writeStringArray(attributes);
        dest.writeInt(discountLevel);
        dest.writeInt(favoListIndex);
        dest.writeInt(listIndex);
        dest.writeString(name);
        dest.writeInt(iconRes);
        dest.writeInt(type);
        dest.writeString(id);
        dest.writeInt(reviews);
        dest.writeDouble(stars);
        dest.writeByte((byte) (isFavorite == null ? 0 : isFavorite ? 1 : 2));
        dest.writeParcelable(coordinates, flags);
    }

    /*public int getDiscountText() {
        switch (discountLevel) {
            case 0: return R.string.discount0;
            case 1: return R.string.discount1;
            case 2: return R.string.discount2;
            case 3: return R.string.discount3;
            case 4: return R.string.discount4;
            case 5: return R.string.discount5;
            default: return -1;
        }
    }*/
}