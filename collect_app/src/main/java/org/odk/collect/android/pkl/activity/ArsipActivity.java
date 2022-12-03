package org.odk.collect.android.pkl.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.pkl.material_design.views.ButtonRectangle;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;
import org.odk.collect.android.preferences.PreferencesActivity;
import org.odk.collect.android.provider.InstanceProviderAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sandy on 03/03/2018.
 */

public class ArsipActivity extends ListActivity implements View.OnLongClickListener{
    private String actionBarTitle;
    private MaterialButton mRestoreButton;
    private MaterialButton mToggleButton;
    private SimpleCursorAdapter mInstances;
    private ArrayList<Long> mSelected = new ArrayList<Long>();
    private Boolean mToggled = false;
    private Boolean mRestored = false;
    private ProgressDialog mProgressDialog;
    private static final int INSTANCE_RESTORED = 0;

    public Cursor getSentCursor() {
        // get all complete or failed submission instances
        String selection = "(" + InstanceProviderAPI.InstanceColumns.STATUS + "=? ) AND (" + InstanceProviderAPI.InstanceColumns.NIM + "=?";

        List<String> whereNim = new ArrayList<>();

        CapiPreference pref = CapiPreference.getInstance();

        whereNim.add(InstanceProviderAPI.STATUS_SUBMITTED);

        whereNim.add((String) pref.get(CapiKey.KEY_NIM));

//        if (StaticFinal.JABATAN_KORTIM_ID.equals(pref.get(CapiKey.KEY_ID_JABATAN))) {
//            List<AnggotaTim> anggotaTim = DBhandler.getInstance().getAllAnggota();
//            for (AnggotaTim a : anggotaTim) {
//                whereNim.add(a.getNimAng());
//                selection += " OR " + InstanceProviderAPI.InstanceColumns.NIM + "=?";
//            }
//        }
        selection += ")";

        String[] selectionArgs = new String[whereNim.size()];
        selectionArgs = whereNim.toArray(selectionArgs);
        String sortOrder = InstanceProviderAPI.InstanceColumns.DISPLAY_NAME + " ASC";
        Cursor c = managedQuery(InstanceProviderAPI.InstanceColumns.CONTENT_URI, null, selection,
                selectionArgs, sortOrder);
        return c;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore);

