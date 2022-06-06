package club.therealbitcoin.bchmap;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.ListFragment;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;
import club.therealbitcoin.bchmap.interfaces.UpdateActivityCallback;
import club.therealbitcoin.bchmap.persistence.VenueFacade;

public class VenuesListFragment extends ListFragment implements View.OnClickListener {

    private static final String BUNDLE = "bvdsfedss";
    private static String
            ONLY_FAVOS = "ONLY_FAVOS";
    private static String
            LAT = "LAT";
    private static String
            LNG = "LGN";
    private UpdateActivityCallback callback;
    private boolean showOnlyFavos;
    private double latitude = -1;
    private double longitude = -1;

    public static VenuesListFragment newInstance(LatLng coordinates, boolean onlyFavs, UpdateActivityCallback cb) {
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
        initAdapter(showOnlyFavos);
    }

    public void initAdapter(boolean onlyFavorites) {
        switchBackground();

        int itemRes;
        List<String> venueTitles = null;
        if (onlyFavorites) {
            itemRes = R.layout.list_item_favos;
            venueTitles = VenueFacade.getInstance().getFavoTitles();
        } else {
            itemRes = R.layout.list_item;
            venueTitles = VenueFacade.getInstance().getVenueTitles();
        }
        if (venueTitles != null && getActivity() != null) {
            setListAdapter(new PopupAdapter(venueTitles, itemRes, getActivity()));
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

        if (showOnlyFavos) {
            handleOnClickFavoView(v, ctx, view);
        } else {
            handleOnClickListView(v, ctx, view);
        }
    }

    private void handleOnClickFavoView(Venue v, Context ctx, View view) {
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
            VenueFacade.getInstance().addFavoriteVenue(item, getContext());
        } else {
            item.setFavorite(false, ctx);
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

    public static Float calcDistancToUserLocation(LatLng userPosition, LatLng coordTarget) {
        if (userPosition == null || coordTarget == null)
            return -1f;

        return calcDistancBetweenTwoPoints(userPosition.latitude, userPosition.longitude, coordTarget.latitude, coordTarget.longitude);
    }

    static Float calcDistancBetweenTwoPoints(double lat1, double lon1, double lat2, double lon2) {
        float[] distance = new float[2];
        Location.distanceBetween(lat1, lon1, lat2, lon2, distance);
        return distance[0];
    }

    private static class ViewHolder {
        private final TextView title;
        private final TextView location;
        private final TextView distance;
        private final TextView rating;
        private final TextView coinz;
        private final View clickArea;
        private final View icon;
        private final View button;

        private ViewHolder(TextView title, TextView location, TextView distance, TextView rating, View clickArea, View button, View icon, TextView coinz) {
            this.title = title;
            this.location = location;
            this.distance = distance;
            this.rating = rating;
            this.clickArea = clickArea;
            this.button = button;
            this.icon = icon;
            this.coinz = coinz;
        }
    }

    class PopupAdapter extends ArrayAdapter<String> {
        PopupAdapter(List<String> venues, int listItemResource, Context ctx) {
            super(ctx, listItemResource, android.R.id.text1, venues);
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, ViewGroup container) {
            ViewHolder holder;
            View view = super.getView(position, convertView, container);
            if (convertView == null) {
                holder = new ViewHolder(view.findViewById(android.R.id.text1), view.findViewById(R.id.location), view.findViewById(R.id.distance), view.findViewById(R.id.rating), view.findViewById(R.id.list_item_click_area), view.findViewById(R.id.list_item_button), view.findViewById(R.id.list_item_icon),view.findViewById(R.id.coinz));
                view.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Venue venue = getVenueByIndex(position);

            if (VenueFacade.getInstance().getTheme(getActivity()) != 0) {
                view.setBackgroundColor(getResources().getColor(R.color.colorListItemDark));
                holder.title.setTextColor(getResources().getColor(R.color.colorTextDarkTheme));
                holder.distance.setTextColor(getResources().getColor(R.color.colorTextDarkTheme));
                holder.coinz.setTextColor(getResources().getColor(R.color.colorTextDarkTheme));
                holder.rating.setTextColor(getResources().getColor(R.color.colorTextDarkTheme));
                if (holder.location != null) //can be null on favo view
                    holder.location.setTextColor(getResources().getColor(R.color.colorTextDarkTheme));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.colorTextDarkTheme));
                holder.title.setTextColor(getResources().getColor(R.color.colorBackGroundDark));
                holder.distance.setTextColor(getResources().getColor(R.color.colorBackGroundDark));
                holder.coinz.setTextColor(getResources().getColor(R.color.colorBackGroundDark));
                holder.rating.setTextColor(getResources().getColor(R.color.colorBackGroundDark));
                if (holder.location != null) //can be null on favo view
                    holder.location.setTextColor(getResources().getColor(R.color.colorBackGroundDark));
            }

            String distanceText = getDistanceText(venue.getCoordinates());
            holder.distance.setText(distanceText);

            StringBuilder builder = new StringBuilder();
            VenuesListFragment.appendCoins(venue, builder);
            holder.coinz.setText(builder.toString());

            String satisfaction = getSatisfactionIndex(venue.reviews, venue.stars);
            if (satisfaction != null)
                holder.rating.setText(satisfaction);
            else
                holder.rating.setText("");

            venue.listItem = view;
            optimizeTouchArea(holder);
            //int iconResource = VenueType.getIconResource(venue.type);
            holder.icon.setBackgroundResource(venue.iconResListItemView);
            if (holder.location != null) //can be null on favo view
                holder.location.setText(venue.location);

            holder.button.setTag(venue);
            //this is necessary for marquee to work
            //holder.title.setSelected(true);

            if (showOnlyFavos) {
                venue.favoListIndex = position;
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
            Float distanceInFloat = calcDistancBetweenTwoPoints(latitude, longitude, coordinates.latitude, coordinates.longitude);
            int distanceInt = distanceInFloat.intValue();
            if (distanceInt < 1000) {
                return distanceInt + " meter";
            }
            return (String.format(Locale.ENGLISH, "%.2f", distanceInFloat / 1000.0)) + " km";
        }
    }

    public static String getSatisfactionIndex(int total, double stars) {
        if(total < 1)
            return null;

        double maximum = total * 5.0;
        Double index = (total * stars) / maximum;
        int percentage = Double.valueOf(index * 100.0).intValue();

        StringBuilder result = new StringBuilder();
        if (percentage > 90) {
            result.append("\uD83D\uDE0D ");
        } else if (percentage > 80) {
            result.append("\uD83D\uDE01 ");
        } else if (percentage > 70) {
            result.append("\uD83D\uDE10 ");
        } else {
            result.append("☹️ ");
        }
        result.append(percentage);
        result.append("%");

        return result.toString();
    }
    public static void appendCoins(Venue venue, StringBuilder builder) {
        builder.append(" ");
        if (venue.coins == null) {
            builder.append("");
        } else {
            String[] splitCoins = venue.coins.split(",");

            int i=0;
            for (String coin : splitCoins) {
                if (i++ != 0)
                    builder.append(", ");

                builder.append(parseCoinToText(coin));
            }
        }
    }

    public static String parseCoinToText(String coin) {
        int c = Integer.parseInt(coin);
        switch (c) {
            case 0: return "BCH";
            case 1: return "DASH";
            case 2: return "BTC";
            case 3: return "USDT";
            case 4: return "BUSD";
            case 5: return "FlexUSD";
            case 6: return "DAI";
            case 7: return "RUSD";
            case 8: return "ZEC";
            default: return null;
        }
    }

    private String parseBrandToText(String coin) {
        int c = Integer.parseInt(coin);
        switch (c) {
            case 0: return "TRBC";
            case 1: return "";
            case 2: return "";
            case 3: return "";
            case 4: return "";
            case 5: return "";
            case 6: return "";
            case 7: return "";
            case 8: return "";
            case 9: return "";
            case 10: return "";
            case 11: return "";
            case 12: return "";
            case 13: return "";
            case 14: return "";
            case 15: return "";
            case 16: return "";
            case 17: return "";
            case 18: return "";
            case 19: return "";
            case 20: return "";
            case 21: return "";
            case 22: return "Other";
            default: return null;
        }
    }

    public static void appendReviews(Venue venue, StringBuilder builder) {
        String satisfaction = VenuesListFragment.getSatisfactionIndex(venue.reviews, venue.stars);
        if (satisfaction == null)
            return;

        builder.append(" ");
        builder.append(satisfaction);
        builder.append(" ");
    }

}
