package club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.enums;

/**
 * Created by root on 18.03.2018.
 */

public enum Venue {
    placesId("p"), lat("x"), lon("y"), name("n"), type("t");

    String code;

    Venue (String c) {
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
