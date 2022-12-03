package org.odk.collect.android.pkl.util;

import android.content.Context;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

/**
 * @author Mahendri
 */

public class TextColor {

    public static String change(Context context, String text, @ColorRes int color) {
        return "<font color=" + ContextCompat.getColor(context, color) + ">"
                + text + "</font>";
    }
}
