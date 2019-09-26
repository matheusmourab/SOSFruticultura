package com.google.codelabs.mdc.java.shrine.utils.listeners;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import com.google.codelabs.mdc.java.shrine.R;

public class NavigationIconClickListener implements View.OnClickListener {

    private final AnimatorSet animatorSet = new AnimatorSet();
    private Context context;
    private View sheet;
    private View lastClickedView;
    private Interpolator interpolator;
    //private int height;
    private boolean backdropShown = false;
    private Drawable openIcon;
    private Drawable closeIcon;

    public NavigationIconClickListener(
            Context context, View sheet, @Nullable Interpolator interpolator,
            @Nullable Drawable openIcon, @Nullable Drawable closeIcon) {
        this.context = context;
        this.sheet = sheet;
        this.interpolator = interpolator;
        this.openIcon = openIcon;
        this.closeIcon = closeIcon;

        //DisplayMetrics displayMetrics = new DisplayMetrics();
        //((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //height = displayMetrics.heightPixels;
    }

    @Override
    public void onClick(View view) {
        backdropShown = !backdropShown;

        this.lastClickedView = view;

        // Cancel the existing animations
        animatorSet.removeAllListeners();
        animatorSet.end();
        animatorSet.cancel();

        updateIcon(view);

        final int translateY = context.getResources().getDimensionPixelSize(R.dimen.shr_product_grid_reveal_height);

        ObjectAnimator animator = ObjectAnimator.ofFloat(sheet, "translationY", backdropShown ? translateY : 0);
        animator.setDuration(500);
        if (interpolator != null) {
            animator.setInterpolator(interpolator);
        }
        animatorSet.play(animator);
        animator.start();
    }

    public void closeBackdrop() {
        if (backdropShown)
            this.onClick(lastClickedView);
    }

    private void updateIcon(View view) {
        if (openIcon != null && closeIcon != null) {
            if (!(view instanceof ImageView)) {
                throw new IllegalArgumentException("updateIcon() must be called on an ImageView");
            }
            if (backdropShown) {
                ((ImageView) view).setImageDrawable(closeIcon);
            } else {
                ((ImageView) view).setImageDrawable(openIcon);
            }
        }
    }
}
