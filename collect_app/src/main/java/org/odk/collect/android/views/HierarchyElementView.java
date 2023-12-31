/*
 * Copyright (C) 2009 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import org.odk.collect.android.R;
import org.odk.collect.android.logic.HierarchyElement;
import org.odk.collect.android.utilities.TextUtils;
import org.odk.collect.android.widgets.QuestionWidget;

public class HierarchyElementView extends RelativeLayout {

    private TextView mPrimaryTextView;
    private TextView mSecondaryTextView;
    private ImageView mIcon;
    private Typeface QUICKSAND = ResourcesCompat.getFont(getContext(), R.font.quicksand);
    private Typeface QUICKSAND_BOLD = ResourcesCompat.getFont(getContext(), R.font.quicksand_bold);


    public HierarchyElementView(Context context, HierarchyElement it) {
        super(context);

        setColor(it.getColor());

        mIcon = new ImageView(context);
        mIcon.setImageDrawable(it.getIcon());
        mIcon.setId(QuestionWidget.newUniqueId());
        mIcon.setPadding(0, 0, dipToPx(4), 0);

        addView(mIcon, new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        mPrimaryTextView = new TextView(context);
        mPrimaryTextView.setTextAppearance(context, android.R.style.TextAppearance_Large);
        setPrimaryText(it.getPrimaryText());
        mPrimaryTextView.setId(QuestionWidget.newUniqueId());
        mPrimaryTextView.setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams l =
                new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        l.addRule(RelativeLayout.RIGHT_OF, mIcon.getId());
        addView(mPrimaryTextView, l);

        mSecondaryTextView = new TextView(context);
        mSecondaryTextView.setText(it.getSecondaryText());
        mSecondaryTextView.setTextAppearance(context, android.R.style.TextAppearance_Small);
        mSecondaryTextView.setGravity(Gravity.CENTER_VERTICAL);

        LayoutParams lp =
                new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.BELOW, mPrimaryTextView.getId());
        lp.addRule(RelativeLayout.RIGHT_OF, mIcon.getId());
        addView(mSecondaryTextView, lp);

        setPadding(dipToPx(8), dipToPx(4), dipToPx(8), dipToPx(8));

    }


    public void setPrimaryText(String text) {

        if(text.contains("BLOK")){
            Log.d("SANDY",text);
            mPrimaryTextView.setTypeface(QUICKSAND_BOLD);
//            mPrimaryTextView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }else{
            mPrimaryTextView.setBackgroundColor(Color.TRANSPARENT);
            mPrimaryTextView.setTypeface(QUICKSAND);
        }
        mPrimaryTextView.setText(TextUtils.textToHtml(text));
    }


    public void setSecondaryText(String text) {
        mSecondaryTextView.setText(text);
        mSecondaryTextView.setTypeface(QUICKSAND);
    }


    public void setIcon(Drawable icon) {
        mIcon.setImageDrawable(icon);
    }


    public void setColor(int color) {
        setBackgroundColor(color);
    }


    public void showSecondary(boolean bool) {
        if (bool) {
            mSecondaryTextView.setVisibility(VISIBLE);
            setMinimumHeight(dipToPx(64));

        } else {
            mSecondaryTextView.setVisibility(GONE);
            setMinimumHeight(dipToPx(32));

        }
    }

    public int dipToPx(int dip) {
        return (int) (dip * getResources().getDisplayMetrics().density + 0.5f);
    }

}
