package club.therealbitcoin.bchmap;


        import android.content.Context;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;
        import android.widget.Toast;

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
        Log.d("TRBC","VenuesListFragment, newInstance only favos:" + onlyFavs);
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
        if (getArguments() != null && getArguments().getBoolean(ONLY_FAVOS)) {
            showOnlyFavos = true;
        }
        setEmptyText(getResources().getString(R.string.favo_list_empty));
        Log.d("TRBC","VenuesListFragment, onActivityCreated: onlyFavos:" + showOnlyFavos);
    }

    @Override
    public void onResume() {
        super.onResume();
        initAdapter(showOnlyFavos);
        Log.d("TRBC","VenuesListFragment, onResume:  onlyFavos:" + showOnlyFavos);
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
        Venue venue = getVenueByIndex(position);

        MarkerDetailsFragment.newInstance(venue, callback).show(getFragmentManager(),"MARKERDIALOG");
    }

    private Venue getVenueByIndex(int position) {
        Venue venue;
        if (!showOnlyFavos) {
            venue = VenueFacade.getInstance().findVenueByIndex(position);
        } else {
            venue =VenueFacade.getInstance().findFavoByIndex(position);
        }
        return venue;
    }

    @Override
    public void onClick(final View view) {
        final Venue v = (Venue) view.getTag();
        Context ctx = getContext();
        Log.d("TRBC","onClick item" + v);

        if (showOnlyFavos) {
            Toast.makeText(ctx, getString(R.string.toast_removed_favorite) + " " + v.name, Toast.LENGTH_SHORT).show();
            Log.d("TRBC","onClick item showOnlyFavos" + showOnlyFavos + v);
            VenueFacade.getInstance().removeFavoriteVenue(v);
            v.setFavorite(false,ctx);
            callback.initAllListViews();
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

        callback.initFavosList();
        updateFavoriteSymbol(button,item);
    }


class PopupAdapter extends ArrayAdapter<String> {

    PopupAdapter(List<String> venues, int listItemResource, Context ctx) {
        super(ctx, listItemResource, android.R.id.text1, venues);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        Venue venue = getVenueByIndex(position);

        if (venue.isFiltered()) {
            return null;
        }

        View view = super.getView(position, convertView, container);
        Log.d("TRBC", "VenuesListFragment, getView" + showOnlyFavos + position);

        View button = view.findViewById(R.id.list_item_button);
        View icon = view.findViewById(R.id.list_item_icon);

        int iconResource = VenueType.getIconResource(venue.type);
        icon.setBackgroundResource(iconResource);

        //venue.favoListIndex = position;
        button.setTag(venue);

        if (!showOnlyFavos) {
            Log.d("TRBC", "showOnlyFavos: " + showOnlyFavos + position);
            updateFavoriteSymbol(button, venue);
        }

            button.setOnClickListener(VenuesListFragment.this);
            return view;
    }

}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void updateFavoriteSymbol(View button, Venue venue) {
        if (venue.isFavorite(getContext())) {
            /*final AnimationDrawable drawable = new AnimationDrawable();
            final Handler handler = new Handler();

            drawable.addFrame(getResources().getDrawable(R.drawable.ic_action_favorite), 400);
            drawable.addFrame(getResources().getDrawable(R.drawable.ic_action_favorite_border), 400);
            drawable.setOneShot(false);

            button.setBackgroundDrawable(drawable);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawable.start();
                }
            }, 100);*/

            button.setBackgroundResource(R.drawable.ic_action_favorite);
        } else {
            button.setBackgroundResource(R.drawable.ic_action_favorite_border);
        }
    }

}
