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
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.javarosa.core.model.FormDef;
import org.javarosa.core.services.transport.payload.ByteArrayPayload;
import org.javarosa.form.api.FormEntryController;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.database.DBErrorModel;
import org.odk.collect.android.database.ItemsetDbAdapter;
import org.odk.collect.android.exception.EncryptionException;
import org.odk.collect.android.listeners.FormSavedListener;
import org.odk.collect.android.logic.FormController;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;
import org.odk.collect.android.provider.FormsProviderAPI.FormsColumns;
import org.odk.collect.android.provider.InstanceProviderAPI;
import org.odk.collect.android.provider.InstanceProviderAPI.InstanceColumns;
import org.odk.collect.android.utilities.EncryptionUtils;
import org.odk.collect.android.utilities.EncryptionUtils.EncryptedFormInformation;
import org.odk.collect.android.utilities.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * Background task for loading a form.
 *
 * @author Carl Hartung (carlhartung@gmail.com)
 * @author Yaw Anokwa (yanokwa@gmail.com)
 * edit Mahendri Dwicahyo
 */
public class SaveToDiskTask extends AsyncTask<Void, String, SaveResult> {
    private final static String t = "SaveToDiskTask";

    private FormSavedListener mSavedListener;
    private Boolean mSave;
    private Boolean mMarkCompleted;
    private Uri mUri;
    private String mInstanceName;
    private int ak;

    public static final int SAVED = 500;
    public static final int SAVE_ERROR = 501;
    public static final int VALIDATE_ERROR = 502;
    public static final int VALIDATED = 503;
    public static final int SAVED_AND_EXIT = 504;
    private ItemsetDbAdapter dber;
    private ArrayList<DBErrorModel> errorlist;
    private int jenis;

    public SaveToDiskTask(Uri uri, Boolean saveAndExit, Boolean markCompleted, String updatedName, ArrayList<DBErrorModel> er, int Ak) {
        Log.d(t, "updateinstance" + mInstanceName);
        dber = new ItemsetDbAdapter();
        mUri = uri;
        mSave = saveAndExit;
        mMarkCompleted = markCompleted;
        mInstanceName = updatedName;
        errorlist = er;
        ak = Ak;
        jenis = 0;
    }
//    public SaveToDiskTask(Uri uri, Boolean saveAndExit, String updatedName, ArrayList<DBErrorModel> er) {
//        dber= new ItemsetDbAdapter();
//        mUri = uri;
//        mSave = saveAndExit;
//        mMarkCompleted = true;
//        mInstanceName = updatedName;
//        errorlist=er;
//        ak=1;
//        jenis=0;
//    }

    public void setJenis() {
        jenis = 1;
    }


