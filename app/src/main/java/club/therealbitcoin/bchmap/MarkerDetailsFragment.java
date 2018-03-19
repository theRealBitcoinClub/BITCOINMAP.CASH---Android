package club.therealbitcoin.bchmap;
 
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.enums.VenueType;

public class MarkerDetailsFragment extends DialogFragment {

	public static final String PLACES_ID = "placesId";
	public static final String TYPE = "msg";
	public static final String TITLE = "title";
	public static final String ICON_RES = "iconRes";
	private static final String TAG = "TRBCDialog";
	int iconResource;
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.d(TAG,"dfdsf");
		Log.d(TAG,"bun" + getArguments());
		return new AlertDialog.Builder(getActivity())
				// Set Dialog Icon
				.setIcon(getArguments().getInt(ICON_RES))
				// Set Dialog Title
				.setTitle(getArguments().getString(TITLE))
				// Set Dialog Message
				.setMessage(getTranslatedType(getArguments().getInt(TYPE)))
 
				// Positive button
				.setPositiveButton("Route", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Do something else
					}
				})
 
				// Negative Button
				.setNegativeButton("More", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,	int which) {
						// Do something else
					}
				}).create();
	}

	private int getTranslatedType(int type) {
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

	public static MarkerDetailsFragment newInstance(int iconRes, String title, int type, String placesId) {
		MarkerDetailsFragment myFragment = new MarkerDetailsFragment();

		Bundle args = new Bundle();
		args.putInt(ICON_RES, iconRes);
		args.putString(TITLE, title);
		args.putInt(TYPE, type);
		args.putString(PLACES_ID, placesId);
		myFragment.setArguments(args);

		return myFragment;
	}

	public static MarkerDetailsFragment newInstance(Venue v) {
		Log.d(TAG,v.toString());
		return newInstance(v.iconRes,v.name,v.type,v.placesId);
	}
}