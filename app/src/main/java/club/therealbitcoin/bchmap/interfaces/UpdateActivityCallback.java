package club.therealbitcoin.bchmap.interfaces;

import com.google.android.gms.maps.model.LatLng; /**
 * Created by root on 23.03.2018.
 */

public interface UpdateActivityCallback {
    void initAllListViews();
    void initFavosList();
    void initListView();

    void updateCameraPosition(LatLng coordinates);
}
