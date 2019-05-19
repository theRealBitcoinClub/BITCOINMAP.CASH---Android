package club.therealbitcoin.bchmap;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraHttpSender;
import org.acra.data.StringFormat;
import org.acra.sender.HttpSender;

@AcraCore(buildConfigClass = BuildConfig.class, reportFormat= StringFormat.JSON)
@AcraHttpSender(uri = "https://collector.tracepot.com/c8b40d36",
        httpMethod = HttpSender.Method.POST)
public class MyApplication extends MultiDexApplication {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}