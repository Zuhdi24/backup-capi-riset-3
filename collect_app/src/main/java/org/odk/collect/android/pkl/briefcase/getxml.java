package org.odk.collect.android.pkl.briefcase;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.javarosa.xform.parse.XFormParser;
import org.kxml2.io.KXmlSerializer;
import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.listeners.DownloadPcl;
import org.odk.collect.android.pkl.briefcase.listener.commondownloadlistener;
import org.odk.collect.android.pkl.briefcase.listener.downloadxmllistener;
import org.odk.collect.android.pkl.briefcase.partisi.commondownloadas;
import org.odk.collect.android.pkl.briefcase.partisi.httpretrieveXmlDocumentDescas;
import org.odk.collect.android.pkl.object.Notifikasi;
import org.odk.collect.android.provider.InstanceProviderAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Cloud Walker on 19/02/2016.
 */
public class getxml implements downloadxmllistener, commondownloadlistener {
    ProgressDialog dialog;
    private Context mcontext;
    private DownloadPcl mDownloadPcl;
    private Notifikasi mNotif;
    private static Object object = new Object();
    private static Object ob = new Object();
    private paramsGet para;
    private boolean complete = false;

    public getxml(Context mcontext) {
        this.mcontext = mcontext;
    }


    public DocumentFetchResult doInBackground(paramsGet para) {
        this.para = para;
        try {
            downloadSubmissionWithOutTerminationFuturre(para.getFormInstancesDir(), para.getLfd(), para.getFs(), para.getUri(), para.getInstancedira(), para.getServerInfo());
        } catch (Exception ex) {
            Log.d("getXml", "" + ex);
        }
        return null;
    }


    public void onPostExecute(DocumentFetchResult documentFetchResult) {

        File file = new File(Collect.INSTANCES_PATH + "/" + FileSystemUtils.asFilesystemSafeName(mNotif.getUnique_id_instance())
                + "/" + FileSystemUtils.asFilesystemSafeName(mNotif.getUnique_id_instance()) + ".xml");
        Log.d( "sheerin", "onPostExecute: " + Collect.INSTANCES_PATH + "/" + FileSystemUtils.asFilesystemSafeName(mNotif.getUnique_id_instance())
                + "/" + FileSystemUtils.asFilesystemSafeName(mNotif.getUnique_id_instance()) + ".xml" );
        Log.d( "sheerin", String.valueOf(file.exists()) );
        if (file.exists()) {
            ContentValues values = new ContentValues();
            values.put(InstanceProviderAPI.InstanceColumns.UUID, " ");
            values.put(InstanceProviderAPI.InstanceColumns.STATUS, InstanceProviderAPI.STATUS_INCOMPLETE);
            values.put(InstanceProviderAPI.InstanceColumns.STATUS_ISIAN, mNotif.getStatus_isian());
            values.put(InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH, Collect.INSTANCES_PATH + "/" + FileSystemUtils.asFilesystemSafeName(mNotif.getUnique_id_instance())
                    + "/" + FileSystemUtils.asFilesystemSafeName(mNotif.getUnique_id_instance()) + ".xml");
            values.put(InstanceProviderAPI.InstanceColumns.JR_FORM_ID, mNotif.getForm_id());
            values.put(InstanceProviderAPI.InstanceColumns.DISPLAY_NAME, mNotif.getFilename());
            values.put(InstanceProviderAPI.InstanceColumns.CAN_EDIT_FILENAMA, InstanceProviderAPI.NOs);
            values.put(InstanceProviderAPI.InstanceColumns.NIM, mNotif.getNim());
            values.put(InstanceProviderAPI.InstanceColumns.KORTIM, mNotif.getKortim());
            Collect.getInstance().getContentResolver()
                    .insert(InstanceProviderAPI.InstanceColumns.CONTENT_URI, values);
            mDownloadPcl.onpostdownload(true, mNotif);
        } else {
            mDownloadPcl.onpostdownload(false, mNotif);
        }

    }

    public void setDownloadpcl(DownloadPcl a, Notifikasi notif) {
        mDownloadPcl = a;
        mNotif = notif;
    }

    private static final String MD5_COLON_PREFIX = "md5:";