    /**
     * Initialize {@link FormEntryController} with {@link FormDef} from binary or from XML. If given
     * an instance, it will be used to fill the {@link FormDef}.
     */
    @Override
    protected SaveResult doInBackground(Void... nothing) {
        Log.d(t, "updateinstance" + mInstanceName);
        SaveResult saveResult = new SaveResult();

        FormController formController = Collect.getInstance().getFormController();
        Log.d("generate", "" + (formController == null));
        Log.d("generate", formController.getInstancePath().getAbsolutePath());
        Log.d("generate", formController.getFormTitle());

        publishProgress(Collect.getInstance().getString(R.string.survey_saving_validating_message));
        Log.d("jumlaherror", "" + errorlist.size());
        dber.open();
        dber.deleterowerrors(formController.getFormTitle(), formController.getInstancePath().getAbsolutePath());
        dber.addRowErrors(errorlist);
        dber.close();

        //formController.validates(mMarkCompleted);
        try {

            int validateStatus = formController.validatesdua(mMarkCompleted);
            if (validateStatus != FormEntryController.ANSWER_OK) {
                // validation failed, pass specific failure
                saveResult.setSaveResult(validateStatus);
                Log.d("rajasave", "" + validateStatus);
                return saveResult;
            }


        } catch (Exception e) {
            Log.e(t, e.getMessage(), e);

            // SCTO-825
            // that means that we have a bad design
            // save the exception to be used in the error dialog.
            saveResult.setSaveErrorMessage(e.getMessage());
            saveResult.setSaveResult(SAVE_ERROR);
            return saveResult;
        }
        //formController.setMetadataTambahan();


        // check if the "Cancel" was hit and exit.
        if (isCancelled()) {
            return null;
        }

        if (mMarkCompleted) {
            formController.postProcessInstance();
        }

        Collect.getInstance().getActivityLogger().logInstanceAction(this, "save", Boolean.toString(mMarkCompleted));

        // close all open databases of external data.
        Collect.getInstance().getExternalDataManager().close();

        // if there is a meta/instanceName field, be sure we are using the latest value
        // just in case the validate somehow triggered an update.
        String updatedSaveName = formController.getSubmissionMetadata().instanceName;
        if (updatedSaveName != null) {
            mInstanceName = updatedSaveName;
        }

        try {
            exportData(mMarkCompleted);

            // attempt to remove any scratch file
            File shadowInstance = savepointFile(formController.getInstancePath());
            if (shadowInstance.exists()) {
                FileUtils.deleteAndReport(shadowInstance);
            }

            saveResult.setSaveResult(mSave ? SAVED_AND_EXIT : SAVED);
        } catch (Exception e) {
            Log.e(t, e.getMessage(), e);

            saveResult.setSaveErrorMessage(e.getMessage());
            saveResult.setSaveResult(SAVE_ERROR);
        }
        if (ak == 1) {

        }

        return saveResult;
    }

