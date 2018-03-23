package club.therealbitcoin.bchmap;
 
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.VenueType;
import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;
import club.therealbitcoin.bchmap.persistence.VenueFacade;

public class MarkerDetailsFragment extends DialogFragment {

	public static final String PARCEL_ID = "dsjlkfndsjkf";
	private static final String TAG = "TRBCDialog";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.d(TAG,"onCreateDialog");
		final Venue venue = getArguments().getParcelable(PARCEL_ID);
		Log.d(TAG,"v" + venue);

		Dialog dialog = new Dialog(getContext());
		dialog.setContentView(R.layout.marker_detail_fragment);
		initClickListener(venue, dialog);

		StringBuilder builder = new StringBuilder(getString(R.string.reviews));
		builder.append(venue.stars);
		builder.append(" (");
		builder.append(venue.reviews);
		builder.append(")");

		((TextView)dialog.findViewById(R.id.dialog_reviews)).setText(builder.toString());
		((TextView)dialog.findViewById(R.id.dialog_type)).setText(VenueType.getTranslatedType(venue.type));
		((TextView)dialog.findViewById(R.id.dialog_header)).setText(venue.name);

		return dialog;
	}

	private void initClickListener(final Venue venue, Dialog dialog) {
		final View btn_route = dialog.findViewById(R.id.dialog_button_route);
		btn_route.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switchColor(btn_route);
				openMapsRoute(venue.placesId);
			}

		});
		final View btn_share = dialog.findViewById(R.id.dialog_button_share);
		btn_share.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switchColor(btn_share);
				shareDeepLink();
			}
		});
		final View btn = dialog.findViewById(R.id.dialog_button_favo);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switchColor(btn);
				if (venue.isFavorite(getContext())) {
					VenueFacade.getInstance().removeFavoriteVenue(venue);
				} else {
					VenueFacade.getInstance().addFavoriteVenue(venue);
				}
			}
		});
	}


	private void switchColor(View btn) {
		btn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
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

	public static MarkerDetailsFragment newInstance(Venue v) {
		MarkerDetailsFragment myFragment = new MarkerDetailsFragment();
		Bundle args = new Bundle();
		args.putParcelable(PARCEL_ID, v);
		myFragment.setArguments(args);

		return myFragment;
	}
}