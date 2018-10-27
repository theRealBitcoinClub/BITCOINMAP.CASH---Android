package club.therealbitcoin.bchmap;
 
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.VenueType;
import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;
import club.therealbitcoin.bchmap.interfaces.AnimatorEndAbstract;
import club.therealbitcoin.bchmap.interfaces.UpdateActivityCallback;
import club.therealbitcoin.bchmap.persistence.VenueFacade;

public class MarkerDetailsFragment extends DialogFragment implements View.OnClickListener {

	public static final String PARCEL_ID = "dsjlkfndsjkf";
	private static final String TAG = "TRBCDialog";
	private static final String IS_ON_MAP_VIEW = "dfdsfn34fn";
	private static UpdateActivityCallback cb;
	private int accentColor;
	private int primaryColor;
	private String accent;
	private String primary;
	private TextView[] tags = new TextView[10];
	private boolean isMapView;


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.marker_detail_fragment, container, true);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        setCancelable(true);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final Venue venue = getArguments().getParcelable(PARCEL_ID);
		isMapView = getArguments().getBoolean(IS_ON_MAP_VIEW);
        initClickListener(venue, view);

        initReviewText(view, venue);
        ((TextView)view.findViewById(R.id.dialog_type)).setText(VenueType.getTranslatedType(venue.type));

		TextView header = view.findViewById(R.id.dialog_header);
		View closeContainer = view.findViewById(R.id.close_details_dialog);
		header.setText(venue.name);
		header.setSelected(true);
		closeContainer.setOnClickListener(this);
        initTagValues(view, venue);


        initDiscountText(view, venue);

		ImageView img = view.findViewById(R.id.img);
		img.setOnClickListener(this);
		String imgUri = venue.IMG_FOLDER + venue.placesId + ".webp";
		Log.d(TAG,imgUri);

		RequestOptions options = new RequestOptions();
		//options.centerCrop(); This freezes, makes the webp not load
		options.diskCacheStrategy(DiskCacheStrategy.ALL);

		Glide.with(this).load(imgUri).apply(options).into(img).getView().setBackgroundResource(R.drawable.placeholder);

		return view;
    }



	@Override
	public void onClick(View v) {
		dismiss();
	}

    private void initReviewText(View view, Venue venue) {
		StringBuilder builder = new StringBuilder();
		if (venue.reviews == 0) {
			builder.append(getString(R.string.reviewsNotAvailable));
		} else {
			builder.append(getString(R.string.reviews));
			builder.append(" ");
			builder.append(venue.stars);
			builder.append(" (");
			builder.append(venue.reviews);
			builder.append(")");
		}

        ((TextView)view.findViewById(R.id.dialog_reviews)).setText(builder.toString());
    }

    private void initDiscountText(View view, Venue venue) {
        int discountText = venue.getDiscountText();
        if (discountText != -1)
            ((TextView)view.findViewById(R.id.dialog_discount)).setText(discountText);
        else {
            ((TextView)view.findViewById(R.id.dialog_discount)).setVisibility(View.INVISIBLE);
        }
    }

    private void initTagValues(View view, Venue venue) {
        String[] attribs = venue.getAttributes();
        Log.d("TRBC","attribs:" + attribs);
        if (attribs != null) {
		initTagViews(view);
		int i = 0;
		Log.d("TRBC","itTagVie:");
		for (String a : attribs
			) {
			if (a == null || a.trim().length() == 0)
			break;

			Log.d("TRBC","aaaaaaaaaaaa:" + a);
			String[] array = getResources().getStringArray(R.array.location_attributes);
			tags[i++].setText(array[Integer.valueOf(a)]);
			}
		}
    }

    private void initTagViews(View view) {
		tags[0] = (TextView)view.findViewById(R.id.tag0);
		tags[1] = (TextView)view.findViewById(R.id.tag1);
		tags[2] = (TextView)view.findViewById(R.id.tag2);
		tags[3] = (TextView)view.findViewById(R.id.tag3);
	}

	private void initClickListener(final Venue venue, View dialog) {
		final View btn_route = dialog.findViewById(R.id.dialog_button_route);

		clickedRouteButton(venue, btn_route);
		if (isMapView) {
			initShareButton(dialog);
		} else {
			initMapButton(dialog);
		}

		final View btn_favo = dialog.findViewById(R.id.dialog_button_favo);

		Context ctx = getContext();
		if (venue.isFavorite(ctx)) {
			switchColor(btn_favo, true, null);
			isFavo = true;
		}

		clickedFavoButton(venue, btn_favo, ctx);
	}

	private void clickedRouteButton(Venue venue, View btn_route) {
		btn_route.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getContext(), getString(R.string.toast_route_button), Toast.LENGTH_SHORT).show();
				switchColor(btn_route, true, null);
				openMapsRoute(venue);
				resetColorWithDelay(btn_route);
			}
		});
	}

	private void initShareButton(View dialog) {
		final View btn_share = dialog.findViewById(R.id.dialog_button_share);
		btn_share.setVisibility(View.VISIBLE);
		btn_share.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {Toast.makeText(getContext(), getString(R.string.toast_sharing_venue), Toast.LENGTH_SHORT).show();
				switchColor(btn_share, true, null);
				shareDeepLink();
				resetColorWithDelay(btn_share);
			}
		});
	}

	private void initMapButton(View dialog) {
		final View btn_map = dialog.findViewById(R.id.dialog_button_map);
		btn_map.setVisibility(View.VISIBLE);
		btn_map.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				MarkerDetailsFragment.this.cb.switchTabZoomCamera();
			}
		});
	}

	private void clickedFavoButton(Venue venue, View btn_favo, Context ctx) {
		btn_favo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isFavo) {
					//Toast.makeText(ctx, getString(R.string.toast_removed_favorite) + " " + venue.name, Toast.LENGTH_SHORT).show();
					VenueFacade.getInstance().removeFavoriteVenue(venue, getContext());
					isFavo = false;
				} else {
					//Toast.makeText(ctx,getString(R.string.toast_added_favorite) + " " +  venue.name,Toast.LENGTH_SHORT).show();
					VenueFacade.getInstance().addFavoriteVenue(venue, getContext());
					isFavo = true;
				}

				//FavoriteButtonAnimator.updateFavoriteSymbol(getContext(), btn_favo, venue, true);

				switchColor(btn_favo, isFavo, new AnimatorEndAbstract() {
					@Override
					public void onAnimationEnd(Animator animation) {

						venue.setFavorite(isFavo, ctx);
						cb.initAllListViews();
					}
				});
			}
		});
	}


	private void animateColorChange(View view, String fromColor, String toColor, Animator.AnimatorListener afterAnim) {
		final float[] from = new float[3];
		final float[] to = new float[3];

		Log.d("TRBC", "fromColor:" + fromColor);
		Log.d("TRBC", "toColor:" + toColor);

		Color.colorToHSV(Color.parseColor(fromColor), from);
		Color.colorToHSV(Color.parseColor(toColor), to);

		ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
		valueAnimator.setDuration(300);

		final float[] hsv  = new float[3];
		valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
			@Override public void onAnimationUpdate(ValueAnimator animation) {
				// Transition along each axis of HSV (hue, saturation, value)
				hsv[0] = from[0] + (to[0] - from[0])*animation.getAnimatedFraction();
				hsv[1] = from[1] + (to[1] - from[1])*animation.getAnimatedFraction();
				hsv[2] = from[2] + (to[2] - from[2])*animation.getAnimatedFraction();

				view.setBackgroundColor(Color.HSVToColor(hsv));
			}
		});

		if (afterAnim != null)
			valueAnimator.addListener(afterAnim);

		valueAnimator.start();
	}

	private void resetColorWithDelay(View btn_route) {
		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
            	//btn_route.clearAnimation();
                switchColor(btn_route, false, null);
            }
        },300L);
	}

	@Override
    public void onDestroy() {
        super.onDestroy();
        cb = null;
    }

	private boolean isFavo = false;

	private void switchColor(View btn, boolean onOff, Animator.AnimatorListener afterAnim) {
		if (accent == null) {
			accentColor = getResources().getColor(R.color.colorAccent);
			primaryColor = getResources().getColor(R.color.colorPrimary);
			accent = "#" + Integer.toHexString(accentColor).substring(2);
			primary = "#" + Integer.toHexString(primaryColor).substring(2);

			Log.d("TRBC", "accent:" + accent);
			Log.d("TRBC", "primary:" + primary);

		}
	    if (onOff)
			animateColorChange(btn, primary, accent, afterAnim);
	    else
	    	animateColorChange(btn, accent, primary, afterAnim);
    }

	private void openMapsRoute(Venue v) {

		Intent i = new Intent(Intent.ACTION_VIEW,
				Uri.parse(Venue.BASE_URI + v.placesId));
		startActivity(i);

	}

	private void shareDeepLink() {
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("text/plain");
		share.putExtra(Intent.EXTRA_TEXT, getString(R.string.recommendation));
		startActivity(Intent.createChooser(share, getString(R.string.thank_you)));
	}

	public static MarkerDetailsFragment newInstance(Venue v, UpdateActivityCallback cb, boolean isOnMapView) {
        MarkerDetailsFragment.cb = cb;
        MarkerDetailsFragment myFragment = new MarkerDetailsFragment();
		Bundle args = new Bundle();
		args.putParcelable(PARCEL_ID, v);
		args.putBoolean(IS_ON_MAP_VIEW, isOnMapView);
		myFragment.setArguments(args);

		return myFragment;
	}
}