/*
 * Copyright (C) 2011 University of Washington.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.pkl.briefcase;

import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.javarosa.xform.parse.XFormParser;
import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.odk.collect.android.application.Collect;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class XmlManipulationUtils {

    private static final String ODK_ID_PARAMETER_EQUALS = "odkId=";

    private static final String t = "XmlManipulationUtils";

    private static final String BAD_OPENROSA_FORMLIST = "The server has not provided an available-forms document compliant with the OpenRosa version 1.0 standard.";

    private static final String BAD_LEGACY_FORMLIST = "The server has not provided an available-forms document compatible with Aggregate 0.9.x.";

    private static final String BAD_NOT_OPENROSA_MANIFEST = "The server did not return an OpenRosa compliant manifest.";

    private static final String NAMESPACE_OPENROSA_ORG_XFORMS_XFORMS_MANIFEST = "http://openrosa.org/xforms/xformsManifest";

    private static final String NAMESPACE_OPENROSA_ORG_XFORMS_XFORMS_LIST = "http://openrosa.org/xforms/xformsList";

    private static final String NAMESPACE_OPENDATAKIT_ORG_SUBMISSIONS = "http://opendatakit.org/submissions";

    private static final String NAMESPACE_ODK = "http://www.opendatakit.org/xforms";

    // NOTE: the only transfered metadata is the instanceID and the submissionDate

    // private static final String FORM_ID_ATTRIBUTE_NAME = "id";
    // private static final String MODEL_VERSION_ATTRIBUTE_NAME = "version";
    // private static final String UI_VERSION_ATTRIBUTE_NAME = "uiVersion";
    private static final String INSTANCE_ID_ATTRIBUTE_NAME = "instanceID";
    private static final String SUBMISSION_DATE_ATTRIBUTE_NAME = "submissionDate";

    // private static final String IS_COMPLETE_ATTRIBUTE_NAME = "isComplete";
    // private static final String MARKED_AS_COMPLETE_DATE_ATTRIBUTE_NAME =
    // "markedAsCompleteDate";

    private static final boolean isXformsListNamespacedElement(Element e) {
        return e.getNamespace().equalsIgnoreCase(NAMESPACE_OPENROSA_ORG_XFORMS_XFORMS_LIST);
    }

    private static final boolean isXformsManifestNamespacedElement(Element e) {
        return e.getNamespace().equalsIgnoreCase(NAMESPACE_OPENROSA_ORG_XFORMS_XFORMS_MANIFEST);
    }

    private static final String OPEN_ROSA_NAMESPACE_PRELIM = "http://openrosa.org/xforms/metadata";
    private static final String OPEN_ROSA_NAMESPACE = "http://openrosa.org/xforms";
    private static final String OPEN_ROSA_NAMESPACE_SLASH = "http://openrosa.org/xforms/";
    private static final String OPEN_ROSA_METADATA_TAG = "meta";
    private static final String OPEN_ROSA_INSTANCE_ID = "instanceID";
    private static final String BASE64_ENCRYPTED_FIELD_KEY = "base64EncryptedFieldKey";

    private static final String UTF_8 = "UTF-8";

    /**
     * Traverse submission looking for OpenRosa metadata tag (with or without
     * namespace).
     *
     * @param parent
     * @return
     */
    private static Element findMetaTag(Element parent, String rootUri) {
        for (int i = 0; i < parent.getChildCount(); ++i) {
            if (parent.getType(i) == Node.ELEMENT) {
                Element child = parent.getElement(i);
                String cnUri = child.getNamespace();
                String cnName = child.getName();
                if (cnName.equals(OPEN_ROSA_METADATA_TAG)
                        && (cnUri == null ||
                        cnUri.equals(EMPTY_STRING) ||
                        cnUri.equals(rootUri) ||
                        cnUri.equalsIgnoreCase(OPEN_ROSA_NAMESPACE) ||
                        cnUri.equalsIgnoreCase(OPEN_ROSA_NAMESPACE_SLASH) ||
                        cnUri.equalsIgnoreCase(OPEN_ROSA_NAMESPACE_PRELIM))) {
                    return child;
                } else {
                    Element descendent = findMetaTag(child, rootUri);
                    if (descendent != null)
                        return descendent;
                }
            }
        }
        return null;
    }

    /**
     * Find the OpenRosa instanceID defined for this record, if any.
     *
     * @return
     */
    public static String getOpenRosaInstanceId(Element root) {
        String rootUri = root.getNamespace();
        Element meta = findMetaTag(root, rootUri);
        if (meta != null) {
            for (int i = 0; i < meta.getChildCount(); ++i) {
                if (meta.getType(i) == Node.ELEMENT) {
                    Element child = meta.getElement(i);
                    String cnUri = child.getNamespace();
                    String cnName = child.getName();
                    if (cnName.equals(OPEN_ROSA_INSTANCE_ID)
                            && (cnUri == null ||
                            cnUri.equals(EMPTY_STRING) ||
                            cnUri.equals(rootUri) ||
                            cnUri.equalsIgnoreCase(OPEN_ROSA_NAMESPACE) ||
                            cnUri.equalsIgnoreCase(OPEN_ROSA_NAMESPACE_SLASH) ||
                            cnUri.equalsIgnoreCase(OPEN_ROSA_NAMESPACE_PRELIM))) {
                        return XFormParser.getXMLText(child, true);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Encrypted field-level encryption key.
     *
     * @param root
     * @return
     */
    public static String getBase64EncryptedFieldKey(Element root) {
        String rootUri = root.getNamespace();
        Element meta = findMetaTag(root, rootUri);
        if (meta != null) {
            for (int i = 0; i < meta.getChildCount(); ++i) {
                if (meta.getType(i) == Node.ELEMENT) {
                    Element child = meta.getElement(i);
                    String cnUri = child.getNamespace();
                    String cnName = child.getName();
                    if (cnName.equals(BASE64_ENCRYPTED_FIELD_KEY)
                            && (cnUri == null ||
                            cnUri.equals(EMPTY_STRING) ||
                            cnUri.equals(rootUri) ||
                            cnUri.equalsIgnoreCase(OPEN_ROSA_NAMESPACE) ||
                            cnUri.equalsIgnoreCase(OPEN_ROSA_NAMESPACE_SLASH))) {
                        return XFormParser.getXMLText(child, true);
                    }
                }
            }
        }
        return null;
    }

    public static class FormInstanceMetadata {
        public final XFormParameters xparam;
        public final String instanceId; // this may be null
        public final String base64EncryptedFieldKey; // this may be null

        FormInstanceMetadata(XFormParameters xparam, String instanceId, String base64EncryptedFieldKey) {
            this.xparam = xparam;
            this.instanceId = instanceId;
            this.base64EncryptedFieldKey = base64EncryptedFieldKey;
        }
    }

    private static final String FORM_ID_ATTRIBUTE_NAME = "id";
    private static final String EMPTY_STRING = "";
    private static final String NAMESPACE_ATTRIBUTE = "xmlns";
    private static final String MODEL_VERSION_ATTRIBUTE_NAME = "version";

    public static FormInstanceMetadata getFormInstanceMetadata(Element root) throws ParsingException {

        // check for odk id
        String formId = root.getAttributeValue(null, FORM_ID_ATTRIBUTE_NAME);

        // if odk id is not present use namespace
        if (formId == null || formId.equalsIgnoreCase(EMPTY_STRING)) {
            String schema = root.getAttributeValue(null, NAMESPACE_ATTRIBUTE);

            // TODO: move this into FormDefinition?
            if (schema == null) {
                throw new ParsingException("Unable to extract form id");
            }

            formId = schema;
        }

        String modelVersionString = root.getAttributeValue(null, MODEL_VERSION_ATTRIBUTE_NAME);

        String instanceId = getOpenRosaInstanceId(root);
        if (instanceId == null) {
            instanceId = root.getAttributeValue(null, INSTANCE_ID_ATTRIBUTE_NAME);
        }
        String base64EncryptedFieldKey = getBase64EncryptedFieldKey(root);
        return new FormInstanceMetadata(new XFormParameters(formId, modelVersionString), instanceId, base64EncryptedFieldKey);
    }

    public static Document parseXml(File submission) throws ParsingException, FileSystemException {

        // parse the xml document...
        Document doc = null;
        try {
            InputStream is = null;
            InputStreamReader isr = null;
            try {
                is = new FileInputStream(submission);
                isr = new InputStreamReader(is, UTF_8);
                Document tempDoc = new Document();
                KXmlParser parser = new KXmlParser();
                parser.setInput(isr);
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
                tempDoc.parse(parser);
                isr.close();
                doc = tempDoc;
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
        } catch (XmlPullParserException e) {
            try {
                return BadXMLFixer.fixBadXML(submission);
            } catch (CannotFixXMLException e1) {
                File debugFileLocation = new File(Collect.BRIEFCASE_PATH, "debug");
                try {
                    if (!debugFileLocation.exists()) {
                        FileUtils.forceMkdir(debugFileLocation);
                    }
                    long checksum = FileUtils.checksumCRC32(submission);
                    File debugFile = new File(debugFileLocation, "submission-" + checksum + ".xml");
                    FileUtils.copyFile(submission, debugFile);
                } catch (IOException e2) {
                    throw new RuntimeException(e2);
                }
                throw new ParsingException("Failed during parsing of submission Xml: "
                        + e.toString());
            }
        } catch (IOException e) {
            throw new FileSystemException("Failed while reading submission xml: "
                    + e.toString());
        }
        return doc;
    }

    public static final List<RemoteFormDefinition> parseFormListResponse(boolean isOpenRosaResponse,
                                                                         Document formListDoc) throws ParsingException {
        // This gets a list of available forms from the specified server.
        List<RemoteFormDefinition> formList = new ArrayList<RemoteFormDefinition>();

        if (isOpenRosaResponse) {
            // Attempt OpenRosa 1.0 parsing
            Element xformsElement = formListDoc.getRootElement();
            if (!xformsElement.getName().equals("xforms")) {
                Log.d(t, "Parsing OpenRosa reply -- root element is not <xforms> :"
                        + xformsElement.getName());
                throw new ParsingException(BAD_OPENROSA_FORMLIST);
            }
            String namespace = xformsElement.getNamespace();
            if (!isXformsListNamespacedElement(xformsElement)) {
                Log.d(t, "Parsing OpenRosa reply -- root element namespace is incorrect:" + namespace);
                throw new ParsingException(BAD_OPENROSA_FORMLIST);
            }
            int nElements = xformsElement.getChildCount();
            for (int i = 0; i < nElements; ++i) {
                if (xformsElement.getType(i) != Element.ELEMENT) {
                    // e.g., whitespace (text)
                    continue;
                }
                Element xformElement = xformsElement.getElement(i);
                if (!isXformsListNamespacedElement(xformElement)) {
                    // someone else's extension?
                    continue;
                }
                String name = xformElement.getName();
                if (!name.equalsIgnoreCase("xform")) {
                    // someone else's extension?
                    continue;
                }

                // this is something we know how to interpret
                String formId = null;
                String formName = null;
                String version = null;
                String majorMinorVersion = null;
                String description = null;
                String downloadUrl = null;
                String manifestUrl = null;
                // don't process descriptionUrl
                int fieldCount = xformElement.getChildCount();
                for (int j = 0; j < fieldCount; ++j) {
                    if (xformElement.getType(j) != Element.ELEMENT) {
                        // whitespace
                        continue;
                    }
                    Element child = xformElement.getElement(j);
                    if (!isXformsListNamespacedElement(child)) {
                        // someone else's extension?
                        continue;
                    }
                    String tag = child.getName();
                    if (tag.equals("formID")) {
                        formId = XFormParser.getXMLText(child, true);
                        if (formId != null && formId.length() == 0) {
                            formId = null;
                        }
                    } else if (tag.equals("name")) {
                        formName = XFormParser.getXMLText(child, true);
                        if (formName != null && formName.length() == 0) {
                            formName = null;
                        }
                    } else if (tag.equals("version")) {
                        version = XFormParser.getXMLText(child, true);
                        if (version != null && version.length() == 0) {
                            version = null;
                        }
                    } else if (tag.equals("majorMinorVersion")) {
                        majorMinorVersion = XFormParser.getXMLText(child, true);
                        if (majorMinorVersion != null && majorMinorVersion.length() == 0) {
                            majorMinorVersion = null;
                        }
                    } else if (tag.equals("descriptionText")) {
                        description = XFormParser.getXMLText(child, true);
                        if (description != null && description.length() == 0) {
                            description = null;
                        }
                    } else if (tag.equals("downloadUrl")) {
                        downloadUrl = XFormParser.getXMLText(child, true);
                        if (downloadUrl != null && downloadUrl.length() == 0) {
                            downloadUrl = null;
                        }
                    } else if (tag.equals("manifestUrl")) {
                        manifestUrl = XFormParser.getXMLText(child, true);
                        if (manifestUrl != null && manifestUrl.length() == 0) {
                            manifestUrl = null;
                        }
                    }
                }
                if (formId == null || downloadUrl == null || formName == null) {
                    Log.d(t, "Parsing OpenRosa reply -- Forms list entry " + Integer.toString(i)
                            + " is missing one or more tags: formId, name, or downloadUrl");
                    formList.clear();
                    throw new ParsingException(BAD_OPENROSA_FORMLIST);
                }
                String versionString = null;
                if (version != null && version.length() != 0) {
                    versionString = version;
                } else if (majorMinorVersion != null && majorMinorVersion.length() != 0) {
                    int idx = majorMinorVersion.indexOf(".");
                    if (idx == -1) {
                        versionString = majorMinorVersion;
                    } else {
                        versionString = majorMinorVersion.substring(0, idx);
                    }
                }

                try {
                    if (versionString != null) {
                        // verify that  the version string is a long integer value...
                        Long.parseLong(versionString);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(t, "Parsing OpenRosa reply -- Forms list entry " + Integer.toString(i)
                            + " has an invalid version string: " + versionString);
                    formList.clear();
                    throw new ParsingException(BAD_OPENROSA_FORMLIST);
                }
                formList.add(new RemoteFormDefinition(formName, formId, versionString,
                        downloadUrl, manifestUrl));
            }
        } else {
            // Aggregate 0.9.x mode...
            // populate HashMap with form names and urls
            Element formsElement = formListDoc.getRootElement();
            int formsCount = formsElement.getChildCount();
            for (int i = 0; i < formsCount; ++i) {
                if (formsElement.getType(i) != Element.ELEMENT) {
                    // whitespace
                    continue;
                }
                Element child = formsElement.getElement(i);
                String tag = child.getName();
                if (tag.equalsIgnoreCase("form")) {
                    String formName = XFormParser.getXMLText(child, true);
                    if (formName != null && formName.length() == 0) {
                        formName = null;
                    }
                    String downloadUrl = child.getAttributeValue(null, "url");
                    downloadUrl = downloadUrl.trim();
                    if (downloadUrl != null && downloadUrl.length() == 0) {
                        downloadUrl = null;
                    }
                    if (downloadUrl == null || formName == null) {
                        Log.d(t, "Parsing OpenRosa reply -- Forms list entry " + Integer.toString(i)
                                + " is missing form name or url attribute");
                        formList.clear();
                        throw new ParsingException(BAD_LEGACY_FORMLIST);
                    }
                    // Since this is ODK Aggregate 0.9.8 or higher, we know that the
                    // formId is
                    // given as a parameter of the URL...
                    String formId = null;
                    try {
                        URL url = new URL(downloadUrl);
                        String qs = url.getQuery();
                        if (qs.startsWith(ODK_ID_PARAMETER_EQUALS)) {
                            formId = qs.substring(ODK_ID_PARAMETER_EQUALS.length());
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    if (formId == null) {
                        throw new ParsingException(
                                "Unable to extract formId from download URL of legacy 0.9.8 server");
                    }
                    formList.add(new RemoteFormDefinition(formName, formId, null, downloadUrl, null));
                }
            }
        }
        return formList;
    }

}
