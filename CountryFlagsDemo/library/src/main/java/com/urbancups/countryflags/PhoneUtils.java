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
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneUtils {


    /**
     * Returns the countrycode (e.g. "US") for the user's phone
     *
     * @param mContext the app context
     * @return String representing the user's countrycode
     * @since 2016-01-24
     *
     * */
    static String getCountryRegionFromPhone(Context mContext) {

        // check if we have read_phone_state. If we do then we can use it to get the user's countrycode accurately
        if (mContext.checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") == PackageManager.PERMISSION_GRANTED) {

            String mCountryCode = null;

            // if we have read_phone_state then we can use the telephonyManager method to get the countrycode
            TelephonyManager mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

            if (mTelephonyManager != null) {
                String str = mTelephonyManager.getLine1Number();
                if (!TextUtils.isEmpty(str) && !str.matches("^0*$")) {
                    mCountryCode = parseNumber(str);
                }

                if (mCountryCode == null) {
                    mCountryCode = mTelephonyManager.getNetworkCountryIso();
                }

            }

            if (mCountryCode == null) {
                return mContext.getResources().getConfiguration().locale.getCountry().toUpperCase();
            } else {
                return mCountryCode.toUpperCase();
            }
        } else {
            return mContext.getResources().getConfiguration().locale.getCountry(); // default to local country if no permissions
        }

    }

    /**
     * Parse the user's phone number to get their countrycode
     * @param usersPhoneNumber
     * @return
     */
    private static String parseNumber(String usersPhoneNumber) {
        if (usersPhoneNumber == null) {
            return null;
        }
        PhoneNumberUtil numberUtil = PhoneNumberUtil.getInstance();
        String result;
        try {
            Phonenumber.PhoneNumber localPhoneNumber = numberUtil.parse(usersPhoneNumber, null);
            result = numberUtil.getRegionCodeForNumber(localPhoneNumber);
            if (result == null) {
                return null;
            }
        } catch (NumberParseException localNumberParseException) {
            return null;
        }
        return result;
    }


}