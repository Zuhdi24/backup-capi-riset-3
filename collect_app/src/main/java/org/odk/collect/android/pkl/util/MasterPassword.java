package org.odk.collect.android.pkl.util;

import android.util.Log;

import java.security.MessageDigest;
import java.util.Calendar;

/**
 * Created by isfann on 2/15/2017.
 */

public class MasterPassword {
    private static String TAG = "PASSWORD";

    public static String getPassword(String nimKortim) {
        String hashedPassword = "semanggatbosq";
        String salt = "inf14";
        int year = Calendar.getInstance().get( Calendar.YEAR );
        int month = Calendar.getInstance().get( Calendar.MONTH );
        int date = Calendar.getInstance().get( Calendar.DATE );
        int hour = Calendar.getInstance().get( Calendar.HOUR_OF_DAY );
        String raw = nimKortim + String.valueOf( year ) + String.valueOf( month ) + String.valueOf( date ) + String.valueOf( hour ) + salt;
        Log.d( TAG, "getPassword: raw = " + raw );

        try {
            MessageDigest md = MessageDigest.getInstance( "MD5" );
            md.update( raw.getBytes() );
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append( String.format( "%02x", b & 0xff ) );
            }
            hashedPassword = sb.toString().substring( 0, 7 );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hashedPassword;
    }
}
