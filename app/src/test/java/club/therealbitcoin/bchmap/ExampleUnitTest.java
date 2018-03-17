package club.therealbitcoin.bchmap;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    /*@Test
    public void testParse() throws Exception {
        double lat = 42.341234;
        double lon = 2.235432;

        LatLng bla = WebService.parseLatLng(new JSONObject("{\"lat\":" + lat + ",\"lon\":" + lon +"}"));
        assertEquals(bla.latitude,lat);
        assertEquals(bla.latitude,lon);
    }*/
}