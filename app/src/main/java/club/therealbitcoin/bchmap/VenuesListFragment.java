package club.therealbitcoin.bchmap;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;
import club.therealbitcoin.bchmap.interfaces.UpdateActivityCallback;
import club.therealbitcoin.bchmap.persistence.VenueFacade;

public class VenuesListFragment extends android.support.v4.app.ListFragment implements View.OnClickListener {

    private static final String BUNDLE = "bvdsfedss";
    private static String
            ONLY_FAVOS = "ONLY_FAVOS";
    private static String
            LAT = "LAT";
    private static String
            LNG = "LGN";
    private UpdateActivityCallback callback;
    private boolean showOnlyFavos;
    private double latitude;
    private double longitude;

    public static VenuesListFragment newInstance(LatLng coordinates, boolean onlyFavs, UpdateActivityCallback cb) {
        Log.d("TRBC", "VenuesListFragment, newInstance only favos:" + onlyFavs);
        Bundle args = new Bundle();
        args.putBoolean(ONLY_FAVOS, onlyFavs);
        if (coordinates != null) {
            args.putDouble(LAT, coordinates.latitude);
            args.putDouble(LNG, coordinates.longitude);
        }
        VenuesListFragment fragment = new VenuesListFragment();
        fragment.callback = cb;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //callback = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArgBoolean(savedInstanceState, ONLY_FAVOS)) {
            showOnlyFavos = true;
        }
        latitude = getArgDouble(savedInstanceState, LAT);
        longitude = getArgDouble(savedInstanceState, LNG);

        if (showOnlyFavos)
            setEmptyText(getResources().getString(R.string.favo_list_empty));
        else
            setEmptyText(getResources().getString(R.string.error_empty_list));