    private void updateInstanceDatabase(boolean incomplete, boolean canEditAfterCompleted) {
        Log.d(t, "updateinstance" + mInstanceName);

        FormController formController = Collect.getInstance().getFormController();

        // Update the instance database...
        ContentValues values = new ContentValues();

        values.put(InstanceColumns.UUID, formController.getSubmissionMetadata().instanceId);
        if (mInstanceName != null) {
            values.put(InstanceColumns.DISPLAY_NAME, mInstanceName);
        }
//        if(ak==1){
//            values.put(InstanceColumns.STATUS, InstanceProviderAPI.STATUS_COMPLETE);
//        } else
        if (incomplete || !mMarkCompleted) {
            values.put(InstanceColumns.STATUS, InstanceProviderAPI.STATUS_INCOMPLETE);
        } else {
            values.put(InstanceColumns.STATUS, InstanceProviderAPI.STATUS_COMPLETE);
        }
        if (ak == 0) {
            Log.d("savekortim3", "masuk");
            if (errorlist.size() == 0) {
                values.put(InstanceColumns.STATUS_ISIAN, InstanceProviderAPI.STATUS_CLEAR);
            } else {
                values.put(InstanceColumns.STATUS_ISIAN, InstanceProviderAPI.STATUS_UNCLEAR);
            }
        } else {
            Cursor resultss = null;
            String[] projs = {InstanceColumns.STATUS_ISIAN};
            String wheres = InstanceColumns.INSTANCE_FILE_PATH + " = ? ";
            String[] whereargss = {formController.getInstancePath().getAbsolutePath()};
            String sttt = null;
            try {
                resultss = Collect.getInstance().getContentResolver().query(InstanceColumns.CONTENT_URI,
                        projs, wheres, whereargss, null);
                resultss.moveToPosition(-1);
                while (resultss.moveToNext()) {
                    sttt = resultss.getString(resultss.getColumnIndex(InstanceColumns.STATUS_ISIAN));
                }
            } catch (Exception ex) {
                Log.d(t, "name " + ex);
            } finally {
                if (resultss != null) {
                    resultss.close();
                }
            }
            resultss.close();
            values.put(InstanceColumns.STATUS_ISIAN, sttt);

        }
        // update this whether or not the status is complete...
        values.put(InstanceColumns.CAN_EDIT_WHEN_COMPLETE, Boolean.toString(canEditAfterCompleted));

        CapiPreference preference = CapiPreference.getInstance();

        // If FormEntryActivity was started with an Instance, just update that instance
        if (Collect.getInstance().getContentResolver().getType(mUri).equals(InstanceColumns.CONTENT_ITEM_TYPE)) {
            int updated = Collect.getInstance().getContentResolver().update(mUri, values, null, null);
            if (updated > 1) {
                Log.w(t, "Updated more than one entry, that's not good: " + mUri.toString());
            } else if (updated == 1) {
                Log.i(t, "Instance successfully updated");
            } else {
                Log.e(t, "Instance doesn't exist but we have its Uri!! " + mUri.toString());
            }
        } else if (Collect.getInstance().getContentResolver().getType(mUri).equals(FormsColumns.CONTENT_ITEM_TYPE)) {
            // If FormEntryActivity was started with a form, then it's likely the first time we're
            // saving.
            // However, it could be a not-first time saving if the user has been using the manual
            // 'save data' option from the menu. So try to update first, then make a new one if that
            // fails.
            String instancePath = formController.getInstancePath().getAbsolutePath();
            String where = InstanceColumns.INSTANCE_FILE_PATH + "=?";
            String[] whereArgs = {
                    instancePath
            };
            int updated =
                    Collect.getInstance().getContentResolver()
                            .update(InstanceColumns.CONTENT_URI, values, where, whereArgs);
            if (updated > 1) {
                Log.w(t, "Updated more than one entry, that's not good: " + instancePath);
            } else if (updated == 1) {
                Log.i(t, "Instance found and successfully updated: " + instancePath);
                // already existed and updated just fine
            } else {
                Log.i(t, "No instance found, creating");
                // Entry didn't exist, so create it.
                Cursor c = null;
                try {
                    // retrieve the form definition...
                    c = Collect.getInstance().getContentResolver().query(mUri, null, null, null, null);
                    c.moveToFirst();
                    String jrformid = c.getString(c.getColumnIndex(FormsColumns.JR_FORM_ID));
                    String jrversion = c.getString(c.getColumnIndex(FormsColumns.JR_VERSION));
                    String formname = c.getString(c.getColumnIndex(FormsColumns.DISPLAY_NAME));
                    String submissionUri = null;
                    if (!c.isNull(c.getColumnIndex(FormsColumns.SUBMISSION_URI))) {
                        submissionUri = c.getString(c.getColumnIndex(FormsColumns.SUBMISSION_URI));
                    }

                    // add missing fields into values
                    values.put(InstanceColumns.INSTANCE_FILE_PATH, instancePath);
                    values.put(InstanceColumns.SUBMISSION_URI, submissionUri);
                    if (mInstanceName != null) {
                        values.put(InstanceColumns.DISPLAY_NAME, mInstanceName);
                    } else {
                        values.put(InstanceColumns.DISPLAY_NAME, formname);
                    }
                    values.put(InstanceColumns.JR_FORM_ID, jrformid);
                    values.put(InstanceColumns.JR_VERSION, jrversion);

                    //Set NIM for Privileges
                    values.put(InstanceColumns.NIM, (String) preference.get(CapiKey.KEY_NIM));
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
                mUri = Collect.getInstance().getContentResolver()
                        .insert(InstanceColumns.CONTENT_URI, values);

            }
        }

        if (mInstanceName == null && ak == 0) {
            try {
                ContentValues defaultName = new ContentValues();
                String namaa = null;
                if (namaa == null) {
                    namaa = formController.getNama();
                }
                String wheres = InstanceColumns.INSTANCE_FILE_PATH + " = ? AND " + InstanceColumns.CAN_EDIT_FILENAMA + " IS NULL";
                String[] whereargss = {formController.getInstancePath().getAbsolutePath()};
                if (namaa == null) {
                    String idd = null;
                    Cursor resultss = null;
                    String[] projs = {InstanceColumns._ID};

                    try {
                        resultss = Collect.getInstance().getContentResolver().query(InstanceColumns.CONTENT_URI,
                                projs, wheres, whereargss, null);
                        resultss.moveToPosition(-1);
                        while (resultss.moveToNext()) {
                            idd = resultss.getString(resultss.getColumnIndex(InstanceColumns._ID));
                        }
                    } catch (Exception ex) {
                        Log.d(t, "name " + ex);
                    }
                    resultss.close();
                    String nim = (String) preference.get(CapiKey.KEY_NIM);

                    namaa = formController.getFormTitle() + "_" + nim + "_" + idd;
                }
                defaultName.put(InstanceColumns.DISPLAY_NAME, namaa);
                int upd =
                        Collect.getInstance().getContentResolver()
                                .update(InstanceColumns.CONTENT_URI, defaultName, wheres, whereargss);
            } catch (Exception ex) {
                Log.d(t, "" + ex);
            }
        }
    }

    /**
     * Return the name of the savepoint file for a given instance.
     *
     * @param instancePath
     * @return
     */
    public static File savepointFile(File instancePath) {
        Log.d(t, "savepointfile");
        File tempDir = new File(Collect.CACHE_PATH);
        return new File(tempDir, instancePath.getName() + ".save");
    }

    /**
     * Write's the data to the sdcard, and updates the instances content provider.
     * In theory we don't have to write to disk, and this is where you'd add
     * other methods.
     *
     * @param markCompleted
     * @return
     */
    private void exportData(boolean markCompleted) throws IOException, EncryptionException {
        Log.d(t, "exportdata");
        FormController formController = Collect.getInstance().getFormController();

        publishProgress(Collect.getInstance().getString(R.string.survey_saving_collecting_message));

        ByteArrayPayload payload = formController.getFilledInFormXml();
        // write out xml
        try {
            Log.d("payload", payload.getPayloadStream().toString());
        } catch (Exception ex) {
            Log.d("payload", "gagalPayload");
        }
        String instancePath = formController.getInstancePath().getAbsolutePath();

        publishProgress(Collect.getInstance().getString(R.string.survey_saving_saving_message));

        Log.d("reproduce instance", instancePath);
        exportXmlFile(payload, instancePath);


        // update the mUri. We have exported the reloadable instance, so update status...
        // Since we saved a reloadable instance, it is flagged as re-openable so that if any error
        // occurs during the packaging of the data for the server fails (e.g., encryption),
        // we can still reopen the filled-out form and re-save it at a later time.
        updateInstanceDatabase(true, true);

        if (markCompleted) {

            // now see if the packaging of the data for the server would make it
            // non-reopenable (e.g., encryption or send an SMS or other fraction of the form).
            boolean canEditAfterCompleted = formController.isSubmissionEntireForm();
            boolean isEncrypted = false;

            // build a submission.xml to hold the data being submitted
            // and (if appropriate) encrypt the files on the side

            // pay attention to the ref attribute of the submission profile...
            File instanceXml = formController.getInstancePath();
            File submissionXml = new File(instanceXml.getParentFile(), "submission.xml");


            payload = formController.getSubmissionXml();

            // write out submission.xml -- the data to actually submit to aggregate

            publishProgress(Collect.getInstance().getString(R.string.survey_saving_finalizing_message));

            exportXmlFile(payload, submissionXml.getAbsolutePath());

            // see if the form is encrypted and we can encrypt it...
            EncryptedFormInformation formInfo = EncryptionUtils.getEncryptedFormInformation(mUri,
                    formController.getSubmissionMetadata());
            if (formInfo != null) {
                // if we are encrypting, the form cannot be reopened afterward
                canEditAfterCompleted = false;
                // and encrypt the submission (this is a one-way operation)...

                publishProgress(Collect.getInstance().getString(R.string.survey_saving_encrypting_message));

                EncryptionUtils.generateEncryptedSubmission(instanceXml, submissionXml, formInfo);
                isEncrypted = true;
            }

            // At this point, we have:
            // 1. the saved original instanceXml,
            // 2. all the plaintext attachments
            // 2. the submission.xml that is the completed xml (whether encrypting or not)
            // 3. all the encrypted attachments if encrypting (isEncrypted = true).
            //
            // NEXT:
            // 1. Update the instance database (with status complete).
            // 2. Overwrite the instanceXml with the submission.xml
            //    and remove the plaintext attachments if encrypting

            updateInstanceDatabase(false, canEditAfterCompleted);

            if (!canEditAfterCompleted) {
                // AT THIS POINT, there is no going back.  We are committed
                // to returning "success" (true) whether or not we can
                // rename "submission.xml" to instanceXml and whether or
                // not we can delete the plaintext media files.
                //
                // Handle the fall-out for a failed "submission.xml" rename
                // in the InstanceUploader task.  Leftover plaintext media
                // files are handled during form deletion.

                // delete the restore Xml file.
                if (!instanceXml.delete()) {
                    String msg = "Error deleting " + instanceXml.getAbsolutePath()
                            + " prior to renaming submission.xml";
                    Log.e(t, msg);
                    throw new IOException(msg);
                }

                // rename the submission.xml to be the instanceXml
                if (!submissionXml.renameTo(instanceXml)) {
                    String msg = "Error renaming submission.xml to " + instanceXml.getAbsolutePath();
                    Log.e(t, msg);
                    throw new IOException(msg);
                }
            } else {
                // try to delete the submissionXml file, since it is
                // identical to the existing instanceXml file
                // (we don't need to delete and rename anything).
                if (!submissionXml.delete()) {
                    String msg = "Error deleting " + submissionXml.getAbsolutePath()
                            + " (instance is re-openable)";
                    Log.w(t, msg);
                }
            }

            // if encrypted, delete all plaintext files
            // (anything not named instanceXml or anything not ending in .enc)
            if (isEncrypted) {
                if (!EncryptionUtils.deletePlaintextFiles(instanceXml)) {
                    Log.e(t, "Error deleting plaintext files for " + instanceXml.getAbsolutePath());
                }
            }
        }
    }


    /**
     * This method actually writes the xml to disk.
     *
     * @param payload
     * @param path
     * @return
     */
    static void exportXmlFile(ByteArrayPayload payload, String path) throws IOException {
        Log.d(t, "exportxmlfile");
        File file = new File(path);
        if (file.exists() && !file.delete()) {
            throw new IOException("Cannot overwrite " + path + ". Perhaps the file is locked?");
        }

        // create data stream

        InputStream is = payload.getPayloadStream();
        int len = (int) payload.getLength();

        // read from data stream
        byte[] data = new byte[len];
//        try {
        int read = is.read(data, 0, len);
        if (read > 0) {
            // write xml file
            RandomAccessFile randomAccessFile = null;
            try {
                // String filename = path + File.separator +
                // path.substring(path.lastIndexOf(File.separator) + 1) + ".xml";
                randomAccessFile = new RandomAccessFile(file, "rws");
                randomAccessFile.write(data);
            } finally {
                if (randomAccessFile != null) {
                    try {
                        randomAccessFile.close();
                    } catch (IOException e) {
                        Log.e(t, "Error closing RandomAccessFile: " + path, e);
                    }
                }
            }
        }
//        } catch (IOException e) {
//            Log.e(t, "Error reading from payload data stream");
//            e.printStackTrace();
//            return false;
//        }
//
//        return false;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        Log.d(t, "onprogressupdate");
        super.onProgressUpdate(values);

        if (mSavedListener != null && values != null) {
            if (values.length == 1) {
                mSavedListener.onProgressStep(values[0]);
            }
        }
    }

    @Override
    protected void onPostExecute(SaveResult result) {
        Log.d(t, "enpostexecute");
        synchronized (this) {
            if (mSavedListener != null && result != null) {
                mSavedListener.savingComplete(result);
            }
        }
    }


    public void setFormSavedListener(FormSavedListener fsl) {
        Log.d(t, "setformsavedlistener");
        synchronized (this) {
            mSavedListener = fsl;
        }
    }


}
