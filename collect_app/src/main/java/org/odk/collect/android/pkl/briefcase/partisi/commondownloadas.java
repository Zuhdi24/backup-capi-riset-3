package org.odk.collect.android.pkl.briefcase.partisi;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HttpContext;
import org.odk.collect.android.pkl.briefcase.ServerConnectionInfo;
import org.odk.collect.android.pkl.briefcase.TransmissionException;
import org.odk.collect.android.pkl.briefcase.WebUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by Cloud Walker on 24/02/2016.
 */
public class commondownloadas extends AsyncTask<String, String, String> {
    private ServerConnectionInfo serverInfo;
    private File f;
    private String downloadUrl;
    private String t = "commondownloadas";
    private static final String NAMESPACE_OPENDATAKIT_ORG_SUBMISSIONS = "http://opendatakit.org/submissions";
    private static final int SERVER_CONNECTION_TIMEOUT = 60000;
    private static final String BRIEFCASE_APP_TOKEN_PARAMETER = "briefcaseAppToken";

    private static final String FETCH_FAILED_DETAILED_REASON = "Fetch of %1$s failed. Detailed reason: ";

    public void setparameters(ServerConnectionInfo serverInfo, File f, String downloadUrl) {
        this.serverInfo = serverInfo;
        this.f = f;
        this.downloadUrl = downloadUrl;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            // OK. We need to download it because we either:
            // (1) don't have it
            // (2) don't know if it is changed because the hash is not md5
            // (3) know it is changed
            URI u = null;
            try {
                URL uurl = new URL(downloadUrl);
                u = uurl.toURI();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d(t, "" + e);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Log.d(t, "" + e);
            }

            // get shared HttpContext so that authentication and cookies are retained.
            HttpContext localContext = serverInfo.getHttpContext();

            HttpClient httpclient = serverInfo.getHttpClient();

            // set up request...
            HttpGet req = WebUtils.createOpenRosaHttpGet(u);

            if (serverInfo.getUsername() != null && serverInfo.getUsername().length() != 0) {
                if (!WebUtils.hasCredentials(localContext, serverInfo.getUsername(), u.getHost())) {
                    WebUtils.clearAllCredentials(localContext);
                    WebUtils.addCredentials(localContext, serverInfo.getUsername(), serverInfo.getPassword(),
                            u.getHost());
                }
            } else {
                WebUtils.clearAllCredentials(localContext);
            }

            if (!serverInfo.isOpenRosaServer()) {
                req.addHeader(BRIEFCASE_APP_TOKEN_PARAMETER, serverInfo.getToken());
            }

            HttpResponse response = null;
            // try
            {
                response = httpclient.execute(req, localContext);
                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != 200) {
                    String errMsg = String.format(FETCH_FAILED_DETAILED_REASON, f.getAbsolutePath())
                            + response.getStatusLine().getReasonPhrase() + " (" + statusCode + ")";
                    Log.d(t, errMsg);
                    flushEntityBytes(response.getEntity());
                    throw new TransmissionException(errMsg);
                }

                // write connection to file
                InputStream is = null;
                OutputStream os = null;
                try {
                    is = response.getEntity().getContent();
                    os = new FileOutputStream(f);
                    byte buf[] = new byte[1024];
                    int len;
                    while ((len = is.read(buf)) > 0) {
                        os.write(buf, 0, len);
                    }
                    os.flush();
                } finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (Exception e) {
                        }
                    }
                    if (is != null) {
                        try {
                            is.close();
                        } catch (Exception e) {
                        }
                    }
                }

            }
        } catch (Exception ex) {
            Log.d(t, "" + ex);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    private static final void flushEntityBytes(HttpEntity entity) {
        if (entity != null) {
            // something is amiss -- read and discard any response body.
            try {
                // don't really care about the stream...
                InputStream is = entity.getContent();
                // read to end of stream...
                final long count = 1024L;
                while (is.skip(count) == count)
                    ;
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
