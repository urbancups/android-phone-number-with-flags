package com.urbancups.countryflags;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

public class CustomSpinner extends Spinner {

    private boolean mToggleFlag = true;
    private OnItemSelectedListener listener;

    public CustomSpinner(Context context, AttributeSet attrs,
                         int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSpinner(Context context) {
        super(context);
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        if (listener != null)
            listener.onItemSelected(null, null, position, 0);
    }

    @Override
    public int getSelectedItemPosition() {
        // this toggle is required because this method will get called in other
        // places too, the most important being called for the
        // OnItemSelectedListener
        if (!mToggleFlag) {
            return 0; // get us to the first element
        }
        return super.getSelectedItemPosition();
    }

    @Override
    public boolean performClick() {
        // this method shows the list of elements from which to select one.
        // we have to make the getSelectedItemPosition to return 0 so you can
        // fool the Spinner and let it think that the selected item is the first
        // element
        mToggleFlag = false;
        boolean result = super.performClick();
        mToggleFlag = true;
        return result;
    }

    public void setOnItemSelectedEvenIfUnchangedListener(
            OnItemSelectedListener listener) {
        this.listener = listener;
    }

}