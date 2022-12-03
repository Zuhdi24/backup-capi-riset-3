package org.odk.collect.android.pkl.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.pkl.object.Kondef;
import org.odk.collect.android.pkl.object.NotificationModel;
import org.odk.collect.android.preferences.PreferencesActivity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

/**
 * @author Rahadi
 */

public class RequestHandler {
    private Context context;
    private RequestQueue requestQueue;
    private SharedPreferences settings;
    private String server;

    private static RequestHandler sInstance;

    public static synchronized RequestHandler getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new RequestHandler(context);
        }
        return sInstance;
    }

    private RequestHandler(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        this.settings = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public LinkedList<NotificationModel> getNotifications(String nim) {
        LinkedList<NotificationModel> nms = new LinkedList<>();

        RequestFuture<String> future = RequestFuture.newFuture();
        HashMap<String, String> params = new HashMap<>();
        params.put(Kondef.PAR_NIM, nim);

        StringRequest request = new NoRetryRequest(Request.Method.GET,
                getRequestURL(Kondef.REQ_GET_NOTIFICATIONS, params), future, future);

        Log.d("DEBUGJARKOM", "Absolute URL : " + request.getUrl());
        requestQueue.add(request);

        try {
            JSONObject j = new JSONObject(future.get());
            JSONArray dk = j.getJSONArray(Kondef.RESP_DAFTAR_NOTIFIKASI);
            for (int i = 0; i < dk.length(); i++) {
                JSONObject o = dk.getJSONObject(i);
                NotificationModel nm = new NotificationModel(o.getInt(Kondef.RESP_ID),
                        o.getString(Kondef.RESP_KATEGORI), o.getString(Kondef.RESP_JUDUL),
                        o.getString(Kondef.RESP_KONTEN), o.getString(Kondef.RESP_TIMESTAMP),
                        o.getString(Kondef.RESP_NIM), 0);
                nms.add(nm);
            }
        } catch (InterruptedException e) {
            Log.e("DEBUGJARKOM", "Future Get Interrupted : " + e);
        } catch (ExecutionException e) {
            Log.e("DEBUGJARKOM", "Future Get Execution Error : " + e);
            return null;
        } catch (JSONException e) {
            Log.e("DEBUGJARKOM", "JSON Error : " + e);
        }

        return nms;
    }

    public String getRequestURL(String request) {
        this.server = settings.getString(PreferencesActivity.KEY_HOST, Collect.getInstance().getString(R.string.default_PCL_server_url));
        String result = server + request;
//        String result = Kondef.REQ_BASE_URL + request;
        return result;
    }

    public String getRequestURL(String request, HashMap<String, String> params) {
        this.server = settings.getString(PreferencesActivity.KEY_HOST, Collect.getInstance().getString(R.string.default_PCL_server_url));
        String result = server + request + "?";
//        String result = Kondef.REQ_BASE_URL + request + "?";
        for (HashMap.Entry<String, String> entry : params.entrySet()) {
            result += entry.getKey() + "=" + entry.getValue() + "&";
        }
        result = result.substring(0, result.length() - 1);
        return result;
    }

    private class NoRetryRequest extends StringRequest {

        public NoRetryRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
            setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }
    }
}
