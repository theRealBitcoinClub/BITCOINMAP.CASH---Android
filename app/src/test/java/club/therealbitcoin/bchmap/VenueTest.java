package club.therealbitcoin.bchmap;

import com.google.android.gms.maps.model.LatLng;

import junit.framework.Assert;

import org.junit.Test;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;


public class VenueTest {

    @Test
    public void createVenue() {
        String testName = "name";
        int testIconRes = R.drawable.ic_action_add_location;
        int type = 0;
        LatLng cord = new LatLng(3.4,5.6);
        String placesId = "4543tdfg34";
        int rev = 234;
        double stras = 4.8;

        Venue venue = new Venue(testName, testIconRes, type, placesId, rev, stras, cord);
        Assert.assertEquals(testName, venue.name);
        Assert.assertNotSame(testName + "fdsfds", venue.name);
        Assert.assertEquals(testIconRes, venue.iconRes);
        Assert.assertEquals(type, venue.type);
        Assert.assertEquals(placesId, venue.placesId);
        Assert.assertEquals(rev, venue.reviews);
        Assert.assertEquals(stras, venue.stars);
    }
}
