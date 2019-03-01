package club.therealbitcoin.bchmap;

import com.google.android.gms.maps.model.LatLng;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.IOException;
import java.util.List;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;
import club.therealbitcoin.bchmap.persistence.VenueFacade;


@RunWith(RobolectricTestRunner.class)
public class VenueTest {
    String testName = "name";
    int testIconRes = R.drawable.ic_action_bitcoin;
    int type = 0;
    LatLng cord = new LatLng(3.4,5.6);
    String placesId = "4543tdfg34";
    int rev = 234;
    double stras = 4.8;

    @Before
    public void setUp () {
        VenueFacade.getInstance().clearCache(RuntimeEnvironment.application);
    }

    @Test
    public void testIsFavorite() {
        Venue v = new Venue(testName, testIconRes, type, placesId, rev, stras, cord, 0, null, null);
        VenueFacade.getInstance().addFavoriteVenue(v, RuntimeEnvironment.application);
        List<Venue> favoriteVenues = VenueFacade.getInstance().getFavoriteVenues();
        Assert.assertEquals(1, favoriteVenues.size());
        v.setFavorite(true, RuntimeEnvironment.application);
        boolean favorite = v.isFavorite(RuntimeEnvironment.application);
        Assert.assertEquals(true, favorite);
    }

    @Test
    public void testEquals() {
        Venue v = new Venue(testName, testIconRes, type, placesId, rev, stras, cord, 0, null, null);
        Venue v2 = new Venue(testName, testIconRes, type, placesId, rev, stras, cord, 0, null, null);
        Assert.assertTrue(v.equals(v2));
        v.placesId = "dfdsfds";
        Assert.assertFalse(v.equals(v2));
    }

    @Test
    public void createVenue() {
        Venue venue = new Venue(testName, testIconRes, type, placesId, rev, stras, cord, 0, null, null);
        Assert.assertEquals(testName, venue.name);
        Assert.assertNotSame(testName + "fdsfds", venue.name);
        Assert.assertEquals(testIconRes, venue.iconRes);
        Assert.assertEquals(type, venue.type);
        Assert.assertEquals(placesId, venue.placesId);
        Assert.assertEquals(rev, venue.reviews);
        Assert.assertEquals(stras, venue.stars);
    }
}
