package com.urbancups.countryflags;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by jonathanmoskovich on 1/26/15.
 */
public class LatoRegularEditText extends EditText {

    private final Context context;

    public LatoRegularEditText(Context context) {
        super(context);

        this.context = context;

        init();
    }

    public LatoRegularEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        init();
    }

    private void init() {
        if (this.isInEditMode()) return;
        this.setTypeface(FontCache.get("LatoRegular.ttf", context));
    }

    public void setTextBold() {
        this.setTypeface(FontCache.get("LatoRegular.ttf", context));
    }
}
