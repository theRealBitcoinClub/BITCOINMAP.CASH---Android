package club.therealbitcoin.bchmap;

import com.google.android.gms.maps.model.LatLng;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.VenueJson;
import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;
import club.therealbitcoin.bchmap.persistence.WebService;

@RunWith(RobolectricTestRunner.class)
public class WebServiceTest {

    private InputStream openFile(String filename) throws IOException {
        return getClass().getClassLoader().getResourceAsStream(filename);
    }

    @Test
    public void testParseLatLng() throws JSONException {
            double lat = 42.341234;
            double lon = 2.235432;

            LatLng bla = WebService.parseLatLng(new JSONObject("{\""+ VenueJson.lat+"\":" + lat + ",\""+VenueJson.lon+"\":" + lon +"}"));
            Assert.assertEquals(bla.latitude,lat);
            Assert.assertEquals(bla.longitude,lon);
    }

    @Test
    public void testParseVenue() throws JSONException {
        String testName = "The Real Bitcoin Club";
        String testId = "ChIJqQmtTryipBIRe3u-XKQxkmw";
        String testLat = "41.4067273";
        String testReviews = "180";
        String testStars = "4.8";

        String venueJson = "[{\"p\":\"trbc\", \"x\":\"41.406595\", \"y\":\"2.16655\",\"n\":\"" + testName + "\", \"t\":\"99\",\"c\":\"3\",\"s\":\"5.0\", \"d\":\"3\", \"a\":\"0,1,2,34\", \"l\":\"Barcelona, Spain, Europe\"}" +
                ",{\"p\":\"" + testId + "\", \"x\":\"41.4059946\",\"y\":\"2.1567663\",\"n\":\"La Lima - comedor vegano asociacion\",\"t\":\"0\",\"c\":\"9\",\"s\":\"5.0\", \"d\":\"0\", \"a\":\"3,5,6,13\", \"l\":\"Barcelona, Spain, Europe\"}" +
                ",{\"p\":\"pasticceria\", \"x\":\"" + testLat + "\",\"y\":\"2.1559692\",\"n\":\"La Pasticceria di Gracia\",\"t\":\"1\",\"c\":\"32\",\"s\":\"4.6\", \"d\":\"0\", \"a\":\"9,10,12,13\", \"l\":\"Barcelona, Spain, Europe\"}" +
                ",{\"p\":\"lavermu\", \"x\":\"41.4000891\",\"y\":\"2.1568007\",\"n\":\"La Vermu - Bar and Tapas\",\"t\":\"2\",\"c\":\"" + testReviews + "\",\"s\":\"4.2\", \"d\":\"0\", \"a\":\"31,32,33,36\", \"l\":\"Barcelona, Spain, Europe\"}" +
                ",{\"p\":\"ChIJayYeJpaipBIR8ZbTZO4x7dM\", \"x\":\"41.4006901\",\"y\":\"2.1579717\",\"n\":\"Gelatology - We make Gelato great again!\",\"t\":\"1\",\"c\":\"49\",\"s\":\"" + testStars + "\", \"d\":\"1\", \"a\":\"18,29,32,33\", \"l\":\"Barcelona, Spain, Europe\"}]";
        List<Venue> venues = WebService.parseVenues(venueJson);

        Assert.assertEquals(5, venues.size());
        Assert.assertEquals(venues.get(0).name,testName);
        Assert.assertNotSame(venues.get(1).name,testName);
        Assert.assertEquals(venues.get(1).placesId,testId);
        Assert.assertEquals(venues.get(2).getCoordinates().latitude,Double.valueOf(testLat));
        Assert.assertEquals(venues.get(3).reviews,(int)Integer.valueOf(testReviews));
        Assert.assertEquals(venues.get(4).stars,Double.valueOf(testStars));
    }

    @Test
    public void testReadJsonFromInputStream() throws IOException, JSONException {
        String s = WebService.readJsonFromInputStream(openFile("places.json"));
        List<Venue> venues = WebService.parseVenues(s);
        Assert.assertEquals(233, venues.size());
    }
}
