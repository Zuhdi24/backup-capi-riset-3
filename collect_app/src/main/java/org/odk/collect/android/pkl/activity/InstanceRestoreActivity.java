package org.odk.collect.android.pkl.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import androidx.annotation.Nullable;
import android.util.Log;

import org.odk.collect.android.R;
import org.odk.collect.android.activities.FormEntryActivity;

import java.util.HashMap;

/**
 * Created by sandy on 03/03/2018.
 */

public class InstanceRestoreActivity extends Activity {
    private final static int PROGRESS_DIALOG = 1;
    private ProgressDialog mProgressDialog;
    private AlertDialog mAlertDialog;

    private String mAlertMsg;
    private boolean mAlertShowing;

    // maintain a list of what we've sent, in case we're interrupted by auth requests
    private HashMap<String, String> mRestoredInstances;
    private String mUrl;
    private Long[] mInstancesToRestore;
    private String t = "INSTANCE_RESTORE";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        mAlertMsg = getString(R.string.please_wait);
        mAlertShowing = false;

        mRestoredInstances = new HashMap<String, String>();

        setTitle("Mengembalikan data");



        // and if we are resuming, use the TO_SEND list of not-yet-sent submissions
        // Otherwise, construct the list from the incoming intent value
        long[] selectedInstanceIDs = null;

            // get instances to upload...
            Intent intent = getIntent();
            selectedInstanceIDs = intent.getLongArrayExtra(FormEntryActivity.KEY_INSTANCES);


        mInstancesToRestore = new Long[(selectedInstanceIDs == null) ? 0 : selectedInstanceIDs.length];
        if (selectedInstanceIDs != null) {
            for (int i = 0; i < selectedInstanceIDs.length; ++i) {
                mInstancesToRestore[i] = selectedInstanceIDs[i];
            }
        }

        // at this point, we don't expect this to be empty...
        if (mInstancesToRestore.length == 0) {
            Log.e(t, "onCreate: No instances to restore");
            // drop through -- everything will process through OK
        } else {
            Log.i(t, "onCreate: Beginning restore of " + mInstancesToRestore.length + " instances!");
        }

        // get the task if we've changed orientations. If it's null it's a new upload.

        showDialog(PROGRESS_DIALOG);
    }
}
