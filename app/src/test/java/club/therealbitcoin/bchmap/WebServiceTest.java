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
        String venueJson = "[{\"p\":\"ChIJEUo5JceipBIRlw3IsieB6Sg\", \"x\":\"41.406599\", \"y\":\"2.1621726\",\"n\":\"" + testName + "\", \"t\":\"0\",\"c\":\"1\",\"s\":\"5.0\"}\n" +
                ",{\"p\":\"" + testId + "\", \"x\":\"41.4038493\",\"y\":\"2.1583495\",\"n\":\"La Besneta\",\"t\":\"1\",\"c\":\"84\",\"s\":\"4.7\"}\n" +
                ",{\"p\":\"ChIJXSD0a9uipBIRwD4vX_u1enY\", \"x\":\"" + testLat + "\",\"y\":\"2.1738323\",\"n\":\"Molsa Sagrada Familia\",\"t\":\"2\",\"c\":\"11\",\"s\":\"4.2\"}\n" +
                ",{\"p\":\"ChIJY_ZMtr6ipBIRVC0WRH76Txc\", \"x\":\"41.4027627\",\"y\":\"2.1578416\",\"n\":\"Cara B\",\"t\":\"3\",\"c\":\"" + testReviews + "\",\"s\":\"4.1\"}\n" +
                ",{\"p\":\"ChIJh2A7VL2ipBIRIxR3Ylnot50\", \"x\":\"41.4006381\",\"y\":\"2.1537003\",\"n\":\"Sincronia Yoga\",\"t\":\"4\",\"c\":\"18\",\"s\":\"" + testStars + "\"}]";
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
        Assert.assertEquals(20, venues.size());
    }
}
