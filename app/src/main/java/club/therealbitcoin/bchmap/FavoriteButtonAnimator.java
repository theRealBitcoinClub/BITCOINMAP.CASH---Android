package club.therealbitcoin.bchmap;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import club.therealbitcoin.bchmap.club.therealbitcoin.bchmap.model.Venue;
import club.therealbitcoin.bchmap.interfaces.AnimationEndAbstract;

public class FavoriteButtonAnimator {
    public static void updateFavoriteSymbol(Context ctx, View button, Venue venue, boolean animate) {
        if (venue.isFavorite(ctx)) {
            if (animate) {
                Animation scaleOut = AnimationUtils.loadAnimation(ctx, R.anim.animation_size_hero_to_zero);
                scaleOut.reset();

                scaleOut.setAnimationListener(new AnimationEndAbstract() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        button.setBackgroundResource(R.drawable.ic_action_favorite);
                        Animation scaleIn = AnimationUtils.loadAnimation(ctx, R.anim.animation_size_zero_to_hero);
                        scaleIn.reset();
                        button.startAnimation(scaleIn);
                    }
                });
                button.startAnimation(scaleOut);
            } else {
                button.setBackgroundResource(R.drawable.ic_action_favorite);
            }
        } else {
            if (animate) {
                Animation scaleOut = AnimationUtils.loadAnimation(ctx, R.anim.animation_size_hero_to_zero);
                scaleOut.reset();
                scaleOut.setAnimationListener(new AnimationEndAbstract() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        button.setBackgroundResource(R.drawable.ic_action_favorite_border);
                        Animation scaleIn = AnimationUtils.loadAnimation(ctx, R.anim.animation_size_zero_to_hero);
                        scaleIn.reset();
                        button.startAnimation(scaleIn);
                    }
                });
                button.startAnimation(scaleOut);
            } else {
                button.setBackgroundResource(R.drawable.ic_action_favorite_border);
            }
        }
    }
}
