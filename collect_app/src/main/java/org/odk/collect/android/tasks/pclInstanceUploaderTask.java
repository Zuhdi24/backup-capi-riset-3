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

package org.odk.collect.android.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.apache.commons.io.FileUtils;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.listeners.InstanceUploaderListener;
import org.odk.collect.android.listeners.UploadPcl;
import org.odk.collect.android.preferences.PreferencesActivity;
import org.odk.collect.android.provider.InstanceProviderAPI;
import org.odk.collect.android.provider.InstanceProviderAPI.InstanceColumns;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * Background task for uploading completed forms.
 *
 * @author Carl Hartung (carlhartung@gmail.com)
 */
public class pclInstanceUploaderTask {

    private static final String t = "InstanceUploaderTask";
    // it can take up to 27 seconds to spin up Aggregate
    private static final int CONNECTION_TIMEOUT = 60000;
    private static final String fail = "Error: ";


    private InstanceUploaderListener mStateListener;
    private UploadPcl mUploadPcl;

    /**
     * Uploads to urlString the submission identified by id with filepath of instance
     */
    private boolean uploadOneSubmission(final String id, final String uudi, final String nim, final String kortim, final String statusisian,
                                        final String status, final String formid, final String instanceFilePath) {

        Log.d("Uploadpcl", "" + "masuk 2");
        File instanceFile = new File(instanceFilePath);
        ContentValues cv = new ContentValues();
// post header
        String xml2String = null;
        try {
            xml2String = FileUtils.readFileToString(instanceFile, "UTF-8");
        } catch (Exception ex) {
            Log.d("uploadpcl", "" + ex);

        }
        final String isi = xml2String;
        String urlpostnotif = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(PreferencesActivity.KEY_HOST,
                        Collect.host) + "/post";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                urlpostnotif, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Uploadpcl suc", "" + response.toString());
                onsuc(uudi);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Uploadpcl Err ", "" + error);
                onfai();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("xml", isi);
                params.put("fileName", formid + "-" + nim + "-" + id);
                params.put("uuid", uudi);
                params.put("nim", nim);
                params.put("kortim", kortim);
                params.put("statusisian", statusisian);
                params.put("status", InstanceProviderAPI.STATUS_SUBMITTED);
                params.put("formid", formid);
                return params;
            }

        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Collect.getInstance2().addToRequestQueue(strReq);


