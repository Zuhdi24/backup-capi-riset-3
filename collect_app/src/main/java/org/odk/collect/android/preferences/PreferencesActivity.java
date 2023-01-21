/*
 * Copyright (C) 2011 University of Washington
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

package org.odk.collect.android.preferences;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.MediaStore.Images;
import androidx.core.content.ContextCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.javarosa.core.services.IPropertyManager;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.logic.FormController;
import org.odk.collect.android.logic.PropertyManager;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;
import org.odk.collect.android.utilities.CompatibilityUtils;
import org.odk.collect.android.utilities.MediaUtils;
import org.odk.collect.android.utilities.UrlUtils;

import java.util.ArrayList;

/**
 * Handles general preferences.
 *
 * @author Thomas Smyth, Sassafras Tech Collective (tom@sassafrastech.com;
 *         constraint behavior option)
 */
public class PreferencesActivity extends PreferenceActivity implements OnPreferenceChangeListener {


    public static final String INTENT_KEY_ADMIN_MODE = "adminMode";
    protected static final int IMAGE_CHOOSER = 0;

    // PUT ALL PREFERENCE KEYS HERE
    public static final String KEY_LAST_VERSION = "lastVersion";
    public static final String KEY_FIRST_RUN = "firstRun";
    public static final String KEY_SHOW_SPLASH = "showSplash";
    public static final String KEY_SPLASH_PATH = "splashPath";
    public static final String KEY_FONT_SIZE = "font_size";
    public static final String KEY_DELETE_AFTER_SEND = "delete_send";

    public static final String KEY_PROTOCOL = "protocol";
    public static final String KEY_PROTOCOL_SETTINGS = "protocol_settings";

    // leaving these in the main screen because username can be used as a
    // pre-fill
    // value in a form
    public static final String KEY_SELECTED_GOOGLE_ACCOUNT = "selected_google_account";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_HOST = "host";

    // AGGREGATE SPECIFIC
    public static final String KEY_SERVER_URL = "server_url";

    // GOOGLE SPECIFIC
    public static final String KEY_GOOGLE_SHEETS_URL = "google_sheets_url";

    // OTHER SPECIFIC
    public static final String KEY_FORMLIST_URL = "formlist_url";
    public static final String KEY_SUBMISSION_URL = "submission_url";

    public static final String NAVIGATION_SWIPE = "swipe";
    public static final String NAVIGATION_BUTTONS = "buttons";

    public static final String CONSTRAINT_BEHAVIOR_ON_SWIPE = "on_swipe";
    public static final String CONSTRAINT_BEHAVIOR_DEFAULT = "on_swipe";

    public static final String KEY_COMPLETED_DEFAULT = "default_completed";

    public static final String KEY_HIGH_RESOLUTION = "high_resolution";

    public static final String KEY_AUTOSEND_WIFI = "autosend_wifi";
    public static final String KEY_AUTOSEND_NETWORK = "autosend_network";

    public static final String KEY_NAVIGATION = "navigation";
    public static final String KEY_CONSTRAINT_BEHAVIOR = "constraint_behavior";

    private PreferenceScreen mSplashPathPreference;

    private ListPreference mSelectedGoogleAccountPreference;
    private ListPreference mFontSizePreference;
    private ListPreference mNavigationPreference;
    private ListPreference mConstraintBehaviorPreference;

    private CheckBoxPreference mAutosendWifiPreference;
    private CheckBoxPreference mAutosendNetworkPreference;
    private ListPreference mProtocolPreference;

    private PreferenceScreen mProtocolSettings;

    protected EditTextPreference mUsernamePreference;
    protected EditTextPreference mPasswordPreference;
    protected ListPreference mHost;

    private String actionBarTitle;

    private static final int MENU_ADMIN = Menu.FIRST;

    private SharedPreferences mAdminPreferences;
    private static final int PASSWORD_DIALOG = 1;