    public void downloadSubmissionWithOutTerminationFuturre(File formInstancesDir, BriefcaseFormDefinition lfd, FormStatus fs, String uri, String instancedira, ServerConnectionInfo serverInfo) throws Exception {
        String formId = lfd.getSubmissionKey(uri);
        Log.d("download id", formId);

        String baseUrl = serverInfo.getUrl() + "/view/downloadSubmission";
        Log.d("baseUrl", baseUrl);

        Map<String, String> params = new HashMap<String, String>();
        params.put("formId", formId);
        String fullUrl = WebUtils.createLinkWithProperties(baseUrl, params);
        Log.d("baseUrl", fullUrl);
        DocumentFetchResult result;
        Log.d("serverFetcher", "1a");
        try {
            DocumentDescription submissionDescription = new DocumentDescription("Fetch of a submission failed.  Detailed error: ",
                    "Fetch of a submission failed.", "submission");
            httpretrieveXmlDocumentDescas res = new httpretrieveXmlDocumentDescas();
            res.setDownloadXmllis(this);
            res.setparameters(fullUrl, serverInfo, false, submissionDescription, null);
            synchronized (object) {
                res.execute();
            }
        } catch (Exception e) {
            Log.d("download xmlDoc", "" + e);
            throw new SubmissionDownloadException(e.getMessage());
        }


    }

    public static class SubmissionDownloadException extends Exception {

        /**
         *
         */
        private static final long serialVersionUID = 8717375089373674335L;

        SubmissionDownloadException(String message) {
            super(message);
        }
    }

    private void downloadMediaFileIfChanged(File mediaDir, MediaFile m, FormStatus fs, ServerConnectionInfo serverInfo) throws Exception {

        File mediaFile = new File(mediaDir, m.filename);

        if (m.hash.startsWith(MD5_COLON_PREFIX)) {
            // see if the file exists and has the same hash
            String hashToMatch = m.hash.substring(MD5_COLON_PREFIX.length());
            if (mediaFile.exists()) {
                String hash = FileSystemUtils.getMd5Hash(mediaFile);
                if (hash.equalsIgnoreCase(hashToMatch))
                    return;
                mediaFile.delete();
            }
        }

//        if ( isCancelled() ) {
//            fs.setStatusString("aborting fetch of media file...", true);
//            throw new TransmissionException("Transfer cancelled by user.");
//        }

        commonDownloadFile(serverInfo, mediaFile, m.downloadUrl);
    }

    public SubmissionManifest parseDownloadSubmissionResponse(Document doc)
            throws ParsingException {

        // and parse the document...
        List<MediaFile> attachmentList = new ArrayList<MediaFile>();
        Element rootSubmissionElement = null;
        String instanceID = null;

        // Attempt parsing
        Element submissionElement = doc.getRootElement();
        if (!submissionElement.getName().equals("submission")) {
            String msg = "Parsing downloadSubmission reply -- root element is not <submission> :"
                    + submissionElement.getName();
            Log.d(t, msg);
            throw new ParsingException(msg);
        }
        String namespace = submissionElement.getNamespace();
        if (!namespace.equalsIgnoreCase(NAMESPACE_OPENDATAKIT_ORG_SUBMISSIONS)) {
            String msg = "Parsing downloadSubmission reply -- root element namespace is incorrect:"
                    + namespace;
            Log.d(t, msg);
            throw new ParsingException(msg);
        }
        int nElements = submissionElement.getChildCount();
        for (int i = 0; i < nElements; ++i) {
            if (submissionElement.getType(i) != Element.ELEMENT) {
                // e.g., whitespace (text)
                continue;
            }
            Element subElement = submissionElement.getElement(i);
            namespace = subElement.getNamespace();
            if (!namespace.equalsIgnoreCase(NAMESPACE_OPENDATAKIT_ORG_SUBMISSIONS)) {
                // someone else's extension?
                continue;
            }
            String name = subElement.getName();
            if (name.equalsIgnoreCase("data")) {
                // find the root submission element and get its instanceID attribute
                int nIdElements = subElement.getChildCount();
                for (int j = 0; j < nIdElements; ++j) {
                    if (subElement.getType(j) != Element.ELEMENT) {
                        // e.g., whitespace (text)
                        continue;
                    }
                    rootSubmissionElement = subElement.getElement(j);
                    break;
                }
                if (rootSubmissionElement == null) {
                    throw new ParsingException("no submission body found in submissionDownload response");
                }

                instanceID = rootSubmissionElement.getAttributeValue(null, "instanceID");
                if (instanceID == null) {
                    throw new ParsingException("instanceID attribute value is null");
                }
            } else if (name.equalsIgnoreCase("mediaFile")) {
                int nIdElements = subElement.getChildCount();
                String filename = null;
                String hash = null;
                String downloadUrl = null;
                for (int j = 0; j < nIdElements; ++j) {
                    if (subElement.getType(j) != Element.ELEMENT) {
                        // e.g., whitespace (text)
                        continue;
                    }
                    Element mediaSubElement = subElement.getElement(j);
                    name = mediaSubElement.getName();
                    if (name.equalsIgnoreCase("filename")) {
                        filename = XFormParser.getXMLText(mediaSubElement, true);
                    } else if (name.equalsIgnoreCase("hash")) {
                        hash = XFormParser.getXMLText(mediaSubElement, true);
                    } else if (name.equalsIgnoreCase("downloadUrl")) {
                        downloadUrl = XFormParser.getXMLText(mediaSubElement, true);
                    }
                }
                attachmentList.add(new MediaFile(filename, hash, downloadUrl));
            } else {
                Log.d(t, "Unrecognized tag inside submission: " + name);
            }
        }

        if (rootSubmissionElement == null) {
            throw new ParsingException("No submission body found");
        }
        if (instanceID == null) {
            throw new ParsingException("instanceID attribute value is null");
        }

        // write submission to a string
        StringWriter fo = new StringWriter();
        KXmlSerializer serializer = new KXmlSerializer();

        serializer.setOutput(fo);
        // setting the response content type emits the xml header.
        // just write the body here...
        // this has the xmlns of the submissions download, indicating that it
        // originated from a briefcase download. Might be useful for discriminating
        // real vs. recovered data?
        rootSubmissionElement.setPrefix(null, NAMESPACE_OPENDATAKIT_ORG_SUBMISSIONS);
        try {
            rootSubmissionElement.write(serializer);
            serializer.flush();
            serializer.endDocument();
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ParsingException("Unexpected IOException: " + e.getMessage());
        }

        return new SubmissionManifest(instanceID, fo.toString(), attachmentList);
    }

