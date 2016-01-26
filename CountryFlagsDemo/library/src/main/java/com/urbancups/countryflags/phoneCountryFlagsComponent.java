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

import android.content.Context;
import android.os.AsyncTask;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.TreeSet;

public class phoneCountryFlagsComponent extends LinearLayout {

    private static final TreeSet<String> CANADA_CODES = new TreeSet<>();
    private Context mContext;

    static {
        CANADA_CODES.add("204");
        CANADA_CODES.add("236");
        CANADA_CODES.add("249");
        CANADA_CODES.add("250");
        CANADA_CODES.add("289");
        CANADA_CODES.add("306");
        CANADA_CODES.add("343");
        CANADA_CODES.add("365");
        CANADA_CODES.add("387");
        CANADA_CODES.add("403");
        CANADA_CODES.add("416");
        CANADA_CODES.add("418");
        CANADA_CODES.add("431");
        CANADA_CODES.add("437");
        CANADA_CODES.add("438");
        CANADA_CODES.add("450");
        CANADA_CODES.add("506");
        CANADA_CODES.add("514");
        CANADA_CODES.add("519");
        CANADA_CODES.add("548");
        CANADA_CODES.add("579");
        CANADA_CODES.add("581");
        CANADA_CODES.add("587");
        CANADA_CODES.add("604");
        CANADA_CODES.add("613");
        CANADA_CODES.add("639");
        CANADA_CODES.add("647");
        CANADA_CODES.add("672");
        CANADA_CODES.add("705");
        CANADA_CODES.add("709");
        CANADA_CODES.add("742");
        CANADA_CODES.add("778");
        CANADA_CODES.add("780");
        CANADA_CODES.add("782");
        CANADA_CODES.add("807");
        CANADA_CODES.add("819");
        CANADA_CODES.add("825");
        CANADA_CODES.add("867");
        CANADA_CODES.add("873");
        CANADA_CODES.add("902");
        CANADA_CODES.add("905");
    }

    final private SparseArray<ArrayList<Country>> mCountriesMap = new SparseArray<>();
    final private PhoneNumberUtil mPhoneNumberUtil = PhoneNumberUtil.getInstance();
    private Spinner mSpinner;
    private EditText mPhoneEdit;
    private CountryAdapter mAdapter;
    private TextView mCountryCode;
    private CustomPhoneNumberFormattingTextWatcher mCustomPhoneNumberFormattingTextWatcher;
    private InputFilter mInputFilter;
    private View mRootView;