    private CapiPreference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplication(), R.color.colorPrimary)));
        actionBarTitle = getString(R.string.general_preferences);
        setTitle(actionBarTitle);

        // not super safe, but we're just putting in this mode to help
        // administrate
        // would require code to access it
        final boolean adminMode = getIntent().getBooleanExtra(INTENT_KEY_ADMIN_MODE, false);

        SharedPreferences adminPreferences = getSharedPreferences(
                AdminPreferencesActivity.ADMIN_PREFERENCES, 0);

        // assign all the preferences in advance because changing one often
        // affects another
        // also avoids npe
        PreferenceCategory autosendCategory = (PreferenceCategory) findPreference(getString(R.string.autosend));
        mAutosendWifiPreference = (CheckBoxPreference) findPreference(KEY_AUTOSEND_WIFI);
        mAutosendNetworkPreference = (CheckBoxPreference) findPreference(KEY_AUTOSEND_NETWORK);
        PreferenceCategory serverCategory = (PreferenceCategory) findPreference(getString(R.string.server_preferences));

        mProtocolPreference = (ListPreference) findPreference(KEY_PROTOCOL);


        mSelectedGoogleAccountPreference = (ListPreference) findPreference(KEY_SELECTED_GOOGLE_ACCOUNT);
        PreferenceCategory clientCategory = (PreferenceCategory) findPreference(getString(R.string.client));
        mNavigationPreference = (ListPreference) findPreference(KEY_NAVIGATION);
        mFontSizePreference = (ListPreference) findPreference(KEY_FONT_SIZE);
        Preference defaultFinalized = findPreference(KEY_COMPLETED_DEFAULT);
        Preference deleteAfterSend = findPreference(KEY_DELETE_AFTER_SEND);
        mSplashPathPreference = (PreferenceScreen) findPreference(KEY_SPLASH_PATH);
        mConstraintBehaviorPreference = (ListPreference) findPreference(KEY_CONSTRAINT_BEHAVIOR);

        mUsernamePreference = (EditTextPreference) findPreference(PreferencesActivity.KEY_USERNAME);
        mPasswordPreference = (EditTextPreference) findPreference(PreferencesActivity.KEY_PASSWORD);
        mHost = (ListPreference) findPreference(PreferencesActivity.KEY_HOST);
        mHost.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String url = newValue.toString();

                // disallow any whitespace
                if (url.contains(" ") || !url.equals(url.trim())) {
                    Toast.makeText(getApplicationContext(),
                            R.string.url_error_whitespace, Toast.LENGTH_SHORT)
                            .show();
                    return false;
                }

                // remove all trailing "/"s
                while (url.endsWith("/")) {
                    url = url.substring(0, url.length() - 1);
                }

                if (UrlUtils.isValidUrl(url)) {
//                    preference.setSummary(newValue.toString());
                    return true;
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.url_error, Toast.LENGTH_SHORT)
                            .show();
                    return false;
                }
            }
        });

        mProtocolSettings = (PreferenceScreen) findPreference(KEY_PROTOCOL_SETTINGS);

        boolean autosendWifiAvailable = adminPreferences.getBoolean(
                AdminPreferencesActivity.KEY_AUTOSEND_WIFI, false);
        if (!(autosendWifiAvailable || adminMode)) {
            autosendCategory.removePreference(mAutosendWifiPreference);
        }

        boolean autosendNetworkAvailable = adminPreferences.getBoolean(
                AdminPreferencesActivity.KEY_AUTOSEND_NETWORK, false);
        if (!(autosendNetworkAvailable || adminMode)) {
            autosendCategory.removePreference(mAutosendNetworkPreference);
        }

        if (!(autosendNetworkAvailable || autosendWifiAvailable || adminMode)) {
            getPreferenceScreen().removePreference(autosendCategory);
        }

        mProtocolPreference = (ListPreference) findPreference(KEY_PROTOCOL);
        mProtocolPreference.setSummary(mProtocolPreference.getEntry());
        Intent prefIntent = null;

        if (mProtocolPreference.getValue().equals(getString(R.string.protocol_odk_default))) {
            setDefaultAggregatePaths();
            prefIntent = new Intent(this, AggregatePreferencesActivity.class);
        } else if (mProtocolPreference.getValue().equals(
                getString(R.string.protocol_google_sheets))) {
            prefIntent = new Intent(this, GooglePreferencesActivity.class);
        } else {
            // other
            prefIntent = new Intent(this, OtherPreferencesActivity.class);
        }
        prefIntent.putExtra(INTENT_KEY_ADMIN_MODE, adminMode);
        mProtocolSettings.setIntent(prefIntent);

        mProtocolPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String oldValue = ((ListPreference) preference).getValue();
                int index = ((ListPreference) preference).findIndexOfValue(newValue.toString());
                String entry = (String) ((ListPreference) preference).getEntries()[index];
                String value = (String) ((ListPreference) preference).getEntryValues()[index];
                preference.setSummary(entry);

                Intent prefIntent = null;
                if (value.equals(getString(R.string.protocol_odk_default))) {
                    setDefaultAggregatePaths();
                    prefIntent = new Intent(PreferencesActivity.this, AggregatePreferencesActivity.class);
                } else if (value.equals(getString(R.string.protocol_google_sheets))) {
                    prefIntent = new Intent(PreferencesActivity.this, GooglePreferencesActivity.class);
                } else {
                    // other
                    prefIntent = new Intent(PreferencesActivity.this, OtherPreferencesActivity.class);
                }
                prefIntent.putExtra(INTENT_KEY_ADMIN_MODE, adminMode);
                mProtocolSettings.setIntent(prefIntent);

                if (!newValue.equals(oldValue)) {
                    startActivity(prefIntent);
                }

                return true;
            }
        });

        boolean changeProtocol = adminPreferences.getBoolean(
                AdminPreferencesActivity.KEY_CHANGE_SERVER, true);
        if (!(changeProtocol || adminMode)) {
            serverCategory.removePreference(mProtocolPreference);
        }
        boolean changeProtocolSettings = adminPreferences.getBoolean(
                AdminPreferencesActivity.KEY_CHANGE_PROTOCOL_SETTINGS, true);
        if (!(changeProtocolSettings || adminMode)) {
            serverCategory.removePreference(mProtocolSettings);
        }

        // get list of google accounts
        final Account[] accounts = AccountManager.get(getApplicationContext()).getAccountsByType(
                "com.google");
        ArrayList<String> accountEntries = new ArrayList<String>();
        ArrayList<String> accountValues = new ArrayList<String>();

        for (int i = 0; i < accounts.length; i++) {
            accountEntries.add(accounts[i].name);
            accountValues.add(accounts[i].name);
        }
        accountEntries.add(getString(R.string.no_account));
        accountValues.add("");

        mSelectedGoogleAccountPreference.setEntries(accountEntries.toArray(new String[accountEntries
                .size()]));
        mSelectedGoogleAccountPreference.setEntryValues(accountValues.toArray(new String[accountValues
                .size()]));
        mSelectedGoogleAccountPreference
                .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        int index = ((ListPreference) preference).findIndexOfValue(newValue.toString());
                        String value = (String) ((ListPreference) preference).getEntryValues()[index];
                        preference.setSummary(value);
                        return true;
                    }
                });
        mSelectedGoogleAccountPreference.setSummary(mSelectedGoogleAccountPreference.getValue());

        boolean googleAccountAvailable = adminPreferences.getBoolean(
                AdminPreferencesActivity.KEY_CHANGE_GOOGLE_ACCOUNT, false);
        if (!(googleAccountAvailable || adminMode)) {
            serverCategory.removePreference(mSelectedGoogleAccountPreference);
        }

        mAdminPreferences = this.getSharedPreferences(
                AdminPreferencesActivity.ADMIN_PREFERENCES, 0);


        mUsernamePreference.setText(userOdk());
        mUsernamePreference.setOnPreferenceChangeListener(this);
        mUsernamePreference.setSummary(mUsernamePreference.getText());
        mUsernamePreference.getEditText().setFilters(new InputFilter[]{getReturnFilter()});

        boolean usernameAvailable = adminPreferences.getBoolean(
                AdminPreferencesActivity.KEY_CHANGE_USERNAME, false);
        if (!(usernameAvailable || adminMode)) {
            serverCategory.removePreference(mUsernamePreference);
        }

        mPasswordPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String pw = newValue.toString();

                if (pw.length() > 0) {
                    mPasswordPreference.setSummary("********");
                } else {
                    mPasswordPreference.setSummary("");
                }
                return true;
            }
        });
        if (mPasswordPreference.getText() != null && mPasswordPreference.getText().length() > 0) {
            mPasswordPreference.setSummary("********");
        }
        mPasswordPreference.getEditText().setFilters(new InputFilter[]{getReturnFilter()});

        boolean passwordAvailable = adminPreferences.getBoolean(
                AdminPreferencesActivity.KEY_CHANGE_PASSWORD, false);
        if (!(passwordAvailable || adminMode)) {
            serverCategory.removePreference(mPasswordPreference);
        }

        boolean navigationAvailable = adminPreferences.getBoolean(
                AdminPreferencesActivity.KEY_NAVIGATION, true);
        mNavigationPreference.setSummary(mNavigationPreference.getEntry());
        mNavigationPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int index = ((ListPreference) preference).findIndexOfValue(newValue.toString());
                String entry = (String) ((ListPreference) preference).getEntries()[index];
                preference.setSummary(entry);
                return true;
            }
        });
        if (!(navigationAvailable || adminMode)) {
            clientCategory.removePreference(mNavigationPreference);
        }

        boolean constraintBehaviorAvailable = adminPreferences.getBoolean(
                AdminPreferencesActivity.KEY_CONSTRAINT_BEHAVIOR, false);
        mConstraintBehaviorPreference.setSummary(mConstraintBehaviorPreference.getEntry());
        mConstraintBehaviorPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int index = ((ListPreference) preference).findIndexOfValue(newValue.toString());
                String entry = (String) ((ListPreference) preference).getEntries()[index];
                preference.setSummary(entry);
                return true;
            }
        });
        if (!(constraintBehaviorAvailable || adminMode)) {
            clientCategory.removePreference(mConstraintBehaviorPreference);
        }

        boolean fontAvailable = adminPreferences.getBoolean(
                AdminPreferencesActivity.KEY_CHANGE_FONT_SIZE, true);
        mFontSizePreference.setSummary(mFontSizePreference.getEntry());
        mFontSizePreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int index = ((ListPreference) preference).findIndexOfValue(newValue.toString());
                String entry = (String) ((ListPreference) preference).getEntries()[index];
                preference.setSummary(entry);
                return true;
            }
        });
        if (!(fontAvailable || adminMode)) {
            clientCategory.removePreference(mFontSizePreference);
        }

        boolean defaultAvailable = adminPreferences.getBoolean(
                AdminPreferencesActivity.KEY_DEFAULT_TO_FINALIZED, false);

        if (!(defaultAvailable || adminMode)) {
            clientCategory.removePreference(defaultFinalized);
        }

        boolean deleteAfterAvailable = adminPreferences.getBoolean(
                AdminPreferencesActivity.KEY_DELETE_AFTER_SEND, false);
        if (!(deleteAfterAvailable || adminMode)) {
            clientCategory.removePreference(deleteAfterSend);
        }

        boolean resolutionAvailable = adminPreferences.getBoolean(
                AdminPreferencesActivity.KEY_HIGH_RESOLUTION, false);

        Preference highResolution = findPreference(KEY_HIGH_RESOLUTION);
        if (!(resolutionAvailable || adminMode)) {
            clientCategory.removePreference(highResolution);
        }

        if (mAdminPreferences.getString(AdminPreferencesActivity.KEY_ADMIN_PW, "").equals("")) {
            SharedPreferences.Editor editor = mAdminPreferences.edit();
            editor.putString(AdminPreferencesActivity.KEY_ADMIN_PW, "kopidangdut");
            editor.commit();
        }

        mSplashPathPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            private void launchImageChooser() {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i, PreferencesActivity.IMAGE_CHOOSER);
            }

            @Override
            public boolean onPreferenceClick(Preference preference) {
                // if you have a value, you can clear it or select new.
                CharSequence cs = mSplashPathPreference.getSummary();
                if (cs != null && cs.toString().contains("/")) {

                    final CharSequence[] items = {getString(R.string.select_another_image),
                            getString(R.string.use_odk_default)};

                    AlertDialog.Builder builder = new AlertDialog.Builder(PreferencesActivity.this);
                    builder.setTitle(getString(R.string.change_splash_path));
                    builder.setNeutralButton(getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (items[item].equals(getString(R.string.select_another_image))) {
                                launchImageChooser();
                            } else {
                                setSplashPath(getString(R.string.default_splash_path));
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();

                } else {
                    launchImageChooser();
                }

                return true;
            }
        });

        mSplashPathPreference.setSummary(mSplashPathPreference.getSharedPreferences().getString(
                KEY_SPLASH_PATH, getString(R.string.default_splash_path)));

        boolean showSplashAvailable = adminPreferences.getBoolean(
                AdminPreferencesActivity.KEY_SHOW_SPLASH_SCREEN, false);

        CheckBoxPreference showSplashPreference = (CheckBoxPreference) findPreference(KEY_SHOW_SPLASH);

        if (!(showSplashAvailable || adminMode)) {
            clientCategory.removePreference(showSplashPreference);
            clientCategory.removePreference(mSplashPathPreference);
        }

        if (!(fontAvailable || defaultAvailable || showSplashAvailable || navigationAvailable
                || adminMode || resolutionAvailable)) {
            getPreferenceScreen().removePreference(clientCategory);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Collect.getInstance().getActivityLogger()
                .logAction(this, "onCreateOptionsMenu", "show");
        super.onCreateOptionsMenu(menu);

        CompatibilityUtils.setShowAsAction(
                menu.add(0, MENU_ADMIN, 0, R.string.admin_preferences)
                        .setIcon(R.drawable.ic_menu_login),
                MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ADMIN:
                Collect.getInstance().getActivityLogger()
                        .logAction(this, "onOptionsItemSelected", "MENU_ADMIN");
                String pw = mAdminPreferences.getString(
                        AdminPreferencesActivity.KEY_ADMIN_PW, "");
                if ("".equalsIgnoreCase(pw)) {
                    Intent i = new Intent(getApplicationContext(),
                            AdminPreferencesActivity.class);
                    startActivity(i);
                } else {
                    showDialog(PASSWORD_DIALOG);
                    Collect.getInstance().getActivityLogger()
                            .logAction(this, "createAdminPasswordDialog", "show");
                }
                break;
            case android.R.id.home:
                onBackPressed();
                // Toast.makeText(this, "home pressed", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case PASSWORD_DIALOG:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final AlertDialog passwordDialog = builder.create();

                passwordDialog.setTitle(getString(R.string.enter_admin_password));
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                input.setTransformationMethod(PasswordTransformationMethod
                        .getInstance());
                passwordDialog.setView(input, 20, 10, 20, 10);

                passwordDialog.setButton(AlertDialog.BUTTON_POSITIVE,
                        getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                String value = input.getText().toString();
                                String pw = mAdminPreferences.getString(
                                        AdminPreferencesActivity.KEY_ADMIN_PW, "");
                                if (pw.compareTo(value) == 0) {
                                    Intent i = new Intent(getApplicationContext(),
                                            AdminPreferencesActivity.class);
                                    startActivity(i);
                                    input.setText("");
                                    passwordDialog.dismiss();
                                } else {
                                    Toast.makeText(
                                            PreferencesActivity.this,
                                            getString(R.string.admin_password_incorrect),
                                            Toast.LENGTH_SHORT).show();
                                    Collect.getInstance()
                                            .getActivityLogger()
                                            .logAction(this, "adminPasswordDialog",
                                                    "PASSWORD_INCORRECT");
                                }
                            }
                        });

                passwordDialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                        getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                Collect.getInstance()
                                        .getActivityLogger()
                                        .logAction(this, "adminPasswordDialog",
                                                "cancel");
                                input.setText("");
                                return;
                            }
                        });

                passwordDialog.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                return passwordDialog;

        }
        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();

        // the property manager should be re-assigned, as properties
        // may have changed.
        IPropertyManager mgr = new PropertyManager(this);
        FormController.initializeJavaRosa(mgr);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // has to go in onResume because it may get updated by
        // a sub-preference screen
        // this just keeps the widgets in sync
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String account = sp.getString(KEY_SELECTED_GOOGLE_ACCOUNT, "");
        mSelectedGoogleAccountPreference.setSummary(account);
        mSelectedGoogleAccountPreference.setValue(account);

        //String user = sp.getString(KEY_USERNAME, getResources().getString(R.string.default_username));
        String user = userOdk();
        String pw = sp.getString(KEY_PASSWORD, getResources().getString(R.string.default_password));
        mUsernamePreference.setSummary(user);
        mUsernamePreference.setText(user);
        if (pw != null && pw.length() > 0) {
            mPasswordPreference.setSummary("********");
            mPasswordPreference.setText(pw);
        }

    }

    private void setSplashPath(String path) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SPLASH_PATH, path);
        editor.commit();

        mSplashPathPreference = (PreferenceScreen) findPreference(KEY_SPLASH_PATH);
        mSplashPathPreference.setSummary(mSplashPathPreference.getSharedPreferences().getString(
                KEY_SPLASH_PATH, getString(R.string.default_splash_path)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_CANCELED) {
            // request was canceled, so do nothing
            return;
        }

        switch (requestCode) {
            case IMAGE_CHOOSER:

                // get gp of chosen file
                Uri selectedMedia = intent.getData();
                String sourceMediaPath = MediaUtils.getPathFromUri(this, selectedMedia, Images.Media.DATA);

                // setting image path
                setSplashPath(sourceMediaPath);
                break;
        }
    }

    private void setDefaultAggregatePaths() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = sp.edit();
        editor.putString(KEY_FORMLIST_URL, getString(R.string.default_odk_formlist));
        editor.putString(KEY_SUBMISSION_URL, getString(R.string.default_odk_submission));
        editor.commit();
    }

    private String userOdk(){
        preference = CapiPreference.getInstance();
        String jabatan = String.valueOf(preference.get(CapiKey.KEY_ID_JABATAN));

        if(jabatan.equals("1")) return ("pcl62");
        else return ("pcl62");

    }

    /**
     * Disallows carriage returns from user entry
     *
     * @return
     */
    protected InputFilter getReturnFilter() {
        InputFilter returnFilter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
                                       int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.getType((source.charAt(i))) == Character.CONTROL) {
                        return "";
                    }
                }
                return null;
            }
        };
        return returnFilter;
    }

    /**
     * Generic listener that sets the summary to the newly selected/entered value
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        preference.setSummary((CharSequence) newValue);
        return true;
    }

}
