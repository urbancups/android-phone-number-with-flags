package com.urbancups.countryflags;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by jonathanmoskovich on 3/4/15.
 */
public class LatoRegularTextView extends TextView {

    public LatoRegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LatoRegularTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        if (this.isInEditMode()) return;
        this.setTypeface(FontCache.get("LatoRegular.ttf", context));
    }

}
