package club.therealbitcoin.bchmap;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;
import club.therealbitcoin.bchmap.persistence.VenueFacade;

@RunWith(RobolectricTestRunner.class)
public class VenueFacadeTest {

    private Venue v = null;

    @Before
    public void setUp() {
        v = new Venue("name", 1, 2, "jkdhuifew", 0, 4.7, new LatLng(3.4, 4.5), 0, null, null);
    }

    @Test
    public void testAddFavorite() {
        VenueFacade.createNewFacadeForTesting().addFavoriteVenue(v, RuntimeEnvironment.application);
        List<Venue> favoriteVenues = VenueFacade.getInstance().getFavoriteVenues();
        Assert.assertEquals(1, favoriteVenues.size());
    }


    @Test
    public void testAddMoreFavorites() {
        VenueFacade.createNewFacadeForTesting().addFavoriteVenue(v, RuntimeEnvironment.application);
        List<Venue> favoriteVenues = VenueFacade.getInstance().getFavoriteVenues();
        Assert.assertEquals(1, favoriteVenues.size());
    }

    @Test
    public void testGetVenueTitles() {
        List<String> venueTitles = VenueFacade.getInstance().getVenueTitles(null);
        Assert.assertEquals(0, venueTitles.size());
        ArrayList<Venue> venues = new ArrayList<Venue>();
        venues.add(v);
        VenueFacade.getInstance().initVenues(venues, RuntimeEnvironment.application);
        venueTitles = VenueFacade.getInstance().getVenueTitles(null);
        Assert.assertEquals(1, venueTitles.size());
    }
}