        return true;
    }

    // TODO: This method is like 350 lines long, down from 400.
    // still. ridiculous. make it smaller.

    public void Background(Long[] values, String kortim, String nim) {


        String selection = InstanceColumns._ID + "=?";
        String[] selectionArgs = new String[(values == null) ? 0 : values.length];
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                if (i != values.length - 1) {
                    selection += " or " + InstanceColumns._ID + "=?";
                }
                selectionArgs[i] = values[i].toString();
            }
        }

        Cursor c = null;
        try {
            c = Collect.getInstance().getContentResolver()
                    .query(InstanceColumns.CONTENT_URI, null, selection, selectionArgs, null);

            if (c.getCount() > 0) {
                c.moveToPosition(-1);
                while (c.moveToNext()) {

                    String instancepath = c.getString(c.getColumnIndex(InstanceColumns.INSTANCE_FILE_PATH));
                    String formid = c.getString(c.getColumnIndex(InstanceColumns.JR_FORM_ID));
                    String Uuid = c.getString(c.getColumnIndex(InstanceColumns.UUID));
                    String statusisian = c.getString(c.getColumnIndex(InstanceColumns.STATUS_ISIAN));
                    String status = c.getString(c.getColumnIndex(InstanceColumns.STATUS));
                    String id = c.getString(c.getColumnIndex(InstanceColumns._ID));
                    uploadOneSubmission(id, Uuid, nim, kortim, statusisian, status, formid, instancepath);
                }
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

    }

    private void onsuc(String uuid) {
        ContentValues cv = new ContentValues();
        cv.put(InstanceColumns.STATUS, InstanceProviderAPI.STATUS_SUBMITTED);
        String where = InstanceColumns.UUID + "=?";
        String[] whereArgs = {uuid};
        int updated =
                Collect.getInstance().getContentResolver()
                        .update(InstanceColumns.CONTENT_URI, cv, where, whereArgs);
        if (updated > 0) {
            Toast.makeText(mContext, "File Uploaded Completed", Toast.LENGTH_SHORT)
                    .show();
            mUploadPcl.onuploadclear();
        } else {
            Toast.makeText(mContext, "File Uploaded Completed but Database Don't Update", Toast.LENGTH_SHORT)
                    .show();
            mUploadPcl.onuploadclear();
        }
    }

    private void onfai() {
        Toast.makeText(mContext, "File Upload not Complete", Toast.LENGTH_SHORT)
                .show();
        mUploadPcl.onuploadclear();
    }

    public void setmUploadPcl(UploadPcl up) {
        mUploadPcl = up;
    }

    private Context mContext;

    public pclInstanceUploaderTask(Context context) {
        mContext = context;
    }


//    protected void onPost(Outcomes outcome) {
//        synchronized (this) {
//            if (mStateListener != null) {
//                if (outcome.mAuthRequestingServer != null) {
//                    mStateListener.authRequest(outcome.mAuthRequestingServer, outcome.mResults);
//                } else {
//                    mStateListener.uploadingComplete(outcome.mResults);
//
//                    StringBuilder selection = new StringBuilder();
//                    Set<String> keys = outcome.mResults.keySet();
//                    Iterator<String> it = keys.iterator();
//
//                    String[] selectionArgs = new String[keys.size()+1];
//                    int i = 0;
//                    selection.append("(");
//                    while (it.hasNext()) {
//                        String id = it.next();
//                        selection.append(InstanceColumns._ID + "=?");
//                        selectionArgs[i++] = id;
//                        if (i != keys.size()) {
//                            selection.append(" or ");
//                        }
//                    }
//                    selection.append(") and status=?");
//                    selectionArgs[i] = InstanceProviderAPI.STATUS_SUBMITTED;
//
//                    Cursor results = null;
//                    try {
//                        results = Collect
//                                .getInstance()
//                                .getContentResolver()
//                                .query(InstanceColumns.CONTENT_URI, null, selection.toString(),
//                                        selectionArgs, null);
//                        if (results.getCount() > 0) {
//                            Long[] toDelete = new Long[results.getCount()];
//                            results.moveToPosition(-1);
//
//                            int cnt = 0;
//                            while (results.moveToNext()) {
//                                toDelete[cnt] = results.getLong(results
//                                        .getColumnIndex(InstanceColumns._ID));
//                                cnt++;
//                            }
//
//                            boolean deleteFlag = PreferenceManager.getDefaultSharedPreferences(
//                                    Collect.getInstance().getApplicationContext()).getBoolean(
//                                    PreferencesActivity.KEY_DELETE_AFTER_SEND, false);
//                            if (deleteFlag) {
//                                DeleteInstancesTask dit = new DeleteInstancesTask();
//                                dit.setContentResolver(Collect.getInstance().getContentResolver());
//                                dit.execute(toDelete);
//                            }
//
//                        }
//                    } finally {
//                        if (results != null) {
//                            results.close();
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//
//
//    protected void onProgressUpdate(Integer... values) {
//        synchronized (this) {
//            if (mStateListener != null) {
//                // update progress and total
//                mStateListener.progressUpdate(values[0].intValue(), values[1].intValue());
//            }
//        }
//    }
//
//
//    public void setUploaderListener(InstanceUploaderListener sl) {
//        synchronized (this) {
//            mStateListener = sl;
//        }
//    }
//
//
//    public static void copyToBytes(InputStream input, OutputStream output,
//                                   int bufferSize) throws IOException {
//        byte[] buf = new byte[bufferSize];
//        int bytesRead = input.read(buf);
//        while (bytesRead != -1) {
//            output.write(buf, 0, bytesRead);
//            bytesRead = input.read(buf);
//        }
//        output.flush();
//    }
//    public void isCancelled(){
//        return true;
//    }

}
