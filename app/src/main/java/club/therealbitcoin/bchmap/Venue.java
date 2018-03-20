package club.therealbitcoin.bchmap;

import android.os.Parcel;
import android.os.Parcelable;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.enums.VenueJson;
import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.enums.VenueType;

public class Venue implements Parcelable{
    String name;
    int iconRes;
    int type;
    String placesId;
    public static String DIRECTIONS = "http://therealbitcoin.club/";
    int reviews;
    double stars;

    public Venue(String name, int iconRes, int type, String placesId, int rev, double stras) {
        this.name = name;
        this.iconRes = iconRes;
        this.type = type;
        this.placesId = placesId;
        this.stars = stras;
        this.reviews = rev;
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
                '}';
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
}