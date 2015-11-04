package com.lemonlab.sockjschatservice;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by lk on 15. 11. 4..
 */
public class AnimationEffect {
//	public static Animation getBounceAnimation(Context context, int from, int to, AnimationListener listener)
//    {
//    	Animation animation = new TranslateAnimation(0.0f, 0.0f, from, to);
//    	animation.setAnimationListener(listener);
//    	animation.setDuration( 1000L );
//    	animation.setInterpolator(AnimationUtils.loadInterpolator(context, android.R.anim.bounce_interpolator));
//        return animation;
//
//    }

    public static Animation getVerticalAnimation(int from, int to, Animation.AnimationListener listener)
    {
        Animation animation = new TranslateAnimation(0.0f, 0.0f, from, to);
        animation.setAnimationListener(listener);
        animation.setDuration( 400 );
        animation.setInterpolator( new AccelerateInterpolator() );
        return animation;
    }

    public static Animation slideVerticalAnimation(boolean isDown, Animation.AnimationListener listener)
    {
        Animation a = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, isDown ? -1.0f : 1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );
        if(listener != null)
            a.setAnimationListener(listener);
        a.setDuration( 400 );
        a.setInterpolator( new AccelerateInterpolator() );
        return a;
    }

    public static Animation inFromRightAnimation()
    {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        );
        inFromRight.setDuration( 400 );
        inFromRight.setInterpolator( new AccelerateInterpolator() );
        return inFromRight;
    }

    public static Animation outToLeftAnimation()
    {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        );
        outtoLeft.setDuration( 400 );
        outtoLeft.setInterpolator( new AccelerateInterpolator() );
        return outtoLeft;
    }

    public static Animation inFromLeftAnimation()
    {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        );
        inFromLeft.setDuration( 400 );
        inFromLeft.setInterpolator( new AccelerateInterpolator() );
        return inFromLeft;
    }

    public static Animation outToRightAnimation()
    {
        Animation outtoRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        );
        outtoRight.setDuration( 400 );
        outtoRight.setInterpolator( new AccelerateInterpolator() );
        return outtoRight;
    }

    public static Animation fadeOut()
    {
        AlphaAnimation fadeOut = new AlphaAnimation( 1.0f, 0.0f );
        fadeOut.setDuration( 1000 );
        fadeOut.setStartOffset( 0 );
        return fadeOut;
    }
}
