package org.odk.collect.android.pkl.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;

import org.odk.collect.android.R;
import org.odk.collect.android.pkl.adapter.NotificationAdapter;
import org.odk.collect.android.pkl.database.DatabaseHandler;
import org.odk.collect.android.pkl.object.NotificationModel;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;

import java.util.LinkedList;

public class NotificationActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    private ListView listNotification;
    private LinkedList<NotificationModel> notifications;
    private NotificationAdapter notificationAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MenuItem menuSearch;
    private SearchView sv;
    private String nim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("PEMBERITAHUAN");

        populateViewById();

        nim = (String) CapiPreference.getInstance().get(CapiKey.KEY_NIM);

        notifications = DatabaseHandler.getInstance(
                getApplicationContext()).getNotifications(nim, false);

        new GetNotifications().execute(notifications == null || notifications.isEmpty());

        swipeRefreshLayout.setOnRefreshListener(this);

        notificationAdapter = new NotificationAdapter(this, notifications);

        listNotification.setEmptyView(findViewById(R.id.emptyview));
        listNotification.setAdapter(notificationAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        notificationAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.destroyDrawingCache();
            swipeRefreshLayout.clearAnimation();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_problem, menu);
        menuSearch = menu.findItem(R.id.action_search);
        sv = (SearchView) MenuItemCompat.getActionView(menuSearch);
        sv.setOnQueryTextListener(this);
        sv.setQueryHint("Cari Pemberitahuan");
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (!sv.isIconified() || sv.isFocused()) {
            sv.setIconified(true);
        } else {
            finish();
        }
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (!sv.isIconified() || sv.isFocused()) {
            sv.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRefresh() {
        new GetNotifications().execute(true);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        notificationAdapter.getFilter().filter(newText.toLowerCase());
        return true;
    }

    private class GetNotifications extends AsyncTask<Boolean, Void, LinkedList<NotificationModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected LinkedList<NotificationModel> doInBackground(Boolean... params) {
            return DatabaseHandler.getInstance(
                    getApplicationContext()).getNotifications(nim, params[0]);
        }

        @Override
        protected void onPostExecute(LinkedList<NotificationModel> notificationModels) {
            super.onPostExecute(notificationModels);
            notifications.clear();
            if (notificationModels != null && !notificationModels.isEmpty()) {
                notifications.addAll(notificationModels);
            }
            notificationAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void populateViewById() {
        listNotification = (ListView) findViewById(R.id.list_problem);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
    }
}
