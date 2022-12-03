package org.odk.collect.android.pkl.briefcase.partisi;

/**
 * Created by Cloud Walker on 24/02/2016.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;
import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Document;
import org.odk.collect.android.pkl.briefcase.DocumentDescription;
import org.odk.collect.android.pkl.briefcase.MetadataUpdateException;
import org.odk.collect.android.pkl.briefcase.ServerConnectionInfo;
import org.odk.collect.android.pkl.briefcase.WebUtils;
import org.odk.collect.android.pkl.briefcase.XmlDocumentFetchException;
import org.odk.collect.android.pkl.briefcase.getxml.DocumentFetchResult;
import org.odk.collect.android.pkl.briefcase.getxml.ResponseAction;
import org.odk.collect.android.pkl.briefcase.listener.downloadxmllistener;
import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;

public class httpretrieveXmlDocumentDescas extends AsyncTask<String, String, DocumentFetchResult> {

    private String t = "XmlDocumentDescas";

    private static final String NAMESPACE_OPENDATAKIT_ORG_SUBMISSIONS = "http://opendatakit.org/submissions";
    private static final int SERVER_CONNECTION_TIMEOUT = 60000;
    private static final String BRIEFCASE_APP_TOKEN_PARAMETER = "briefcaseAppToken";

    private static final CharSequence HTTP_CONTENT_TYPE_TEXT_XML = "text/xml";

    private static final CharSequence HTTP_CONTENT_TYPE_APPLICATION_XML = "application/xml";


    private static final String FETCH_FAILED_DETAILED_REASON = "Fetch of %1$s failed. Detailed reason: ";

    private downloadxmllistener mdownload;
    private boolean complete = false;
    private Context context;

    public void setDownloadXmllis(downloadxmllistener as) {
        mdownload = as;
    }

    private HttpUriRequest request;
    private int[] validStatusList;
    private ServerConnectionInfo serverInfo;
    private boolean alwaysResetCredentials;
    private DocumentDescription description;
    private ResponseAction action;

    public void setparameters(String urlString,
                              ServerConnectionInfo serverInfo, boolean alwaysResetCredentials,
                              DocumentDescription description, ResponseAction action) {
        try {
            URI u = null;
            try {
                URL url = new URL(urlString);
                u = url.toURI();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                String msg = description.getFetchDocFailed() + "Invalid url: " + urlString + ".\nFailed with error: "
                        + e.getMessage();
//      log.severe(msg);
                throw new XmlDocumentFetchException(msg);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                String msg = description.getFetchDocFailed() + "Invalid uri: " + urlString + ".\nFailed with error: "
                        + e.getMessage();
//      log.severe(msg);
                throw new XmlDocumentFetchException(msg);
            }

            HttpClient httpClient = serverInfo.getHttpClient();
            if (httpClient == null) {
                httpClient = WebUtils.createHttpClient(SERVER_CONNECTION_TIMEOUT);
                serverInfo.setHttpClient(httpClient);
            }

            // get shared HttpContext so that authentication and cookies are retained.
            HttpContext localContext = serverInfo.getHttpContext();
            if (localContext == null) {
                localContext = WebUtils.createHttpContext();
                serverInfo.setHttpContext(localContext);
            }

            // set up request...
            HttpGet req = WebUtils.createOpenRosaHttpGet(u);

            int[] validStatusList = {200};
            request = req;
            this.validStatusList = validStatusList;
            this.serverInfo = serverInfo;
            this.alwaysResetCredentials = alwaysResetCredentials;
            this.description = description;
            this.action = action;

        } catch (Exception ex) {
            Log.e(t, "setparameters : " + ex);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected DocumentFetchResult doInBackground(String... params) {
        try {
            DocumentFetchResult doc = download(request, validStatusList, serverInfo, alwaysResetCredentials, description, action);
            return doc;
        } catch (Exception ex) {
            Log.e(t, "documentfetchresult : " + ex);
            return null;
        }

    }

    public DocumentFetchResult download(HttpUriRequest request,
                                        int[] validStatusList, ServerConnectionInfo serverInfo, boolean alwaysResetCredentials,
                                        DocumentDescription description,
                                        ResponseAction action) throws XmlDocumentFetchException {
        HttpClient httpClient = serverInfo.getHttpClient();

        // get shared HttpContext so that authentication and cookies are retained.
        HttpContext localContext = serverInfo.getHttpContext();

        URI u = request.getURI();

        if (serverInfo.getUsername() != null && serverInfo.getUsername().length() != 0) {
            if (alwaysResetCredentials
                    || !WebUtils.hasCredentials(localContext, serverInfo.getUsername(), u.getHost())) {
                WebUtils.clearAllCredentials(localContext);
                WebUtils.addCredentials(localContext, serverInfo.getUsername(), serverInfo.getPassword(),
                        u.getHost());
            }
        } else {
            WebUtils.clearAllCredentials(localContext);
        }

        if (!serverInfo.isOpenRosaServer()) {
            request.addHeader(BRIEFCASE_APP_TOKEN_PARAMETER, serverInfo.getToken());
        }

//    if ( description.isCancelled() ) {
//      throw new XmlDocumentFetchException("Transfer of " + description.getDocumentDescriptionType() + " aborted.");
//    }

        HttpResponse response = null;
        try {
            response = httpClient.execute(request, localContext);
            int statusCode = response.getStatusLine().getStatusCode();

            HttpEntity entity = response.getEntity();
            String lcContentType = (entity == null) ? null : entity.getContentType().getValue()
                    .toLowerCase();

            XmlDocumentFetchException ex = null;
            boolean statusCodeValid = false;
            for (int i : validStatusList) {
                if (i == statusCode) {
                    statusCodeValid = true;
                    break;
                }
            }
            // if anything is amiss, ex will be non-null after this cascade.

            if (!statusCodeValid) {
                String webError = response.getStatusLine().getReasonPhrase() + " (" + statusCode + ")";

                if (statusCode == 400) {
                    ex = new XmlDocumentFetchException(description.getFetchDocFailed() + webError + " while accessing: "
                            + u.toString() + "\nPlease verify that the " + description.getDocumentDescriptionType()
                            + " that is being uploaded is well-formed.");
                } else {
                    ex = new XmlDocumentFetchException(
                            description.getFetchDocFailed()
                                    + webError
                                    + " while accessing: "
                                    + u.toString()
                                    + "\nPlease verify that the URL, your user credentials and your permissions are all correct.");
                }
            } else if (entity == null) {
//        log.severe("No entity body returned from: " + u.toString() + " is not text/xml");
                ex = new XmlDocumentFetchException(description.getFetchDocFailed()
                        + " Server unexpectedly returned no content while accessing: " + u.toString());
            } else if (!(lcContentType.contains(HTTP_CONTENT_TYPE_TEXT_XML) || lcContentType
                    .contains(HTTP_CONTENT_TYPE_APPLICATION_XML))) {
                Log.e(t, "ContentType: " + entity.getContentType().getValue() + "returned from: "
                        + u.toString() + " is not text/xml");
                ex = new XmlDocumentFetchException(description.getFetchDocFailed()
                        + "A non-XML document was returned while accessing: " + u.toString()
                        + "\nA network login screen may be interfering with the transmission to the server.");
            }

            if (ex != null) {
                flushEntityBytes(entity);
                // and throw the exception...
                //throw ex;
                Log.e(t, "ex 223 : " + ex);
            }

            // parse the xml document...
            Document doc = null;
            try {
                InputStream is = null;
                InputStreamReader isr = null;
                try {
                    is = entity.getContent();
                    isr = new InputStreamReader(is, "UTF-8");
                    doc = new Document();
                    KXmlParser parser = new KXmlParser();
                    parser.setInput(isr);
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
                    doc.parse(parser);
                    isr.close();
                } finally {
                    if (isr != null) {
                        try {
                            isr.close();
                        } catch (Exception e) {
                            // no-op
                        }
                    }
                    if (is != null) {
                        try {
                            is.close();
                        } catch (Exception e) {
                            // no-op
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(t, "Parsing failed with " + e.getMessage());
                //throw new XmlDocumentFetchException(description.getFetchDocFailed() + " while accessing: " + u.toString());
                Log.e(t, "" + new XmlDocumentFetchException(description.getFetchDocFailed() + " while accessing: " + u.toString()));

                //needed to stop the download process
                return null;
            }

            // examine header fields...

            // is it an OpenRosa server?
            boolean isOR = false;
            Header[] fields = response.getHeaders(WebUtils.OPEN_ROSA_VERSION_HEADER);
            if (fields != null && fields.length >= 1) {
                isOR = true;
                boolean versionMatch = false;
                boolean first = true;
                StringBuilder b = new StringBuilder();
                for (Header h : fields) {
                    if (WebUtils.OPEN_ROSA_VERSION.equals(h.getValue())) {
                        versionMatch = true;
                        break;
                    }
                    if (!first) {
                        b.append("; ");
                    }
                    first = false;
                    b.append(h.getValue());
                }
                if (!versionMatch) {
//          log.warning(WebUtils.OPEN_ROSA_VERSION_HEADER + " unrecognized version(s): "
//                  + b.toString());
                }
            }

            // what about location?
            Header[] locations = response.getHeaders("Location");
            if (locations != null && locations.length == 1) {
                try {
                    URL url = new URL(locations[0].getValue());
                    URI uNew = url.toURI();
                    if (u.getHost().equalsIgnoreCase(uNew.getHost())) {
                        // trust the server to tell us a new location
                        // ... and possibly to use https instead.
                        String fullUrl = url.toExternalForm();
                        int idx = fullUrl.lastIndexOf("/");
                        serverInfo.setUrl(fullUrl.substring(0, idx));
                    } else {
                        // Don't follow a redirection attempt to a different host.
                        // We can't tell if this is a spoof or not.
                        String msg = description.getFetchDocFailed() + "Unexpected redirection attempt to a different host: "
                                + uNew.toString();
//            log.severe(msg);
                        // throw new XmlDocumentFetchException(msg);
                        Log.e(t, "msg 309 : " + new XmlDocumentFetchException(msg));
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    String msg = description.getFetchDocFailed() + "Unexpected exception: " + e.getMessage();
//          log.severe(msg);
                    //throw new XmlDocumentFetchException(msg);
                    Log.e(t, "msg 316 : " + new XmlDocumentFetchException(msg));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    String msg = description.getFetchDocFailed() + "Unexpected exception: " + e.getMessage();
//          log.severe(msg);
                    //throw new XmlDocumentFetchException(msg);
                    Log.e(t, "msg 322 : " + new XmlDocumentFetchException(msg));
                }
            }
            DocumentFetchResult result = new DocumentFetchResult(doc, isOR);
            if (action != null) {
                action.doAction(result);
            }
            complete = true;
            return result;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            String msg = description.getFetchDocFailed() + "Unexpected exception: " + e.getMessage();
//      log.severe(msg);
            throw new XmlDocumentFetchException(msg);
            // Log.e(t,""+new XmlDocumentFetchException(msg));
        } catch (IOException e) {
            e.printStackTrace();
            String msg;
            if (e instanceof UnknownHostException) {
                msg = description.getFetchDocFailed() + "Unknown host: " + e.getMessage();
            } else {
                msg = description.getFetchDocFailed() + "Unexpected " + e.getClass().getName() + ": " + e.getMessage();
            }
//      log.severe(msg);
            throw new XmlDocumentFetchException(msg);
            //Log.e(t,""+new XmlDocumentFetchException(msg));
        } catch (MetadataUpdateException e) {
            e.printStackTrace();
            String msg = description.getFetchDocFailed() + "Unexpected exception: " + e.getMessage();
//      log.severe(msg);
            throw new XmlDocumentFetchException(msg);
            //Log.e(t,""+new XmlDocumentFetchException(msg));
        }
    }

    @Override
    protected void onPostExecute(DocumentFetchResult documentFetchResult) {
        super.onPostExecute(documentFetchResult);
//        if (complete) {
        Log.w("RAHADI", "Completed : " + complete);
        try {
            mdownload.ondownloadxmlcomplete(complete, documentFetchResult);
        } catch (Exception ex) {
            Log.e(t, "ondownloadxmlcomplete : " + ex);
        }
//        } else {

//        }
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
