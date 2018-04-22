package club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import club.therealbitcoin.bchmap.R;
import club.therealbitcoin.bchmap.persistence.WebService;

public class Venue implements Parcelable {
    public int favoListIndex =-1;
    public String name;
    public int iconRes;
    public int type;
    public String placesId;
    public static String BASE_URI = "http://therealbitcoin.club/";
    public static String IMG_FOLDER =  BASE_URI + "img/app/";
    public int reviews;
    public double stars;
    private Boolean isFavorite = null;
    LatLng coordinates;
    private boolean filtered = false;
    public int listIndex = -1;
    public View listItem;
    private int discountLevel = -1;
    private String[] attributes;

    public String[] getAttributes() {
        return attributes;
    }

    protected Venue(Parcel in) {
        discountLevel = in.readInt();
        favoListIndex = in.readInt();
        listIndex = in.readInt();
        name = in.readString();
        iconRes = in.readInt();
        type = in.readInt();
        placesId = in.readString();
        reviews = in.readInt();
        stars = in.readDouble();
        in.readStringArray(attributes);
        byte tmpIsFavorite = in.readByte();
        isFavorite = tmpIsFavorite == 0 ? null : tmpIsFavorite == 1;
        coordinates = in.readParcelable(LatLng.class.getClassLoader());
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

    public void setFavorite(Boolean favorite, Context ctx) {
        isFavorite = favorite;

        SharedPreferences sharedPref = ctx.getSharedPreferences(
                ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        sharedPref.edit().putString(placesId,null);
    }

    @Nullable
    public Boolean isFavorite(Context ctx) {
        if (isFavorite == null && ctx != null) {
            SharedPreferences sharedPref = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            return sharedPref.contains(placesId);
        }

        return isFavorite != null ? isFavorite : false;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public Venue(String name, int iconRes, int type, String placesId, int rev, double stras, LatLng cord, int dscnt, String[] attr) {
        this.discountLevel = dscnt;
        this.name = name;
        this.iconRes = iconRes;
        this.type = type;
        this.placesId = placesId;
        this.stars = stras;
        this.reviews = rev;
        this.coordinates = cord;
        this.attributes = attr;
    }

    public static Venue createInstance(JSONObject venue) throws JSONException {
        String name = venue.getString(VenueJson.name.toString());
        double stars = venue.getDouble(VenueJson.score.toString());
        int rev = venue.getInt(VenueJson.reviews.toString());
        LatLng latLng = WebService.parseLatLng(venue);
        int type = venue.getInt(VenueJson.type.toString());
        String placesId = venue.getString(VenueJson.placesId.toString());
        int dscnt = venue.getInt(VenueJson.discount.toString());
        String[] atribs = parseAttributes(venue);
        return new Venue(name, VenueType.getIconResource(type), type, placesId, rev, stars, latLng, dscnt, atribs);
    }

    private static String[] parseAttributes(JSONObject vJson) throws JSONException {
        String attribs = vJson.getString(VenueJson.attributes.toString());
        if (attribs == null)
            return null;

        String[] split = attribs.split(",");
        return split;
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
        appendData(sb, VenueJson.score.toString(), stars);
        appendData(sb, VenueJson.discount.toString(), discountLevel, true);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(discountLevel);
        dest.writeInt(favoListIndex);
        dest.writeInt(listIndex);
        dest.writeString(name);
        dest.writeInt(iconRes);
        dest.writeInt(type);
        dest.writeString(placesId);
        dest.writeInt(reviews);
        dest.writeDouble(stars);
        dest.writeByte((byte) (isFavorite == null ? 0 : isFavorite ? 1 : 2));
        dest.writeParcelable(coordinates, flags);
    }

    public void setFiltered(boolean b) {
        filtered = b;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public int getDiscountText() {
        switch (discountLevel) {
            case 0: return R.string.discount0;
            case 1: return R.string.discount1;
            case 2: return R.string.discount2;
            case 3: return R.string.discount3;
            default: return -1;
        }
    }
}