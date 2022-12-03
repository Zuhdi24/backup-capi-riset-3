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

package org.odk.collect.android.activities;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;

/**
 * An example of tab content that launches an activity via
 * {@link TabHost.TabSpec#setContent(Intent)}
 * edit Mahendri Dwicahyo
 */
public class FileManagerTabs extends TabActivity {

    private TextView mTVFF;
    private TextView mTVDF;

    private static final String FORMS_TAB = "forms_tab";
    private static final String DATA_TAB = "data_tab";

    private String actionBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBarTitle = getString(R.string.manage_files);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplication(), R.color.colorPrimary)));
        setTitle(actionBarTitle);

        final TabHost tabHost = getTabHost();
//		tabHost.setBackgroundColor(Color.WHITE);
        tabHost.getTabWidget().setBackgroundColor(ContextCompat.getColor(getApplication(), R.color.white2));

        Intent remote = new Intent(this, DataManagerList.class);
        tabHost.addTab(tabHost.newTabSpec(DATA_TAB)
                .setIndicator(getString(R.string.data)).setContent(remote));

        Intent local = new Intent(this, FormManagerList.class);
        tabHost.addTab(tabHost.newTabSpec(FORMS_TAB)
                .setIndicator(getString(R.string.forms)).setContent(local));


        // hack to set font size
        LinearLayout ll = (LinearLayout) tabHost.getChildAt(0);
        TabWidget tw = (TabWidget) ll.getChildAt(0);

        int fontsize = Collect.getQuestionFontsize();

        ViewGroup rllf = (ViewGroup) tw.getChildAt(0);
        mTVFF = getTextViewChild(rllf);
        if (mTVFF != null) {
//			mTVFF.setTextColor(ContextCompat.getColor(getApplication(), R.color.colorPrimary));
            mTVFF.setPadding(0, 0, 0, 6);
            mTVFF.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mTVFF.setAllCaps(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mTVFF.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }

        ViewGroup rlrf = (ViewGroup) tw.getChildAt(1);
        mTVDF = getTextViewChild(rlrf);
        if (mTVDF != null) {
//			mTVDF.setTextColor(ContextCompat.getColor(getApplication(), R.color.colorPrimary));
            mTVDF.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mTVDF.setPadding(0, 0, 0, 6);
            mTVDF.setAllCaps(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mTVDF.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }
    }

    private TextView getTextViewChild(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof TextView) {
                return (TextView) view;
            }
        }
        return null;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                onBackPressed();

                // Toast.makeText(this, "home pressed", Toast.LENGTH_LONG).show();
                break;

        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Collect.getInstance().getActivityLogger().logOnStart(this);
    }

    @Override
    protected void onStop() {
        Collect.getInstance().getActivityLogger().logOnStop(this);
        super.onStop();
    }

}
