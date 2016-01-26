/*
 * Copyright (c) 2014-2015 Amberfog.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.urbancups.countryflags;

import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;

import com.google.i18n.phonenumbers.AsYouTypeFormatter;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.Locale;

public class CustomPhoneNumberFormattingTextWatcher implements TextWatcher {

    /**
     * Indicates the change was caused by ourselves.
     */
    private boolean mSelfChange = false;

    /**
     * Indicates the formatting has been stopped.
     */
    private boolean mStopFormatting;

    private AsYouTypeFormatter asYouTypeFormatter;

    private OnPhoneChangedListener mOnPhoneChangedListener;

    /**
     * The formatting is based on the current system locale and future locale changes
     * may not take effect on this instance.
     */
    public CustomPhoneNumberFormattingTextWatcher(OnPhoneChangedListener listener) {
        this(Locale.getDefault().getCountry());
        mOnPhoneChangedListener = listener;
    }

    /**
     * The formatting is based on the given <code>countryCode</code>.
     *
     * @param countryCode the ISO 3166-1 two-letter country code that indicates the country/region
     *                    where the phone number is being entered.
     * @hide
     */
    public CustomPhoneNumberFormattingTextWatcher(String countryCode) {
        if (countryCode == null) throw new IllegalArgumentException();
        asYouTypeFormatter = PhoneNumberUtil.getInstance().getAsYouTypeFormatter(countryCode);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        if (mSelfChange || mStopFormatting) {
            return;
        }
        // If the user manually deleted any non-dialable characters, stop formatting
        if (count > 0 && hasSeparator(s, start, count)) {
            stopFormatting();
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mSelfChange || mStopFormatting) {
            return;
        }
        // If the user inserted any non-dialable characters, stop formatting
        if (count > 0 && hasSeparator(s, start, count)) {
            stopFormatting();
        }
    }

    @Override
    public synchronized void afterTextChanged(Editable s) {

        if (mStopFormatting) {
            // Restart the formatting when all texts were clear.
            mStopFormatting = !(s.length() == 0);
            return;
        }
        if (mSelfChange) {
            // Ignore the change caused by s.replace().
            return;
        }
        String formatted = reformat(s, Selection.getSelectionEnd(s));
        if (formatted != null) {
            int rememberedPos = asYouTypeFormatter.getRememberedPosition();
            mSelfChange = true;

            //Log.d("afterTextChanged","s was " +s);
            //Log.d("afterTextChanged", "formatted was " + formatted);
            //s.replace(0, formatted.length(), formatted);
            //s.replace(0, s.length(), formatted, 0, formatted.length());
            //Log.d("afterTextChanged", "after replace s was " + s);
            /*if (s.charAt(0) != '+') {
                s.insert(0, "+");
            }*/
            // The text could be changed by other TextWatcher after we changed it. If we found the
            // text is not the one we were expecting, just give up calling setSelection().
            /*if (formatted.equals(s.toString())) {
                Selection.setSelection(s, rememberedPos);
            }*/
            mSelfChange = false;
        } /*else if (s.length() == 0) {
            mSelfChange = true;
            s.insert(0, "+");
            mSelfChange = false;
        }*/

        if (mOnPhoneChangedListener != null) {
            mOnPhoneChangedListener.onPhoneChanged(formatted);
        }
    }

    /**
     * Generate the formatted number by ignoring all non-dialable chars and stick the cursor to the
     * nearest dialable char to the left. For instance, if the number is  (650) 123-45678 and '4' is
     * removed then the cursor should be behind '3' instead of '-'.
     */
    private String reformat(CharSequence s, int cursor) {

        Log.d("CustomPhoneNumberFormattingTextWatcher", "reformat with " +s +" and position " +String.valueOf(cursor));

        // The index of char to the leftward of the cursor.
        int curIndex = cursor - 1;
        String formatted = null;
        asYouTypeFormatter.clear();
        char lastNonSeparator = 0;
        boolean hasCursor = false;
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (PhoneNumberUtils.isNonSeparator(c)) {
                if (lastNonSeparator != 0) {
                    formatted = getFormattedNumber(lastNonSeparator, hasCursor);
                    hasCursor = false;
                }
                lastNonSeparator = c;
            }
            if (i == curIndex) {
                hasCursor = true;
            }
        }
        if (lastNonSeparator != 0) {
            formatted = getFormattedNumber(lastNonSeparator, hasCursor);
        }

        if (formatted == null) {
            return "";
        }

        Log.d("CustomPhoneNumberFormattingTextWatcher", "reformat returning " +formatted);
        return formatted;
    }

    private String getFormattedNumber(char lastNonSeparator, boolean hasCursor) {
        return hasCursor ? asYouTypeFormatter.inputDigitAndRememberPosition(lastNonSeparator)
                : asYouTypeFormatter.inputDigit(lastNonSeparator);
    }

    private void stopFormatting() {

        Log.d("CustomPhoneNumberFormattingTextWatcher", "stopFormatting");

        mStopFormatting = true;
        asYouTypeFormatter.clear();
    }

    private boolean hasSeparator(final CharSequence s, final int start, final int count) {
        for (int i = start; i < start + count; i++) {
            char c = s.charAt(i);
            if (!PhoneNumberUtils.isNonSeparator(c)) {
                return true;
            }
        }
        return false;
    }
}