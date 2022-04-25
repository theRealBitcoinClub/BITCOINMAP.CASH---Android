package club.therealbitcoin.bchmap;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("club.therealbitcoin.bchmap", appContext.getPackageName());
    }

    //@Test
    //public void yaya() throws Exception {
    //    ViewInteraction bla = getObject();
    //    assertNotNull(bla);
    //    assertNull(bla);
    //}

    //private ViewInteraction getObject() {
    //   return onView(withText("Sign-in"));
    //}
}
