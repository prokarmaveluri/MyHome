/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.prokarma.myhome.R;

/**
 * Widget to provide a custom animated spinner using the sample logo
 */
public class SDKSpinner extends FrameLayout {
    public SDKSpinner(Context context) {
        super(context);
        init(context);
    }

    public SDKSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SDKSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SDKSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.sdk_spinner_animation, this, true);

        ImageView spinnerRing = (ImageView) view.findViewById(R.id.sdk_spinner_ring);
        Animation rotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.infinite_rotation);
        spinnerRing.startAnimation(rotateAnimation);
    }

}
