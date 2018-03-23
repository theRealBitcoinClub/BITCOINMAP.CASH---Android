package club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model;

import club.therealbitcoin.bchmap.R;

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

    public static int getIconResource(int type) {
        if (type == VenueType.ATM.getIndex())
            return R.drawable.ic_map_bitcoin;
        if (type == VenueType.Food.getIndex())
            return R.drawable.ic_map_food;
        if (type == VenueType.Super.getIndex())
            return R.drawable.ic_map_shop;
        if (type == VenueType.Bar.getIndex())
            return R.drawable.ic_map_bar;
        if (type == VenueType.Spa.getIndex())
            return R.drawable.ic_map_spa;

        return -1;
    }

    public static int getTranslatedType(int type) {
        if (VenueType.ATM.getIndex() == type)
            return R.string.type_atm;

        if (VenueType.Food.getIndex() == type)
            return R.string.type_food;

        if (VenueType.Super.getIndex() == type)
            return R.string.type_super;

        if (VenueType.Bar.getIndex() == type)
            return R.string.type_bar;

        if (VenueType.Spa.getIndex() == type)
            return R.string.type_spa;

        return -1;
    }
}