        Log.d("TRBC", "VenuesListFragment, onActivityCreated: onlyFavos:" + showOnlyFavos);
    }

    private boolean getArgBoolean(Bundle savedInstanceState, String key) {
        return (savedInstanceState != null && savedInstanceState.getBoolean(key))
                || (getArguments() != null && getArguments().getBoolean(key));
    }

        private double getArgDouble(Bundle savedInstanceState, String key) {
            double str = getDouble(savedInstanceState, key);
            return str == -1 ? getDouble(getArguments(), key) : -1;
    }

    private double getDouble(Bundle savedInstanceState, String key) {
        return savedInstanceState != null ? savedInstanceState.getDouble(key) : -1;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ONLY_FAVOS, showOnlyFavos);
        outState.putDouble(LAT, latitude);
        outState.putDouble(LNG, longitude);
    }

    @Override
    public void onResume() {
        super.onResume();
        //adapter.notifyDataSetChanged();
        initAdapter(showOnlyFavos);
        Log.d("TRBC", "VenuesListFragment, onResume:  onlyFavos:" + showOnlyFavos);
    }

    public void initAdapter(boolean onlyFavorites) {
        switchBackground();

        Log.d("TRBC", "VenuesListFragment, initAdapter favos:" + onlyFavorites);

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
            setListAdapter(new PopupAdapter(latitude, longitude, venueTitles, itemRes, getActivity()));
            Log.d("TRBC", "venuetitles size:" + venueTitles.size() + " getListAdapter().getCount();" + getListAdapter().getCount());
        }
    }

    private void switchBackground() {
        try {
            if (VenueFacade.getInstance().getTheme(getActivity()) == 0 && getListView() != null)
                getListView().setBackgroundColor(getResources().getColor(android.R.color.white));
            else
                getListView().setBackgroundColor(getResources().getColor(R.color.colorBackGroundDark));
        } catch (IllegalStateException e) {/*catch exception which occurs if initAdapter is called first Time before view is created*/}
    }

    @Override
    public void onListItemClick(ListView listView, View v, int position, long id) {
        Venue venue = getVenueByIndex(position);

        callback.updateCameraPosition(venue.getCoordinates());
        MarkerDetailsFragment.newInstance(venue, callback, false).show(getFragmentManager(), "MARKERDIALOG");
    }

    private Venue getVenueByIndex(int position) {
        Venue venue;
        if (!showOnlyFavos) {
            venue = VenueFacade.getInstance().findVenueByIndex(position);
        } else {
            venue = VenueFacade.getInstance().findFavoByIndex(position);
        }
        return venue;
    }

    @Override
    public void onClick(final View view) {
        final Venue v = (Venue) view.getTag();
        Context ctx = getContext();
        Log.d("TRBC", "onClick item" + v);

        if (showOnlyFavos) {
            handleOnClickFavoView(v, ctx, view);
        } else {
            handleOnClickListView(v, ctx, view);
        }
    }

    private void handleOnClickFavoView(Venue v, Context ctx, View view) {
        Log.d("TRBC", "onClick item showOnlyFavos" + showOnlyFavos + v);
        Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.animation_remove_favorite);
        animation.reset();
        v.listItem.startAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                VenueFacade.getInstance().removeFavoriteVenue(v, getContext());
                callback.initAllListViews();

            }
        }, 300L);
    }

    private void handleOnClickListView(Venue item, Context ctx, View button) {
        if (!item.isFavorite(ctx)) {
            item.setFavorite(true, ctx);
            //Toast.makeText(ctx, getString(R.string.toast_added_favorite) + item.name, Toast.LENGTH_SHORT).show();
            VenueFacade.getInstance().addFavoriteVenue(item, getContext());
        } else {
            item.setFavorite(false, ctx);
            //Toast.makeText(ctx, getString(R.string.toast_removed_favorite) + item.name, Toast.LENGTH_SHORT).show();
            VenueFacade.getInstance().removeFavoriteVenue(item, getContext());
        }

        FavoriteButtonAnimator.updateFavoriteSymbol(getContext(), button, item, true);
        callback.initFavosList();
    }

    private void optimizeTouchArea(ViewHolder holder) {
        View touchArea = holder.clickArea;
        if (touchArea != null) {
            touchArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VenuesListFragment.this.onClick(holder.button);
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private Float getDistancBetweenTwoPoints(double lat1,double lon1,double lat2,double lon2) {
        float[] distance = new float[2];
        Location.distanceBetween( lat1, lon1, lat2, lon2, distance);
        return distance[0];
    }

    private static class ViewHolder {
        public TextView title;
        public TextView location;
        public TextView distance;
        public View clickArea;
        public View icon;
        public View button;

        public ViewHolder(TextView title, TextView location, TextView distance, View clickArea, View button, View icon) {
            this.title = title;
            this.location = location;
            this.distance = distance;
            this.clickArea = clickArea;
            this.button = button;
            this.icon = icon;
        }
    }

    class PopupAdapter extends ArrayAdapter<String> {

        PopupAdapter(double latitude, double longitude, List<String> venues, int listItemResource, Context ctx) {
            super(ctx, listItemResource, android.R.id.text1, venues);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            ViewHolder holder;
            View view = super.getView(position, convertView, container);
            if (convertView == null) {
                holder = new ViewHolder(view.findViewById(android.R.id.text1), view.findViewById(R.id.location), view.findViewById(R.id.distance), view.findViewById(R.id.list_item_click_area), view.findViewById(R.id.list_item_button), view.findViewById(R.id.list_item_icon));
                view.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Venue venue = getVenueByIndex(position);

            if (venue == null) {
                return null;
            }

            //View view = super.getView(position, convertView, container);
            Log.d("TRBC", "VenuesListFragment, getView" + showOnlyFavos + position);

            if (VenueFacade.getInstance().getTheme(getActivity()) != 0) {
                view.setBackgroundColor(getResources().getColor(R.color.colorListItemDark));
                holder.title.setTextColor(getResources().getColor(R.color.colorTextDarkTheme));
                if (holder.location != null) //can be null on favo view
                    holder.location.setTextColor(getResources().getColor(R.color.colorTextDarkTheme));
            }

            String distanceText = getDistanceText(venue.getCoordinates());
            holder.distance.setText(distanceText);
            venue.listItem = view;
            optimizeTouchArea(holder);
            //int iconResource = VenueType.getIconResource(venue.type);
            holder.icon.setBackgroundResource(venue.iconRes);
            if (holder.location != null) //can be null on favo view
                holder.location.setText(venue.location);

            holder.button.setTag(venue);
            //this is necessary for marquee to work
            //holder.title.setSelected(true);

            if (showOnlyFavos) {
                venue.favoListIndex = position;
                Log.d("TRBC", "showOnlyFavos: " + showOnlyFavos + position);
            } else {
                venue.listIndex = position;
                FavoriteButtonAnimator.updateFavoriteSymbol(getContext(), holder.button, venue, false);
            }

            holder.button.setOnClickListener(VenuesListFragment.this);
            return view;
        }

        @NonNull
        private String getDistanceText(LatLng coordinates) {
            if (latitude == -1 || longitude == -1 || coordinates == null) {
                return "";
            }
            Float distanceInFloat = getDistancBetweenTwoPoints(latitude, longitude, coordinates.latitude, coordinates.longitude);
            Integer distanceInt = distanceInFloat.intValue();
            if (distanceInt < 1000) {
                return distanceInt + " meter";
            }
            return (String.format(Locale.ENGLISH, "%.2f", distanceInFloat / 1000.0)) + " km";
        }
    }

}
