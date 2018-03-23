package club.therealbitcoin.bchmap;


        import android.content.Context;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;

        import java.util.ArrayList;
        import java.util.List;

        import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;
        import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.VenueType;
        import club.therealbitcoin.bchmap.interfaces.UpdateActivityCallback;
        import club.therealbitcoin.bchmap.persistence.VenueFacade;

public class VenuesListFragment extends android.support.v4.app.ListFragment implements View.OnClickListener {

    private static final String BUNDLE = "bvdsfedss";
    private static String
            ONLY_FAVOS = "ONLY_FAVOS";
    private static UpdateActivityCallback callback;
    private boolean showOnlyFavos;

    public static VenuesListFragment newInstance(boolean onlyFavs, UpdateActivityCallback cb) {
        VenuesListFragment.callback = cb;
        Log.d("TRBC","VenuesListFragment, newInstance");
        Bundle args = new Bundle();
        args.putBoolean(ONLY_FAVOS,onlyFavs);
        VenuesListFragment fragment = new VenuesListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callback = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initAdapter(showOnlyFavos);
        if (getArguments() != null && getArguments().getBoolean(ONLY_FAVOS)) {
            showOnlyFavos = true;
        }
        Log.d("TRBC","VenuesListFragment, onActivityCreated" + showOnlyFavos);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TRBC","VenuesListFragment, onResume" + showOnlyFavos);
        //initAdapter(showOnlyFavos);
    }

    public void initAdapter(boolean onlyFavorites) {
        Log.d("TRBC","VenuesListFragment, initAdapter favos:" + onlyFavorites);

        int itemRes;
        ArrayList<String> venueTitles = null;
        if (onlyFavorites) {
            itemRes = R.layout.list_item_favos;
            venueTitles = VenueFacade.getInstance().getFavoTitles();
        } else {
            itemRes = R.layout.list_item;
            venueTitles = VenueFacade.getInstance().getVenueTitles();
        }
        if (venueTitles != null && getActivity() != null) {
            setListAdapter(new PopupAdapter(venueTitles, itemRes, getActivity()));
            Log.d("TRBC","venuetitles size:" + venueTitles.size() + " getListAdapter().getCount();" + getListAdapter().getCount());
        }
    }

    @Override
    public void onListItemClick(ListView listView, View v, int position, long id) {
        Venue venue = VenueFacade.getInstance().findVenueByIndex(position);


        MarkerDetailsFragment.newInstance(venue, callback).show(getFragmentManager(),"MARKERDIALOG");
        // Show a toast if the user clicks on an item
        //Toast.makeText(getActivity(), "Item Clicked: " + item, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(final View view) {
        final Venue v = (Venue) view.getTag();
        Context ctx = getContext();
        Log.d("TRBC","onClick item" + v);

        if (showOnlyFavos) {
            Log.d("TRBC","onClick item" + v);
            VenueFacade.getInstance().removeFavoriteVenue(v);
            v.setFavorite(false,ctx);
            callback.updateBothListViews();
        } else {
            handleOnClickListView(v, ctx, view);
        }
    }

    private void handleOnClickListView(Venue item, Context ctx, View button) {
        if (!item.isFavorite(ctx)) {
            item.setFavorite(true, ctx);
            //Toast.makeText(ctx, getString(R.string.toast_added_favorite) + item.name, Toast.LENGTH_SHORT).show();
            VenueFacade.getInstance().addFavoriteVenue(item);
        } else {
            item.setFavorite(false, ctx);
            //Toast.makeText(ctx, getString(R.string.toast_removed_favorite) + item.name, Toast.LENGTH_SHORT).show();
            VenueFacade.getInstance().removeFavoriteVenue(item);
        }

        callback.updateFavosList();
        updateFavoriteSymbol(button,item);
    }


class PopupAdapter extends ArrayAdapter<String> {

    PopupAdapter(List<String> venues, int listItemResource, Context ctx) {
        super(ctx, listItemResource, android.R.id.text1, venues);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        View view = super.getView(position, convertView, container);
        Log.d("TRBC", "VenuesListFragment, getView" + showOnlyFavos + position);

        View button = view.findViewById(R.id.list_item_button);
        View icon = view.findViewById(R.id.list_item_icon);

        Venue venue;

        if (!showOnlyFavos) {
            venue =VenueFacade.getInstance().findVenueByIndex(position);
        } else {
            venue =VenueFacade.getInstance().findFavoByIndex(position);
        }

        int iconResource = VenueType.getIconResource(venue.type);
        icon.setBackgroundResource(iconResource);

        venue.tempIndex = position;
        button.setTag(venue);

        if (!showOnlyFavos) {
            Log.d("TRBC", "showOnlyFavos: " + showOnlyFavos + position);
            updateFavoriteSymbol(button, venue);
        }

            button.setOnClickListener(VenuesListFragment.this);
            return view;
    }

}
    private void updateFavoriteSymbol(View button, Venue venue) {
        if (venue.isFavorite(getContext())) {
            button.setBackgroundResource(R.drawable.ic_action_favorite);
        } else {
            button.setBackgroundResource(R.drawable.ic_action_favorite_border);
        }
    }

}
