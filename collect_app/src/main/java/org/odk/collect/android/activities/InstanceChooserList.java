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

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.pkl.activity.PklMainActivity;
import org.odk.collect.android.pkl.database.DBhandler;
import org.odk.collect.android.pkl.object.AnggotaTim;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;
import org.odk.collect.android.pkl.preference.StaticFinal;
import org.odk.collect.android.provider.InstanceProviderAPI;
import org.odk.collect.android.provider.InstanceProviderAPI.InstanceColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for displaying all the valid instances in the instance directory.
 *
 * @author Yaw Anokwa (yanokwa@gmail.com)
 * @author Carl Hartung (carlhartung@gmail.com)
 * edit Mahendri Dwicahyo
 */
public class InstanceChooserList extends ListActivity {

    private static final boolean EXIT = true;
    private static final boolean DO_NOT_EXIT = false;
    private AlertDialog mAlertDialog;

    private String actionBarTitle;
    private int setBack = 0;

    @Override
    public void onBackPressed() {
        if(setBack == 1){
            Intent BackpressedIntent = new Intent();
            BackpressedIntent .setClass(getApplicationContext(), PklMainActivity.class);
            BackpressedIntent .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(BackpressedIntent );
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        setBack = i.getIntExtra("setBack", setBack);
        Log.d("setBack", String.valueOf(setBack));

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplication(), R.color.colorPrimary)));
        actionBarTitle = getString(R.string.review_data);
        setTitle(actionBarTitle);



        // must be at the beginning of any activity that can be called from an external intent
        try {
            Collect.createODKDirs();
        } catch (RuntimeException e) {
            createErrorDialog(e.getMessage(), EXIT);
            return;
        }

        setContentView(R.layout.chooser_list_layout);
        TextView tv = (TextView) findViewById(R.id.status_text);
        tv.setVisibility(View.GONE);

        CapiPreference pref = CapiPreference.getInstance();
        String selection = InstanceColumns.STATUS + " !=? AND (" + InstanceColumns.NIM + " =?";
        Log.d("RAHADI", "NIM at InstanceChooserList : " + pref.get(CapiKey.KEY_NIM));

        List<String> whereNim = new ArrayList<>();
        whereNim.add(InstanceProviderAPI.STATUS_SUBMITTED);
        whereNim.add((String) pref.get(CapiKey.KEY_NIM));

        if (StaticFinal.JABATAN_KORTIM_ID.equals(pref.get(CapiKey.KEY_ID_JABATAN))) {
            List<AnggotaTim> anggotaTim = DBhandler.getInstance().getAllAnggota();
            for (AnggotaTim a : anggotaTim) {
                whereNim.add(a.getNimAng());
                selection += " OR " + InstanceColumns.NIM + "=?";
            }
        }
        selection += ")";
        Log.d("RAHADI", "Selection:" + selection);

        String[] selectionArgs = new String[whereNim.size()];
        selectionArgs = whereNim.toArray(selectionArgs);
        String sortOrder = InstanceColumns.STATUS + " DESC, " + InstanceColumns.DISPLAY_NAME + " ASC";
        Cursor c = managedQuery(InstanceColumns.CONTENT_URI, null, selection, selectionArgs, sortOrder);

        String[] data = new String[]{
                InstanceColumns.DISPLAY_NAME, InstanceColumns.DISPLAY_SUBTEXT
        };
        int[] view = new int[]{
                R.id.text1, R.id.text2
        };

        // render total instance view
        SimpleCursorAdapter instances =
                new SimpleCursorAdapter(this, R.layout.two_item_instance, c, data, view);
        setListAdapter(instances);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }


    /**
     * Stores the path of selected instance in the parent class and finishes.
     */
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        Cursor c = (Cursor) getListAdapter().getItem(position);
        startManagingCursor(c);
        Uri instanceUri =
                ContentUris.withAppendedId(InstanceColumns.CONTENT_URI,
                        c.getLong(c.getColumnIndex(InstanceColumns._ID)));

        Collect.getInstance().getActivityLogger().logAction(this, "onListItemClick", instanceUri.toString());

        String action = getIntent().getAction();
        if (Intent.ACTION_PICK.equals(action)) {
            // caller is waiting on a picked form
            setResult(RESULT_OK, new Intent().setData(instanceUri));
        } else {
            // the form can be edited if it is incomplete or if, when it was
            // marked as complete, it was determined that it could be edited
            // later.
            String status = c.getString(c.getColumnIndex(InstanceColumns.STATUS));
            String strCanEditWhenComplete =
                    c.getString(c.getColumnIndex(InstanceColumns.CAN_EDIT_WHEN_COMPLETE));

            boolean canEdit = status.equals(InstanceProviderAPI.STATUS_INCOMPLETE)
                    || Boolean.parseBoolean(strCanEditWhenComplete);
            if (!canEdit) {
                createErrorDialog(getString(R.string.cannot_edit_completed_form),
                        DO_NOT_EXIT);
                return;
            }
            // caller wants to view/edit a form, so launch formentryactivity
            startActivity(new Intent(Intent.ACTION_EDIT, instanceUri));
        }
        if(setBack == 0){
            finish();
        }
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

    private void createErrorDialog(String errorMsg, final boolean shouldExit) {
        Collect.getInstance().getActivityLogger().logAction(this, "createErrorDialog", "show");

        mAlertDialog = new AlertDialog.Builder(this).create();
        mAlertDialog.setIcon(android.R.drawable.ic_dialog_info);
        mAlertDialog.setMessage(errorMsg);
        DialogInterface.OnClickListener errorListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Collect.getInstance().getActivityLogger().logAction(this, "createErrorDialog",
                                shouldExit ? "exitApplication" : "OK");
                        if (shouldExit) {
                            finish();
                        }
                        break;
                }
            }
        };
        mAlertDialog.setCancelable(false);
        mAlertDialog.setButton(getString(R.string.ok), errorListener);
        mAlertDialog.show();
    }


}
