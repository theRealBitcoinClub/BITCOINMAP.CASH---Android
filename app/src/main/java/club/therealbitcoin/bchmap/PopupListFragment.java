package club.therealbitcoin.bchmap;

/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

        import android.content.Context;
        import android.os.Bundle;
        import android.support.v4.app.ListFragment;
        import android.support.v7.widget.PopupMenu;
        import android.util.Log;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;
        import android.widget.Toast;

        import java.util.ArrayList;
        import java.util.List;

        import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;
        import club.therealbitcoin.bchmap.persistence.VenueFacade;

/**
 * This ListFragment displays a list of cheeses, with a clickable view on each item whichs displays
 * a {@link android.support.v7.widget.PopupMenu PopupMenu} when clicked, allowing the user to
 * remove the item from the list.
 */
public class PopupListFragment extends ListFragment implements View.OnClickListener {

    private static final String BUNDLE = "bvdsfedss";
    private static String
            ONLY_FAVOS = "ONLY_FAVOS";
    private boolean showOnlyFavos;

    public static PopupListFragment newInstance(boolean onlyFavs) {
        Log.d("TRBC","PopupListFragment, newInstance");
        Bundle args = new Bundle();
        args.putBoolean(ONLY_FAVOS,onlyFavs);
        PopupListFragment fragment = new PopupListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments() != null && getArguments().getBoolean(ONLY_FAVOS)) {
            showOnlyFavos = true;
        }
        initAdapter(showOnlyFavos);
    }

    public void initAdapter(boolean onlyFavorites) {
        Log.d("TRBC","PopupListFragment, initAdapter");

        ArrayList<String> venueTitles = null;
        if (onlyFavorites) {
            venueTitles = VenueFacade.getInstance().getFavoTitles(getContext());
        } else {
            venueTitles = VenueFacade.getInstance().getVenueTitles(getContext());
        }
        setListAdapter(new PopupAdapter(venueTitles));
    }

    @Override
    public void onListItemClick(ListView listView, View v, int position, long id) {
        Venue venue = VenueFacade.getInstance().findVenueByIndex(position);


        MarkerDetailsFragment.newInstance(venue).show(getFragmentManager(),"MARKERDIALOG");
        // Show a toast if the user clicks on an item
        //Toast.makeText(getActivity(), "Item Clicked: " + item, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(final View view) {
        final Venue item = (Venue) view.getTag();

        Context ctx = getContext();
        if (!item.isFavorite(ctx)) {
                item.setFavorite(true, ctx);
                Toast.makeText(ctx,getString(R.string.toast_added_favorite) + item.name,Toast.LENGTH_SHORT).show();
                VenueFacade.getInstance().addFavoriteVenue(item, ctx);
            }
            else {
                item.setFavorite(false, ctx);
                Toast.makeText(ctx,getString(R.string.toast_removed_favorite) + item.name,Toast.LENGTH_SHORT).show();
                VenueFacade.getInstance().removeFavoriteVenue(item);
            }

        initAdapter(showOnlyFavos);
        // We need to post a Runnable to show the popup to make sure that the PopupMenu is
        // correctly positioned. The reason being that the view may change position before the
        // PopupMenu is shown.
        /*view.post(new Runnable() {
            @Override
            public void run() {
                showPopupMenu(view);
            }
        });*/
    }

    // BEGIN_INCLUDE(show_popup)
    private void showPopupMenu(View view) {
        final PopupAdapter adapter = (PopupAdapter) getListAdapter();

        // Retrieve the clicked item from view's tag
        final Venue item = (Venue) view.getTag();

        // Create a PopupMenu, giving it the clicked view for an anchor
        PopupMenu popup = new PopupMenu(getActivity(), view);

        // Inflate our menu resource into the PopupMenu's Menu
        popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());

        // Set a listener so we are notified if a menu item is clicked
        /*popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_favo:
                        try {

                        } catch (IOException e) {
                            Log.e("TRBC","CCCCCCCCCCC");
                            e.printStackTrace();
                        }
                        return true;
                }
                return false;
            }
        });

        // Finally show the PopupMenu
        popup.show();*/
    }
    // END_INCLUDE(show_popup)

    /**
     * A simple array adapter that creates a list of cheeses.
     */
    class PopupAdapter extends ArrayAdapter<String> {

        PopupAdapter(List<String> venues) {
            super(getActivity(), R.layout.list_item, android.R.id.text1, venues);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            // Let ArrayAdapter inflate the layout and set the text
            View view = super.getView(position, convertView, container);
            Log.d("TRBC","PopupListFragment, getView" + position);

            // BEGIN_INCLUDE(button_popup)
            // Retrieve the popup button from the inflated view
            View button = view.findViewById(R.id.favorite_button);

            // Set the item as the button's tag so it can be retrieved later
                Venue venue = VenueFacade.getInstance().findVenueByIndex(position);
            button.setTag(venue);

                if (venue.isFavorite(getContext())) {
                    VenueFacade.getInstance().addFavoriteVenue(venue, getContext());
                    button.setBackgroundResource(R.drawable.ic_action_favorite);
                } else {
                    button.setBackgroundResource(R.drawable.ic_action_favorite_border);
                }


            // Set the fragment instance as the OnClickListener
            button.setOnClickListener(PopupListFragment.this);
            // END_INCLUDE(button_popup)

            // Finally return the view to be displayed
            return view;
        }
    }

}
