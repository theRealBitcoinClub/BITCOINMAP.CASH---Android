package club.therealbitcoin.bchmap;
 
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.VenueType;
import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;

public class MarkerDetailsFragment extends DialogFragment {

	public static final String PLACES_ID = "placesId";
	public static final String MSG = "msg";
	public static final String TITLE = "title";
	public static final String ICON_RES = "iconRes";
	private static final String TAG = "TRBCDialog";
	private static final String TYPE = "type";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.d(TAG,"dfdsf");
		final Bundle args = getArguments();
		Log.d(TAG,"bun" + args);
		String venueType = getString(getTranslatedType(args.getInt(TYPE)));
		return new AlertDialog.Builder(getActivity())
				.setIcon(args.getInt(ICON_RES))
				.setTitle(args.getString(TITLE))
				.setMessage(venueType + args.getString(MSG))
				// RIGHT button
				.setPositiveButton(getActivity().getString(R.string.share), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent share = new Intent(Intent.ACTION_SEND);
						share.setType("text/plain");
						share.putExtra(Intent.EXTRA_TEXT, getString(R.string.recommendation));
						startActivity(Intent.createChooser(share, getString(R.string.thank_you)));
					}
				})
				// LEFT Button
				.setNegativeButton(getActivity().getString(R.string.info), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,	int which) {
						Intent i = new Intent(Intent.ACTION_VIEW,
								Uri.parse(Venue.DIRECTIONS+ args.getString(PLACES_ID)));
						startActivity(i);
					}
				}).create();
	}

	private static int getTranslatedType(int type) {
		if (VenueType.ATM.getIndex() == type)
			return R.string.type_atm;

		if (VenueType.Food.getIndex() == type)
			return R.string.type_food;

		if (VenueType.Super.getIndex() == type)
			return R.string.type_super;

		if (VenueType.Bar.getIndex() == type)
			return R.string.type_bar;

		if (VenueType.Spa.getIndex() == type)
			return R.string.type_spa;

		return -1;
	}

	public static MarkerDetailsFragment newInstance(int iconRes, String title, int type, String placesId, double stars, int reviews) {
		MarkerDetailsFragment myFragment = new MarkerDetailsFragment();

		Bundle args = new Bundle();
		args.putInt(ICON_RES, iconRes);
		args.putString(TITLE, title);
		args.putInt(TYPE, type);
		args.putString(MSG, ", " + stars + " (" + reviews + ")");
		args.putString(PLACES_ID, placesId);
		myFragment.setArguments(args);

		return myFragment;
	}

	public static MarkerDetailsFragment newInstance(Venue v) {
		Log.d(TAG,v.toString());
		return newInstance(v.iconRes,v.name,v.type,v.placesId, v.stars, v.reviews);
	}
}