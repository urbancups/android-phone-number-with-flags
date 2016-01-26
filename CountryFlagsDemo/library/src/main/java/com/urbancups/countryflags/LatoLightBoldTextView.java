package com.urbancups.countryflags;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by jonathanmoskovich on 3/4/15.
 */
public class LatoLightBoldTextView extends TextView {

    public LatoLightBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LatoLightBoldTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        if (this.isInEditMode()) return;
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "LatoLight.ttf");
        setTypeface(typeface, Typeface.BOLD);
    }

}
