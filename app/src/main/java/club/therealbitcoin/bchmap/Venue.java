package club.therealbitcoin.bchmap;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.enums.VenueType;

public class Venue {
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
}