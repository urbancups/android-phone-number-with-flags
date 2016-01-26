package com.urbancups.countryflags;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by jonathanmoskovich on 1/26/15.
 */
public class LatoLightBoldEditText extends EditText {

    private Typeface typeface;
    private final Context context;

    public LatoLightBoldEditText(Context context) {
        super(context);

        this.context = context;

        init();
    }

    public LatoLightBoldEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        init();
    }

    private void init() {
        if (this.isInEditMode()) return;
        typeface = Typeface.createFromAsset(context.getAssets(), "LatoLight.ttf");
        setTypeface(typeface, Typeface.BOLD);
    }

}
