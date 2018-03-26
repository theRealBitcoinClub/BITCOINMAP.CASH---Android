package club.therealbitcoin.bchmap;
 
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
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
        View view = inflater.inflate(R.layout.marker_detail_fragment, container, true);
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

		clickedRouteButton(venue, btn_route);
		clickedShareButton(dialog);

		final View btn_favo = dialog.findViewById(R.id.dialog_button_favo);

		Context ctx = getContext();
		if (venue.isFavorite(ctx)) {
			switchColor(btn_favo, true);
			isFavo = true;
		}

		clickedFavoButton(venue, btn_favo, ctx);
	}

	private void clickedRouteButton(Venue venue, View btn_route) {
		btn_route.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getContext(), getString(R.string.toast_route_button), Toast.LENGTH_SHORT).show();
				switchColor(btn_route, true);
				openMapsRoute(venue.placesId);
				resetColorWithDelay(btn_route);
			}

		});
	}

	private void clickedShareButton(View dialog) {
		final View btn_share = dialog.findViewById(R.id.dialog_button_share);
		btn_share.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getContext(), getString(R.string.toast_sharing_venue), Toast.LENGTH_SHORT).show();
				switchColor(btn_share, true);
				shareDeepLink();
				resetColorWithDelay(btn_share);
			}
		});
	}

	private void clickedFavoButton(Venue venue, View btn_favo, Context ctx) {
		btn_favo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isFavo) {
					Toast.makeText(ctx, getString(R.string.toast_removed_favorite) + " " + venue.name, Toast.LENGTH_SHORT).show();
					VenueFacade.getInstance().removeFavoriteVenue(venue);
					isFavo = false;
				} else {
					Toast.makeText(ctx,getString(R.string.toast_added_favorite) + " " +  venue.name,Toast.LENGTH_SHORT).show();
					VenueFacade.getInstance().addFavoriteVenue(venue);
					isFavo = true;
				}

				switchColor(btn_favo, isFavo);
				venue.setFavorite(isFavo, ctx);
				cb.initAllListViews();
			}
		});
	}


	private void animateColorChange(View view, String fromColor, String toColor) {
		final float[] from = new float[3],
				to =   new float[3];

		Color.colorToHSV(Color.parseColor(fromColor), from);   // from white
		Color.colorToHSV(Color.parseColor(toColor), to);     // to red

		ValueAnimator anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
		anim.setDuration(300);                              // for 300 ms

		final float[] hsv  = new float[3];                  // transition color
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
			@Override public void onAnimationUpdate(ValueAnimator animation) {
				// Transition along each axis of HSV (hue, saturation, value)
				hsv[0] = from[0] + (to[0] - from[0])*animation.getAnimatedFraction();
				hsv[1] = from[1] + (to[1] - from[1])*animation.getAnimatedFraction();
				hsv[2] = from[2] + (to[2] - from[2])*animation.getAnimatedFraction();

				view.setBackgroundColor(Color.HSVToColor(hsv));
			}
		});

		anim.start();
	}

	private void resetColorWithDelay(View btn_route) {
		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                switchColor(btn_route, false);
            }
        },250L);
	}

	@Override
    public void onDestroy() {
        super.onDestroy();
        cb = null;
    }

	private boolean isFavo = false;

	private void switchColor(View btn, boolean onOff) {
		String accent = "#79bf8c";
		String primary = "#549c68";
	    if (onOff) {
			animateColorChange(btn, primary, accent);
			//btn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
		}
	    else animateColorChange(btn, accent, primary);
		//btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

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