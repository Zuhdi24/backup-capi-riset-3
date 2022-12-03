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

package org.odk.collect.android.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.database.DBErrorModel;
import org.odk.collect.android.logic.HierarchyElement;
import org.odk.collect.android.views.HierarchyElementView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HierarchyListAdapter extends BaseAdapter {

    private Context mContext;
    private List<HierarchyElement> mItems = new ArrayList<HierarchyElement>();
    private ArrayList<DBErrorModel> errorlist;


    public void setErrorlist(ArrayList<DBErrorModel> a) {
        errorlist = a;
        for (DBErrorModel ab : errorlist) {
            Log.d("jumlaherroradp", ab.getNullXPath());
        }
    }

    public ArrayList<DBErrorModel> getErrorlist() {
        return errorlist;
    }


    public HierarchyListAdapter(Context context) {
        mContext = context;
    }


    @Override
    public int getCount() {
        return mItems.size();
    }


    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HierarchyElementView hev;


        if (isErrorListAda(mItems.get(position).getNullXpath())) {
            mItems.get(position).setColor(Color.parseColor("#ff6961"));

        } else {
            if (mItems.get(position).getPrimaryText().contains("BLOK")) {
                Log.e("SANDY", mItems.get(position).getPrimaryText());
                mItems.get(position).setColor(Color.rgb(255,152,0));
            } else {
                mItems.get(position).setColor(Color.WHITE);
            }
        }
        if (convertView == null) {
            hev = new HierarchyElementView(mContext, mItems.get(position));
        } else {
            hev = (HierarchyElementView) convertView;
            hev.setPrimaryText(mItems.get(position).getPrimaryText());
            hev.setSecondaryText(mItems.get(position).getSecondaryText());
            hev.setIcon(mItems.get(position).getIcon());
            hev.setColor(mItems.get(position).getColor());
        }

        if (mItems.get(position).getSecondaryText() == null
                || mItems.get(position).getSecondaryText().equals("")) {
            hev.showSecondary(false);
        } else {
            hev.showSecondary(true);
        }
        return hev;

    }

    public boolean isErrorListAda(String a) {
        boolean y = false;
        for (int x = 0; x < errorlist.size(); x++) {
            if (errorlist.get(x).getNullXPath().equals(a)) {
                y = true;
                break;
            }
        }
        return y;
    }


    /**
     * Sets the list of items for this adapter to use.
     */
    public void setListItems(List<HierarchyElement> it) {
        mItems = it;
    }

}