    final private AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Country c = (Country) mSpinner.getItemAtPosition(position);
            mCountryCode.setText(mContext.getString(com.urbancups.countryflags.R.string.countryCode, String.valueOf(c.getCountryCode())));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private final OnPhoneChangedListener mOnPhoneChangedListener = new OnPhoneChangedListener() {
        @Override
        public void onPhoneChanged(String phone) {

            Log.d("OnPhoneChangedListener", "onPhoneChanged with " + phone);
            mPhoneEdit.removeTextChangedListener(mCustomPhoneNumberFormattingTextWatcher);
            mPhoneEdit.setFilters(new InputFilter[]{});
            mPhoneEdit.getText().clear();
            mPhoneEdit.getText().append(phone);
            mPhoneEdit.setSelection(mPhoneEdit.getText().length());
            mPhoneEdit.setFilters(new InputFilter[]{mInputFilter});
            mPhoneEdit.addTextChangedListener(mCustomPhoneNumberFormattingTextWatcher);

            try {
                Phonenumber.PhoneNumber p = mPhoneNumberUtil.parse(phone, null);
                ArrayList<Country> list = mCountriesMap.get(p.getCountryCode());
                Country country = null;
                if (list != null) {
                    if (p.getCountryCode() == 1) {
                        String num = String.valueOf(p.getNationalNumber());
                        if (num.length() >= 3) {
                            String code = num.substring(0, 3);
                            if (CANADA_CODES.contains(code)) {
                                for (Country c : list) {
                                    // Canada has priority 1, US has priority 0
                                    if (c.getPriority() == 1) {
                                        country = c;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (country == null) {
                        for (Country c : list) {
                            if (c.getPriority() == 0) {
                                country = c;
                                break;
                            }
                        }
                    }
                }
                if (country != null) {
                    final int position = country.getNum();
                    mSpinner.post(new Runnable() {
                        @Override
                        public void run() {
                            mSpinner.setSelection(position);
                        }
                    });
                }
            } catch (NumberParseException ignore) {
            }

        }
    };

    public phoneCountryFlagsComponent(Context context) {
        super(context);
        initViewGroup(context);
    }

    public phoneCountryFlagsComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViewGroup(context);
    }

    public phoneCountryFlagsComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViewGroup(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        for(int i = 0 ; i < getChildCount() ; i++){
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        for(int i = 0 ; i < getChildCount() ; i++){
            getChildAt(i).layout(l, t, r, b);
        }

    }

    private void initViewGroup(Context context) {
        mContext = context;

        View.inflate(context, R.layout.phone_country_flags_component,this);

        mRootView = getRootView();

        initUI();
        initCodes();
    }

    private void initUI() {

        LinearLayout spinnerContainer = (LinearLayout) mRootView.findViewById(com.urbancups.countryflags.R.id.flagsFragment_flagsSpinnerContainer);
        mCustomPhoneNumberFormattingTextWatcher = new CustomPhoneNumberFormattingTextWatcher(mOnPhoneChangedListener);
        mSpinner = (Spinner) mRootView.findViewById(com.urbancups.countryflags.R.id.flagsFragment_flagsSpinner);
        mSpinner.setOnItemSelectedListener(mOnItemSelectedListener);

        mAdapter = new CountryAdapter(mContext);

        mSpinner.setAdapter(mAdapter);
        spinnerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpinner.performClick();
            }
        });

        mCountryCode = (TextView) mRootView.findViewById(com.urbancups.countryflags.R.id.flagsFragment_countryCode);
        mPhoneEdit = (EditText) mRootView.findViewById(com.urbancups.countryflags.R.id.phone);

        mPhoneEdit.addTextChangedListener(mCustomPhoneNumberFormattingTextWatcher);

        mInputFilter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);

                    // don't allow a leading zero
                    if (dstart == 0 && String.valueOf(c).equalsIgnoreCase("0")) {
                        return "";
                    }

                    if (dstart >= 0 && !Character.isDigit(c)) {
                        return "";
                    }

                }

                return null;
            }
        };

        mPhoneEdit.setFilters(new InputFilter[]{mInputFilter});

        mPhoneEdit.setImeOptions(EditorInfo.IME_ACTION_SEND);
        mPhoneEdit.setImeActionLabel(mContext.getString(com.urbancups.countryflags.R.string.action_send), EditorInfo.IME_ACTION_SEND);
        mPhoneEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (mPhoneEdit != null && mPhoneEdit.getText().length() > 0) {
                    updateNationalNumber(mPhoneEdit.getText().toString());
                }
                return actionId == EditorInfo.IME_ACTION_SEND;
            }
        });

    }

    private void initCodes() {
        new AsyncPhoneInitTask().execute();
    }

    class AsyncPhoneInitTask extends AsyncTask<Void, Void, ArrayList<Country>> {

        private int mSpinnerPosition = -1;

        public AsyncPhoneInitTask() {
        }

        @Override
        protected ArrayList<Country> doInBackground(Void... params) {
            final ArrayList<Country> data = new ArrayList<>(233);
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(mContext.getApplicationContext().getAssets().open("countries.dat"), "UTF-8"));

                // do reading, usually loop until end of file reading
                String line;
                int i = 0;
                while ((line = reader.readLine()) != null) {
                    //process line
                    Country c = new Country(mContext, line, i);
                    data.add(c);
                    ArrayList<Country> list = mCountriesMap.get(c.getCountryCode());
                    if (list == null) {
                        list = new ArrayList<>();
                        mCountriesMap.put(c.getCountryCode(), list);
                    }
                    list.add(c);
                    i++;
                }
            } catch (IOException e) {
                //log the exception
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        //log the exception
                    }
                }
            }

            String countryRegion = PhoneUtils.getCountryRegionFromPhone(mContext);
            int code = mPhoneNumberUtil.getCountryCodeForRegion(countryRegion);
            ArrayList<Country> list = mCountriesMap.get(code);
            if (list != null) {
                for (Country c : list) {
                    if (c.getPriority() == 0) {
                        mSpinnerPosition = c.getNum();
                        break;
                    }
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<Country> data) {
            mAdapter.addAll(data);
            if (mSpinnerPosition > 0) {
                mSpinner.setSelection(mSpinnerPosition);
            }
        }
    }

    /**
     * Updates the national number based on the param s
     * Takes all formatting out of param s and then reformats the number
     * using the AsYouTypeFormatter for libphonenumber and based upon
     * the region code
     *
     * @param numberToFormat The formatted value to be used to update the national number
     * @return String The new formatted national number
     */
    @SuppressWarnings("unused") public String updateNationalNumber(String numberToFormat){

        Log.d("BaseFlagFragment", "updateNationalNumber with " + numberToFormat);

        /*//Instantiate the as you type formatter with the current region (US or UK)
        AsYouTypeFormatter aytf = mPhoneNumberUtil.getAsYouTypeFormatter(PhoneUtils.getCountryRegionFromPhone(mContext));

        //RE input all of the digits into the formatter
        for(int i = 0; i < numberToFormat.length(); i++){
            numberToFormat = aytf.inputDigit(numberToFormat.charAt(i));
        }

        //Clear the formatter for the next round of input
        aytf.clear();*/

        Phonenumber.PhoneNumber fNationalNumber = new Phonenumber.PhoneNumber();

        //Try to update the phone number with the formatted number
        try {
            if (!TextUtils.isEmpty(numberToFormat)) {
                 fNationalNumber = mPhoneNumberUtil.parse(numberToFormat, PhoneUtils.getCountryRegionFromPhone(mContext));
            }
            //Rejects if the number isn't in an acceptable format for the region code given etc.
        } catch (NumberParseException e) {
            Log.d("BaseFlagFragment","updateNationalNumber showing toast");
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            return "";
        }

        fNationalNumber.setCountryCodeSource(Phonenumber.PhoneNumber.CountryCodeSource.FROM_NUMBER_WITH_PLUS_SIGN);
        fNationalNumber.setCountryCode(mPhoneNumberUtil.getCountryCodeForRegion(PhoneUtils.getCountryRegionFromPhone(mContext)));

        String numberToReturn = mPhoneNumberUtil.format(fNationalNumber, PhoneNumberUtil.PhoneNumberFormat.E164);

        Log.d("BaseFlagFragment", "Returning formatted number " + numberToReturn);

        //Return the formatted phone number
        return numberToReturn;
    }

}
