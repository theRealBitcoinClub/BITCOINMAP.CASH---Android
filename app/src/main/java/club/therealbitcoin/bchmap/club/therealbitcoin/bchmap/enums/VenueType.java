package club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.enums;

/**
 * Created by root on 18.03.2018.
 */

public enum VenueType {
    ATM(0), Food(1), Super(2), Bar(3), Spa(4);

    private int index;

    VenueType (int x) {
        index =x;
    }

    public int getIndex() {
        return index;
    }
}
