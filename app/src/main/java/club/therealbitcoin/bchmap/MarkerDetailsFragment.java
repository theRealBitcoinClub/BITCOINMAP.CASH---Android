package club.therealbitcoin.bchmap;
 
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.VenueType;
import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;
import club.therealbitcoin.bchmap.interfaces.UpdateActivityCallback;
import club.therealbitcoin.bchmap.persistence.VenueFacade;

public class MarkerDetailsFragment extends DialogFragment {

	public static final String PARCEL_ID = "dsjlkfndsjkf";
	private static final String TAG = "TRBCDialog";
    private static UpdateActivityCallback cb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.marker_detail_fragment, container, false);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        setCancelable(true);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final Venue venue = getArguments().getParcelable(PARCEL_ID);
        initClickListener(venue, view);

        StringBuilder builder = new StringBuilder(getString(R.string.reviews));
        builder.append(" ");
        builder.append(venue.stars);
        builder.append(" (");
        builder.append(venue.reviews);
        builder.append(")");

        ((TextView)view.findViewById(R.id.dialog_reviews)).setText(builder.toString());
        ((TextView)view.findViewById(R.id.dialog_type)).setText(VenueType.getTranslatedType(venue.type));
        ((TextView)view.findViewById(R.id.dialog_header)).setText(venue.name);

        return view;
    }

	private void initClickListener(final Venue venue, View dialog) {
		final View btn_route = dialog.findViewById(R.id.dialog_button_route);
		btn_route.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switchColor(btn_route, true);
				openMapsRoute(venue.placesId);
			}

		});
		final View btn_share = dialog.findViewById(R.id.dialog_button_share);
		btn_share.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switchColor(btn_share, true);
				shareDeepLink();
			}
		});

		final View btn_favo = dialog.findViewById(R.id.dialog_button_favo);
		btn_favo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    if (hasClickedFavo)
			        return;

                switchColor(btn_favo, true);
                Context ctx = getContext();

                    Toast.makeText(ctx,getString(R.string.toast_added_favorite) + " " +  venue.name,Toast.LENGTH_SHORT).show();
					VenueFacade.getInstance().addFavoriteVenue(venue);
					venue.setFavorite(true, ctx);

                    cb.updateBothListViews();
					hasClickedFavo = true;
			}
		});
	}
    @Override
    public void onDestroy() {
        super.onDestroy();
        cb = null;
    }

	private boolean hasClickedFavo = false;

	private void switchColor(View btn, boolean onOff) {
	    if (onOff)
		btn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
	    else btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }

	private void openMapsRoute(String s) {
		Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse(Venue.DIRECTIONS+ s));
		startActivity(i);
	}

	private void shareDeepLink() {
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("text/plain");
		share.putExtra(Intent.EXTRA_TEXT, getString(R.string.recommendation));
		startActivity(Intent.createChooser(share, getString(R.string.thank_you)));
	}

	public static MarkerDetailsFragment newInstance(Venue v, UpdateActivityCallback cb) {
        MarkerDetailsFragment.cb = cb;
        MarkerDetailsFragment myFragment = new MarkerDetailsFragment();
		Bundle args = new Bundle();
		args.putParcelable(PARCEL_ID, v);
		myFragment.setArguments(args);

		return myFragment;
	}
}