package club.therealbitcoin.bchmap;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.enums.VenueType;

public class Venue {
    String name;
    int iconRes;
    int type;
    String placesId;
    String directionsUrl;
    String moreInfoUrl;

    public Venue(String name, int iconRes, int type, String placesId) {
        this.name = name;
        this.iconRes = iconRes;
        this.type = type;
        this.placesId = placesId;
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

    public String getDirectionsUrl() {
        return directionsUrl;
    }

    public String getMoreInfoUrl() {
        return moreInfoUrl;
    }

    @Override
    public String toString() {
        return "Venue{" +
                "name='" + name + '\'' +
                ", iconRes=" + iconRes +
                ", type=" + type +
                ", placesId='" + placesId + '\'' +
                ", directionsUrl='" + directionsUrl + '\'' +
                ", moreInfoUrl='" + moreInfoUrl + '\'' +
                '}';
    }
}