    public static class SubmissionManifest {
        final List<MediaFile> attachmentList;
        final String submissionXml;
        final String instanceID;

        SubmissionManifest(String instanceID, String submissionXml, List<MediaFile> attachmentList) {
            this.instanceID = instanceID;
            this.submissionXml = submissionXml;
            this.attachmentList = attachmentList;
        }
    }

    public static class MediaFile {
        final String filename;
        final String hash;
        final String downloadUrl;

        MediaFile(String filename, String hash, String downloadUrl) {
            this.filename = filename;
            this.hash = hash;
            this.downloadUrl = downloadUrl;
        }
    }

    public static class DocumentFetchResult {
        public final Document doc;
        public final boolean isOpenRosaResponse;

        public DocumentFetchResult(Document doc, boolean isOpenRosaResponse) {
            this.doc = doc;
            this.isOpenRosaResponse = isOpenRosaResponse;
        }
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

    public interface ResponseAction {
        void doAction(DocumentFetchResult result) throws MetadataUpdateException;
    }

    private static final String NAMESPACE_OPENDATAKIT_ORG_SUBMISSIONS = "http://opendatakit.org/submissions";
    private static final int SERVER_CONNECTION_TIMEOUT = 60000;
    private static final String BRIEFCASE_APP_TOKEN_PARAMETER = "briefcaseAppToken";

    private static final CharSequence HTTP_CONTENT_TYPE_TEXT_XML = "text/xml";

    private static final CharSequence HTTP_CONTENT_TYPE_APPLICATION_XML = "application/xml";

    private static final String t = "getxml";

    private static final String FETCH_FAILED_DETAILED_REASON = "Fetch of %1$s failed. Detailed reason: ";

    //    public static final DocumentFetchResult getXmlDocumentWithOutDocumentDesc(String urlString,
//                                                                              ServerConnectionInfo serverInfo, boolean alwaysResetCredentials,
//                                                                              DocumentDescription description, ResponseAction action)
//            throws XmlDocumentFetchException {
//
//
//
//        return httpRetrieveXmlDocumentWithOutDocumentDesc(req, validStatusList, serverInfo, alwaysResetCredentials,
//                description, action);
//    }
//    private static  DocumentFetchResult httpRetrieveXmlDocumentWithOutDocumentDesc(HttpUriRequest request,
//                                                                                        int[] validStatusList, ServerConnectionInfo serverInfo, boolean alwaysResetCredentials,
//                                                                                        DocumentDescription description,
//                                                                                        ResponseAction action) throws XmlDocumentFetchException {
//        httpretrieveXmlDocumentDescas htas= new httpretrieveXmlDocumentDescas();
//        htas.setparameters(request,validStatusList,serverInfo,alwaysResetCredentials,description,action);
//        DocumentFetchResult doc=null;
//        try {
//            synchronized (object) {
//                doc=htas.execute().get();
//            }
//        }catch (Exception ex){
//            Log.d(t,""+ex);
//        }
//        return doc;
//    }
    public static final void commonDownloadFile(ServerConnectionInfo serverInfo, File f,
                                                String downloadUrl) throws URISyntaxException, IOException,
            TransmissionException {
        commondownloadas common = new commondownloadas();
        common.setparameters(serverInfo, f, downloadUrl);
        synchronized (object) {
            common.execute();
        }
    }

