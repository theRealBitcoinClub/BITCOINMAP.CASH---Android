package club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model;

/**
 * Created by root on 18.03.2018.
 */

public enum VenueJson {
    placesId("p"), lat("x"), lon("y"), name("n"), type("t"), reviews("c"), score("s"), discount("d"), attributes("a"), location("l");

    String code;

    VenueJson (String c) {
        code = c;
    }

    @Override
    public String toString() {
        return getCode();
    }

    public String getCode() {
        return code;
    }
}