        // set up long click listener

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplication(), R.color.colorPrimary)));

        actionBarTitle = "Restore Hasil Wawancara";
        setTitle(actionBarTitle);

        mProgressDialog = new ProgressDialog(this);
        mRestoreButton = findViewById(R.id.upload_button);
        mRestoreButton.setText("Kembalikan");
        mRestoreButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mProgressDialog.setMessage("Mengembalikan status kuesioner...");
                    Collect.getInstance()
                            .getActivityLogger()
                            .logAction(this, "uploadButton",
                                    Integer.toString(mSelected.size()));

                    if (mSelected.size() > 0) {
                        // items selected
                        restoreSelectedFiles();
                        mToggled = false;
                        mSelected.clear();
                        ArsipActivity.this.getListView().clearChoices();
                        mRestoreButton.setEnabled(false);
                    } else {
                        // no items selected
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.noselect_error),
                                Toast.LENGTH_SHORT).show();

                }
            }
        });

        mToggleButton = findViewById(R.id.toggle_button);
        mToggleButton.setLongClickable(true);
        mToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // toggle selections of items to all or none
                ListView ls = getListView();
                mToggled = !mToggled;

                Collect.getInstance()
                        .getActivityLogger()
                        .logAction(this, "toggleButton",
                                Boolean.toString(mToggled));
                // remove all items from selected list
                mToggleButton.setText("PILIH SEMUA");
                mSelected.clear();
                for (int pos = 0; pos < ls.getCount(); pos++) {
                    ls.setItemChecked(pos, mToggled);
                    // add all items if mToggled sets to select all
                    if (mToggled) {
                        mToggleButton.setText("BATAL PILIH");
                        mSelected.add(ls.getItemIdAtPosition(pos));
                    }
                }
                mRestoreButton.setEnabled(!(mSelected.size() == 0));

            }
        });

        Cursor c = getSentCursor();
        c.moveToFirst();
        while(c.moveToNext()){
            Log.d("SANDY",c.getString(c.getColumnIndex(InstanceProviderAPI.InstanceColumns._ID)));
        }
        Log.d("SANDY", String.valueOf(c.getCount()));

        String[] data = new String[]{InstanceProviderAPI.InstanceColumns.DISPLAY_NAME,
                InstanceProviderAPI.InstanceColumns.DISPLAY_SUBTEXT};
        int[] view = new int[]{R.id.text1, R.id.text2};

        // render total instance view
        mInstances = new SimpleCursorAdapter(this,
                R.layout.two_item_multiple_choice, c, data, view);

        setListAdapter(mInstances);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        getListView().setItemsCanFocus(false);
        mRestoreButton.setEnabled(!(mSelected.size() == 0));

        // if current activity is being reinitialized due to changing
        // orientation restore all check
        // marks for ones selected
        if (mRestored) {
            ListView ls = getListView();
            for (long id : mSelected) {
                for (int pos = 0; pos < ls.getCount(); pos++) {
                    if (id == ls.getItemIdAtPosition(pos)) {
                        ls.setItemChecked(pos, true);
                        break;
                    }
                }

            }
            mRestored = false;
        }
    }

    protected void restoreSelectedFiles(){
        // send list of _IDs.
        long[] instanceIDs = new long[mSelected.size()];
        for (int i = 0; i < mSelected.size(); i++) {
            instanceIDs[i] = mSelected.get(i);
        }
        String selection = InstanceProviderAPI.InstanceColumns._ID + "=?";
        String[] selectionArgs = new String[(instanceIDs == null) ? 0 : instanceIDs.length];
        if (instanceIDs != null) {
            for (int i = 0; i < instanceIDs.length; i++) {
                if (i != instanceIDs.length - 1) {
                    selection += " or " + InstanceProviderAPI.InstanceColumns._ID + "=?";
                }
                selectionArgs[i] = String.valueOf(instanceIDs[i]);
            }
        }

        Cursor c = null;
        c = Collect.getInstance().getContentResolver()
                    .query(InstanceProviderAPI.InstanceColumns.CONTENT_URI, null, selection, selectionArgs, null);
        Uri toUpdate = null;
        if (c.getCount() > 0) {
            c.moveToPosition(-1);
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndex(InstanceProviderAPI.InstanceColumns._ID));
                toUpdate = Uri.withAppendedPath(InstanceProviderAPI.InstanceColumns.CONTENT_URI, id);
                ContentValues cv = new ContentValues();
                cv.put(InstanceProviderAPI.InstanceColumns.STATUS,
                        InstanceProviderAPI.STATUS_SUBMISSION_FAILED);



                Collect.getInstance().getContentResolver()
                        .update(toUpdate, cv, null, null);
            }
        }



        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mProgressDialog.dismiss();
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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // get row id from db
        Cursor c = (Cursor) getListAdapter().getItem(position);
        long k = c.getLong(c.getColumnIndex(InstanceProviderAPI.InstanceColumns._ID));

        Collect.getInstance().getActivityLogger()
                .logAction(this, "onListItemClick", Long.toString(k));

        // add/remove from selected list
        if (mSelected.contains(k))
            mSelected.remove(k);
        else
            mSelected.add(k);

        mRestoreButton.setEnabled(!(mSelected.size() == 0));

    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        long[] selectedArray = savedInstanceState
//                .getLongArray(BUNDLE_SELECTED_ITEMS_KEY);
//        for (int i = 0; i < selectedArray.length; i++)
//            mSelected.add(selectedArray[i]);
//        mToggled = savedInstanceState.getBoolean(BUNDLE_TOGGLED_KEY);
//        mRestored = true;
//        mUploadButton.setEnabled(selectedArray.length > 0);
//    }
    private void createPreferencesMenu() {
        Intent i = new Intent(this, PreferencesActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Collect.getInstance().getActivityLogger()
                .logAction(this, "onCreateOptionsMenu", "show");
        super.onCreateOptionsMenu(menu);


        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                // Toast.makeText(this, "home pressed", Toast.LENGTH_LONG).show();
                break;

        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
