package club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model;

import club.therealbitcoin.bchmap.R;

public enum VenueType {
    ATM(99), Food(0), Sweet(1), Bar(2), Super(3), Spa(999), Fashion(4), Hotel(5), Tattoo(9999), Place(1337), Cafe(123);

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
        if (type == VenueType.Fashion.getIndex())
            return R.drawable.ic_map_basket;
        if (type == VenueType.Sweet.getIndex())
            return R.drawable.ic_map_sweet;
        if (type == VenueType.Hotel.getIndex())
            return R.drawable.ic_map_hotel;
        if (type == VenueType.Tattoo.getIndex())
            return R.drawable.ic_map_tattoo;
        if (type == VenueType.Place.getIndex())
            return R.drawable.ic_map_place;
        if (type == VenueType.Cafe.getIndex())
            return R.drawable.ic_map_sun;

        return -1;
    }

    public static int getTranslatedType(VenueType t) {
        return getTranslatedType(t.getIndex());
    }

    public static int getTranslatedType(int type) {
        if (VenueType.ATM.getIndex() == type)
            return R.string.type_atm;

        if (VenueType.Food.getIndex() == type)
            return R.string.type_food;

        if (VenueType.Super.getIndex() == type)
            return R.string.type_super;

        if (VenueType.Bar.getIndex() == type || VenueType.Cafe.getIndex() == type)
            return R.string.type_bar;

        if (VenueType.Spa.getIndex() == type)
            return R.string.type_spa;

        if (VenueType.Fashion.getIndex() == type)
            return R.string.type_fashion;

        if (VenueType.Sweet.getIndex() == type)
            return R.string.type_sweet;

        if (VenueType.Hotel.getIndex() == type)
            return R.string.type_hotel;

        if (VenueType.Tattoo.getIndex() == type)
            return R.string.type_tattoo;

        if (VenueType.Place.getIndex() == type)
            return R.string.type_place;

        return -1;
    }
}