    @Override
    public void ondownloadxmlcomplete(boolean complete1, DocumentFetchResult result) throws Exception {
        if (complete1) {
            complete = true;
            File formInstancesDir = para.getFormInstancesDir();
            BriefcaseFormDefinition lfd = para.getLfd();
            FormStatus fs = para.getFs();
            String uri = para.getUri();
            String instancedira = para.getInstancedira();
            ServerConnectionInfo serverInfo = para.getServerInfo();
            Log.d("serverFetcher", "1b");
            // and parse the document...
            SubmissionManifest submissionManifest;
            try {
                submissionManifest = parseDownloadSubmissionResponse(result.doc);
            } catch (ParsingException e) {
                Log.d("download parse", "" + e);
                throw new SubmissionDownloadException(e.getMessage());
            }

            String msg = "Fetched instanceID=" + submissionManifest.instanceID;
            Log.d("download msq", msg);
//    logger.info(msg);

            if (FileSystemUtils.hasFormSubmissionDirectory(formInstancesDir, submissionManifest.instanceID)) {
                // create instance directory...

                File instanceDir = FileSystemUtils.assertFormSubmissionDirectory(formInstancesDir,
                        submissionManifest.instanceID);

                downloadmedia(submissionManifest, instanceDir);

                // fetch attachments


                // write submission file -- we rely on instanceId being unique...

                // if we get here and it was a legacy server (0.9.x), we don't
                // actually know whether the submission was complete.  Otherwise,
                // if we get here, we know that this is a completed submission
                // (because it was in /view/submissionList) and that we safely
                // copied it into the storage area (because we didn't get any
                // exceptions).
                if (serverInfo.isOpenRosaServer()) {
//        formDatabase.assertRecordedInstanceDirectory(uri, instanceDir);
                }
            } else {
                // create instance directory...
                File instanceDir = FileSystemUtils.assertFormSubmissionDirectory(formInstancesDir,
                        submissionManifest.instanceID);

                // fetch attachments
                downloadmedia(submissionManifest, instanceDir);

                // write submission file


                // if we get here and it was a legacy server (0.9.x), we don't
                // actually know whether the submission was complete.  Otherwise,
                // if we get here, we know that this is a completed submission
                // (because it was in /view/submissionList) and that we safely
                // copied it into the storage area (because we didn't get any
                // exceptions).
                if (serverInfo.isOpenRosaServer()) {
//        formDatabase.assertRecordedInstanceDirectory(uri, instanceDir);
                }
            }
//            onPostExecute(null);
//            mDownloadPcl.onpostdownload(complete, mNotif);
        } else {
//            onPostExecute(null);
//            mDownloadPcl.onpostdownload(complete, mNotif);
        }
        onPostExecute(null);
    }

    private void downloadmedia(SubmissionManifest submissionManifest, File instanceDir) throws Exception {
        File formInstancesDir = para.getFormInstancesDir();
        BriefcaseFormDefinition lfd = para.getLfd();
        FormStatus fs = para.getFs();
        String uri = para.getUri();
        String instancedira = para.getInstancedira();
        ServerConnectionInfo serverInfo = para.getServerInfo();
        for (MediaFile m : submissionManifest.attachmentList) {
            //synchronized (ob) {
            downloadMediaFileIfChanged(instanceDir, m, fs, serverInfo);
            //}
        }
        File submissionFile = new File(instanceDir, FileSystemUtils.asFilesystemSafeName(submissionManifest.instanceID) + ".xml");
        OutputStreamWriter fo = new OutputStreamWriter(new FileOutputStream(submissionFile), "UTF-8");
        fo.write(submissionManifest.submissionXml);
        fo.close();
//        mDownloadPcl.onpostdownload(true, mNotif);

    }

    @Override
    public void ondownloadcommoncomplete(boolean complete) {

    }
}
