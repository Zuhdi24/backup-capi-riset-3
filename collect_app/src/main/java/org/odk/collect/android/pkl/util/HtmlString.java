package org.odk.collect.android.pkl.util;

import android.text.Html;
import android.text.SpannableStringBuilder;

import com.google.common.base.CharMatcher;

/**
 * @author Rahadi
 */

public class HtmlString {

    public static SpannableStringBuilder stringToHTML(String inputString) {
        String newline = System.getProperty("line.separator");
        String asteriskSubs = "<asterisk>";
        String underscoreSubs = "<underscore>";
        String boldTagOpen = "<b>";
        String boldTagClose = "</b>";
        String italicTagOpen = "<i>";
        String italicTagClose = "</i>";
        String newLineTag = "<br/>";
        String enter = inputString.replace(newline, newLineTag);

        int countStars = CharMatcher.is('*').countIn(enter);

        enter = enter.replace("*", asteriskSubs);
        String bold = enter;

        for (int i = 0; i < countStars - (countStars % 2); i++) {
            if (i % 2 == 1) {
                String inspect = bold.substring(bold.lastIndexOf(boldTagOpen), bold.indexOf(asteriskSubs));
                if (inspect.contains(newLineTag)) {
                    bold = new StringBuilder(bold).replace(bold.lastIndexOf(boldTagOpen), bold.lastIndexOf(boldTagOpen) + boldTagOpen.length(), "*").toString();
                    continue;
                }
            }
            int idx = bold.indexOf(asteriskSubs);
            bold = new StringBuilder(bold).replace(idx, idx + asteriskSubs.length(),
                    i % 2 == 0 ? boldTagOpen : boldTagClose).toString();
        }

        int countScores = CharMatcher.is('_').countIn(bold);

        bold = bold.replace("_", underscoreSubs);
        String italic = bold;

        for (int i = 0; i < countScores - (countScores % 2); i++) {
            if (i % 2 == 1) {
                String inspect = italic.substring(italic.lastIndexOf(italicTagOpen), italic.indexOf(underscoreSubs));
                if (inspect.contains(newLineTag)) {
                    italic = new StringBuilder(italic).replace(italic.lastIndexOf(italicTagOpen), italic.lastIndexOf(italicTagOpen) + italicTagOpen.length(), "_").toString();
                    continue;
                }
            }
            int idx = italic.indexOf(underscoreSubs);
            italic = new StringBuilder(italic).replace(idx, idx + underscoreSubs.length(),
                    i % 2 == 0 ? italicTagOpen : italicTagClose).toString();
        }

        return new SpannableStringBuilder(Html.fromHtml(italic));
    }
}
