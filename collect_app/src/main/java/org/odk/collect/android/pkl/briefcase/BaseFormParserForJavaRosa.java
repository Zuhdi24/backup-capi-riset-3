/*
 * Copyright (C) 2009 Google Inc.
 * Copyright (C) 2010 University of Washington.
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

import org.javarosa.core.model.DataBinding;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.IDataReference;
import org.javarosa.core.model.SubmissionProfile;
import org.javarosa.core.model.instance.AbstractTreeElement;
import org.javarosa.core.model.instance.FormInstance;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.core.model.instance.TreeReference;
import org.javarosa.core.services.PrototypeManager;
import org.javarosa.model.xform.XFormsModule;
import org.javarosa.xform.parse.XFormParser;
import org.javarosa.xform.util.XFormUtils;
import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.odk.collect.android.pkl.briefcase.ODKIncompleteSubmissionData.Reason;
import org.odk.collect.android.pkl.briefcase.utils.WebUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Parses an XML definition of an XForm based on java rosa types
 *
 * @author wbrunette@gmail.com
 * @author mitchellsundt@gmail.com
 * @author chrislrobert@gmail.com
 */
public class BaseFormParserForJavaRosa {

    private static final String LEADING_QUESTION_XML_PATTERN = "^[^<]*<\\s*\\?\\s*xml.*";
    private static final String t = "JavaRosaBaseFormParser";

    /**
     * Classes needed to serialize objects. Need to put anything from JR in here.
     */
    public final static String[] SERIALIABLE_CLASSES = {
            "org.javarosa.core.services.locale.ResourceFileDataSource", // JavaRosaCoreModule
            "org.javarosa.core.services.locale.TableLocaleSource", // JavaRosaCoreModule
            "org.javarosa.core.model.FormDef",
            "org.javarosa.core.model.SubmissionProfile", // CoreModelModule
            "org.javarosa.core.model.QuestionDef", // CoreModelModule
            "org.javarosa.core.model.GroupDef", // CoreModelModule
            "org.javarosa.core.model.instance.FormInstance", // CoreModelModule
            "org.javarosa.core.model.data.BooleanData", // CoreModelModule
            "org.javarosa.core.model.data.DateData", // CoreModelModule
            "org.javarosa.core.model.data.DateTimeData", // CoreModelModule
            "org.javarosa.core.model.data.DecimalData", // CoreModelModule
            "org.javarosa.core.model.data.GeoPointData", // CoreModelModule
            "org.javarosa.core.model.data.GeoShapeData", // CoreModelModule
            "org.javarosa.core.model.data.GeoTraceData", // CoreModelModule
            "org.javarosa.core.model.data.IntegerData", // CoreModelModule
            "org.javarosa.core.model.data.LongData", // CoreModelModule
            "org.javarosa.core.model.data.MultiPointerAnswerData", // CoreModelModule
            "org.javarosa.core.model.data.PointerAnswerData", // CoreModelModule
            "org.javarosa.core.model.data.SelectMultiData", // CoreModelModule
            "org.javarosa.core.model.data.SelectOneData", // CoreModelModule
            "org.javarosa.core.model.data.StringData", // CoreModelModule
            "org.javarosa.core.model.data.TimeData", // CoreModelModule
            "org.javarosa.core.model.data.UncastData", // CoreModelModule
            "org.javarosa.core.model.data.helper.BasicDataPointer", // CoreModelModule
            "org.javarosa.core.model.Action", // CoreModelModule
            "org.javarosa.core.model.actions.SetValueAction" //CoreModelModule
    };

    private static boolean isJavaRosaInitialized = false;

    /**
     * The JR implementation here does not look thread-safe or
     * like something to be invoked more than once.
     * Moving it within a critical section and a do-once guard.
     */
    private static final void initializeJavaRosa() {
        synchronized (t) {
            if (!isJavaRosaInitialized) {
                // need a list of classes that formdef uses
                // unfortunately, the JR registerModule() functions do more than this.
                // register just the classes that would have been registered by:
                // new JavaRosaCoreModule().registerModule();
                // new CoreModelModule().registerModule();
                // replace with direct call to PrototypeManager
                PrototypeManager.registerPrototypes(SERIALIABLE_CLASSES);
                new XFormsModule().registerModule();
                isJavaRosaInitialized = true;
            }
        }
    }

    private static final String BASE64_ENCRYPTED_FIELD_KEY = "base64EncryptedFieldKey";
    private static final String BASE64_RSA_PUBLIC_KEY = "base64RsaPublicKey";

    // result from comparing two XForms
    public enum DifferenceResult {
        XFORMS_IDENTICAL, // instance and body are identical
        XFORMS_SHARE_INSTANCE, // instances (including binding) identical; body
        // differs
        XFORMS_SHARE_SCHEMA, // instances differ, but share common database schema
        XFORMS_DIFFERENT, // instances differ significantly enough to affect
        // database schema
        XFORMS_MISSING_VERSION, XFORMS_EARLIER_VERSION
    }

    // bind attributes that CAN change without affecting database structure
    // Note: must specify these entirely in lowercase
    private static final List<String> ChangeableBindAttributes;

    // core instance def. attrs. that CANNOT change w/o affecting db structure
    // Note: must specify these entirely in lowercase
    private static final List<String> NonchangeableInstanceAttributes;

    static {
        ChangeableBindAttributes = Arrays.asList("relevant", "constraint", "readonly", "required", "calculate",
                XFormParser.NAMESPACE_JAVAROSA.toLowerCase() + ":constraintmsg",
                XFormParser.NAMESPACE_JAVAROSA.toLowerCase() + ":preload",
                XFormParser.NAMESPACE_JAVAROSA.toLowerCase() + ":preloadparams",
                "appearance");

        NonchangeableInstanceAttributes = Arrays.asList("id");
    }

    // nodeset attribute name, in <bind> elements
    private static final String NODESET_ATTR = "nodeset";

    // type attribute name, in <bind> elements
    private static final String TYPE_ATTR = "type";

    private static final String ENCRYPTED_FORM_DEFINITION = "<?xml version=\"1.0\"?>"
            + "<h:html xmlns=\"http://www.w3.org/2002/xforms\" xmlns:h=\"http://www.w3.org/1999/xhtml\" xmlns:ev=\"http://www.w3.org/2001/xml-events\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:odk=\""
            + ParserConsts.NAMESPACE_ODK
            + "\" xmlns:jr=\"http://openrosa.org/javarosa\">"
            + "<h:head>"
            + "<h:title>Encrypted Form</h:title>"
            + "<model>"
            + "<instance>"
            + "<data id=\"encrypted\" xmlns=\"http://www.opendatakit.org/xforms/encrypted\" xmlns:orx=\"http://openrosa.org/xforms\">"
            + "<base64EncryptedKey/>"
            + "<orx:meta>"
            + "<orx:instanceID/>"
            + "</orx:meta>"
            + "<media>"
            + "<file/>"
            + "</media>"
            + "<encryptedXmlFile/>"
            + "<base64EncryptedElementSignature/>"
            + "</data>"
            + "</instance>"
            + "<bind nodeset=\"/data/base64EncryptedKey\" type=\"string\" odk:length=\"2048\" />"
            + "<bind nodeset=\"/data/meta/instanceID\" type=\"string\"/>"
            + "<bind nodeset=\"/data/media/file\" type=\"binary\"/>"
            + "<bind nodeset=\"/data/encryptedXmlFile\" type=\"binary\"/>"
            + "<bind nodeset=\"/data/base64EncryptedElementSignature\" type=\"string\" odk:length=\"2048\" />"
            + "</model>"
            + "</h:head>"
            + "<h:body>"
            + "<input ref=\"base64EncryptedKey\"><label>Encrypted Symmetric Key</label></input>"
            + "<input ref=\"meta/instanceID\"><label>InstanceID</label></input>"
            + "<repeat nodeset=\"/data/media\">"
            + "<upload ref=\"file\" mediatype=\"image/*\"><label>media file</label></upload>"
            + "</repeat>"
            + "<upload ref=\"encryptedXmlFile\" mediatype=\"image/*\"><label>submission</label></upload>"
            + "<input ref=\"base64EncryptedElementSignature\"><label>Encrypted Element Signature</label></input>"
            + "</h:body>" + "</h:html>";

    private static final String ODK_TIMESTAMP_COMMENT = "<!-- ODK Aggregate upload time: ";

    private static int xmlInsertLocation(String xml) {
        int idx = xml.indexOf(">");
        if (idx == -1)
            return -1;
        String snip = xml.substring(0, idx).toLowerCase();
        if (snip.matches(LEADING_QUESTION_XML_PATTERN)) {
            // the file started with a <?xml...?> tag -- get the next(<html>) tag.
            idx = xml.indexOf(">", idx + 1); // <html
            if (idx == -1)
                return -1;
        }
        idx = xml.indexOf(">", idx + 1);
        if (idx == -1)
            return -1;
        ++idx;
        return idx;
    }

    public static String xmlWithoutTimestampComment(String xml) {
        int idx = xmlInsertLocation(xml);

        if (xml.startsWith(ODK_TIMESTAMP_COMMENT, idx)) {
            int endIdx = xml.indexOf(">", idx);
            if (endIdx == -1)
                return xml;
            ++endIdx;
            return xml.substring(0, idx) + xml.substring(endIdx);
        } else {
            return xml;
        }
    }

    public static Date xmlTimestamp(String xml) {
        int idx = xmlInsertLocation(xml);

        if (xml.startsWith(ODK_TIMESTAMP_COMMENT, idx)) {
            // find space after the IS8601 timestamp
            int endIdx = xml.indexOf(" ", idx + ODK_TIMESTAMP_COMMENT.length());
            if (endIdx == -1)
                return new Date();
            ++endIdx;
            String timestamp = xml.substring(idx + ODK_TIMESTAMP_COMMENT.length(), endIdx);
            Date d = WebUtils.parseDate(timestamp);
            if (d != null) {
                return d;
            } else {
                return new Date();
            }
        } else {
            return new Date();
        }
    }

    public static String xmlWithTimestampComment(String xmlWithoutTimestampComment, String serverUrl) {
        int idx = xmlInsertLocation(xmlWithoutTimestampComment);

        return xmlWithoutTimestampComment.substring(0, idx) + ODK_TIMESTAMP_COMMENT
                + WebUtils.iso8601Date(new Date()) + " on " + serverUrl + " -->"
                + xmlWithoutTimestampComment.substring(idx);
    }

    private static class XFormParserWithBindEnhancements extends XFormParser {
        @SuppressWarnings("unused")
        private Document xmldoc;
        private BaseFormParserForJavaRosa parser;

        public XFormParserWithBindEnhancements(BaseFormParserForJavaRosa parser, Document form) {
            super(form);
            this.xmldoc = form;
            this.parser = parser;
        }

        protected void parseBind(Element e) {
            // remember raw bindings in case we want to compare parsed XForms later
            parser.bindElements.add(copyBindingElement(e));
            List<String> usedAtts = new ArrayList<String>();

            DataBinding binding = processStandardBindAttributes(usedAtts, e);

            String value = e.getAttributeValue(ParserConsts.NAMESPACE_ODK, "length");
            if (value != null) {
                e.setAttribute(ParserConsts.NAMESPACE_ODK, "length", null);
            }

            Log.d(t, "Calling handle found value " + ((value == null) ? "null" : value));

            if (value != null) {
                Integer iValue = Integer.valueOf(value);
                parser.setNodesetStringLength(e.getAttributeValue(null, "nodeset"), iValue);
            }

            // print unused attribute warning message for parent element
            if (XFormUtils.showUnusedAttributeWarning(e, usedAtts)) {
                System.out.println(XFormUtils.unusedAttWarning(e, usedAtts));
            }

            addBinding(binding);
        }
    }

    private static synchronized final XFormParserWithBindEnhancements parseFormDefinition(String xml,
                                                                                          BaseFormParserForJavaRosa parser) throws ODKIncompleteSubmissionData {

        StringReader isr = null;
        try {
            isr = new StringReader(xml);
            Document doc = XFormParser.getXMLDocument(isr);
            return new XFormParserWithBindEnhancements(parser, doc);
        } catch (Exception e) {
            throw new ODKIncompleteSubmissionData(e, ODKIncompleteSubmissionData.Reason.BAD_JR_PARSE);
        } finally {
            isr.close();
        }
    }

    /**
     * The ODK Id that uniquely identifies the form
     */
    protected final FormDef rootJavaRosaFormDef;
    protected final XFormParameters rootElementDefn;
    protected final TreeElement trueSubmissionElement;
    protected final TreeElement submissionElement;
    protected final XFormParameters submissionElementDefn;
    protected final String base64RsaPublicKey;
    protected final boolean isFileEncryptedForm;
    protected final boolean isNotUploadableForm;
    protected final boolean isInvalidFormXmlns; // legacy 0.9.8 form
    protected final String title;
    protected final boolean isFieldEncryptedForm;
    protected final String base64EncryptedFieldRsaPublicKey;

    /**
     * The XForm definition in XML
     */
    protected final String xml;

    // extracted from XForm during parsing
    private final Map<String, Integer> stringLengths = new HashMap<String, Integer>();
    // original bindings from parse-time for later comparison
    private final List<Element> bindElements = new ArrayList<Element>();

    private void setNodesetStringLength(String nodeset, Integer length) {
        stringLengths.put(nodeset, length);
    }

    protected Integer getNodesetStringLength(AbstractTreeElement<?> e) {
        List<String> path = new ArrayList<String>();
        while (e != null && e.getName() != null) {
            path.add(e.getName());
            e = e.getParent();
        }
        Collections.reverse(path);

        StringBuilder b = new StringBuilder();
        for (String s : path) {
            b.append("/");
            b.append(s);
        }

        String nodeset = b.toString();
        Integer len = stringLengths.get(nodeset);
        return len;
    }

    /**
     * Extract the form id, version and uiVersion.
     *
     * @param rootElement        - the tree element that is the root submission.
     * @param defaultFormIdValue - used if no "id" attribute found. This should already be
     *                           slash-substituted.
     * @return
     */
    private XFormParameters extractFormParameters(TreeElement rootElement, String defaultFormIdValue) {

        String formIdValue = null;
        String versionString = rootElement.getAttributeValue(null, "version");

        // search for the "id" attribute
        for (int i = 0; i < rootElement.getAttributeCount(); i++) {
            String name = rootElement.getAttributeName(i);
            if (name.equals(ParserConsts.FORM_ID_ATTRIBUTE_NAME)) {
                formIdValue = rootElement.getAttributeValue(i);
                formIdValue = formIdValue.replaceAll(ParserConsts.FORWARD_SLASH,
                        ParserConsts.FORWARD_SLASH_SUBSTITUTION);
                break;
            }
        }

        return new XFormParameters((formIdValue == null) ? defaultFormIdValue : formIdValue,
                versionString);
    }

    /**
     * Determine whether or not a field is encrypted. Field-level encryption
     * plumbing.
     *
     * @param element
     * @return
     */
    public final static boolean isEncryptedField(TreeElement element) {
        String v = getBindAttribute(element, "encrypted");
        return (v != null && ("true".equalsIgnoreCase(v) || "true()".equalsIgnoreCase(v)));
    }

    /**
     * Field-level encryption requires an extended Javarosa library that expose an
     * "encrypted" bind attribute that identifies the fields that are to be
     * encrypted and a BASE64_RSA_PUBLIC_KEY bind attribute on the
     * BASE64_ENCRYPTED_FIELD_KEY field in the form.
     * <p>
     * Requires an experimental custom Javarosa library.
     * <p>
     * Not enabled in the main tree.
     *
     * @param element
     * @param name
     * @return
     */
    private final static String getBindAttribute(TreeElement element, String name) {
        return null;
        // TODO: uncomment this if the experimental library is used...
        // return element.getBindAttributeValue(null, name);
    }

    /**
     * Traverse the submission looking for the first matching tag in depth-first
     * order.
     *
     * @param parent
     * @param name
     * @return
     */
    private TreeElement findDepthFirst(TreeElement parent, String name) {
        int len = parent.getNumChildren();
        for (int i = 0; i < len; ++i) {
            TreeElement e = parent.getChildAt(i);
            if (name.equals(e.getName())) {
                return e;
            } else if (e.getNumChildren() != 0) {
                TreeElement v = findDepthFirst(e, name);
                if (v != null)
                    return v;
            }
        }
        return null;
    }

    /**
     * Field-level encryption support. Forms with field-level encryption must have
     * a meta block with a BASE64_ENCRYPTED_FIELD_KEY entry.
     *
     * @return base64EncryptedFieldRsaPublicKey string if field encryption is
     * present.
     */
    private String extractBase64FieldEncryptionKey(TreeElement submissionElement) {
        TreeElement meta = findDepthFirst(submissionElement, "meta");
        if (meta != null) {
            List<TreeElement> l;

            // Save the base64 RSA-Encrypted symmetric encryption key
            // we are using for field encryption.
            // Do not encrypt the form if we can't save this encrypted key...
            l = meta.getChildrenWithName(BASE64_ENCRYPTED_FIELD_KEY);
            if (l.size() == 1) {
                TreeElement ek = l.get(0);
                String base64EncryptedFieldRsaPublicKey = getBindAttribute(ek, BASE64_RSA_PUBLIC_KEY);
                if (base64EncryptedFieldRsaPublicKey != null
                        && base64EncryptedFieldRsaPublicKey.trim().length() == 0) {
                    base64EncryptedFieldRsaPublicKey = null;
                }
                return base64EncryptedFieldRsaPublicKey;
            }
        }
        return null;
    }

    /**
     * Alternate constructor for internally comparing whether two form definitions
     * share the same data elements and storage models. This just parses the
     * supplied xml and does nothing else.
     *
     * @throws ODKIncompleteSubmissionData
     */
    protected BaseFormParserForJavaRosa(String existingXml, String existingTitle, boolean allowLegacy)
            throws ODKIncompleteSubmissionData {
        if (existingXml == null) {
            throw new ODKIncompleteSubmissionData(Reason.MISSING_XML);
        }

        xml = existingXml;

        initializeJavaRosa();

        XFormParserWithBindEnhancements xfp = parseFormDefinition(xml, this);
        try {
            rootJavaRosaFormDef = xfp.parse();
        } catch (Exception e) {
            throw new ODKIncompleteSubmissionData(
                    "Javarosa failed to construct a FormDef. Is this an XForm definition?", e,
                    Reason.BAD_JR_PARSE);
        }

        if (rootJavaRosaFormDef == null) {
            throw new ODKIncompleteSubmissionData(
                    "Javarosa failed to construct a FormDef.  Is this an XForm definition?",
                    Reason.BAD_JR_PARSE);
        }
        FormInstance dataModel = rootJavaRosaFormDef.getInstance();
        if (dataModel == null) {
            throw new ODKIncompleteSubmissionData(
                    "Javarosa failed to construct a FormInstance.  Is this an XForm definition?",
                    Reason.BAD_JR_PARSE);
        }
        TreeElement rootElement = dataModel.getRoot();

        boolean schemaMalformed = false;
        String schemaValue = dataModel.schema;
        if (schemaValue != null) {
            int idx = schemaValue.indexOf(":");
            if (idx != -1) {
                if (schemaValue.indexOf("/") < idx) {
                    // malformed...
                    schemaValue = allowLegacy ? schemaValue.replaceAll(ParserConsts.FORWARD_SLASH,
                            ParserConsts.FORWARD_SLASH_SUBSTITUTION) : null;
                    schemaMalformed = true;
                } else {
                    // need to escape all slashes... for xpath processing...
                    schemaValue = schemaValue.replaceAll(ParserConsts.FORWARD_SLASH,
                            ParserConsts.FORWARD_SLASH_SUBSTITUTION);
                }
            } else {
                // malformed...
                schemaValue = allowLegacy ? schemaValue.replaceAll(ParserConsts.FORWARD_SLASH,
                        ParserConsts.FORWARD_SLASH_SUBSTITUTION) : null;
                schemaMalformed = true;
            }
        }
        try {
            rootElementDefn = extractFormParameters(rootElement, schemaValue);
        } catch (IllegalArgumentException e) {
            if (schemaMalformed) {
                throw new ODKIncompleteSubmissionData(
                        "xmlns attribute for the data model is not well-formed: '"
                                + dataModel.schema
                                + "' should be of the form xmlns=\"http://your.domain.org/formId\"\nConsider defining the formId using the 'id' attribute instead of the 'xmlns' attribute (id=\"formId\")",
                        Reason.ID_MALFORMED);
            } else {
                throw new ODKIncompleteSubmissionData(
                        "The data model does not have an id or xmlns attribute.  Add an id=\"your.domain.org:formId\" attribute to the top-level instance data element of your form.",
                        Reason.ID_MISSING);
            }
        }
        if (!allowLegacy && rootElementDefn.modelVersion != null
                && (rootElementDefn.modelVersion > Long.valueOf(Integer.MAX_VALUE))) {
            // for some reason, the datastore is not persisting Long values correctly?
            throw new ODKIncompleteSubmissionData(
                    "The version string must be an integer less than 2147483648", Reason.ID_MALFORMED);
        }
        isInvalidFormXmlns = schemaMalformed;

        boolean isNotUploadableForm = false;
        // Determine the information about the submission...
        SubmissionProfile p = rootJavaRosaFormDef.getSubmissionProfile();
        if (p == null || p.getRef() == null) {
            trueSubmissionElement = rootElement;
            submissionElementDefn = rootElementDefn;
        } else {
            TreeElement rref = rootJavaRosaFormDef.getInstance().resolveReference(p.getRef());
            trueSubmissionElement = (rref == null) ? rootElement : rref;
            if (rref == null) {
                submissionElementDefn = rootElementDefn;
            } else {
                try {
                    submissionElementDefn = extractFormParameters(trueSubmissionElement, null);
                } catch (Exception e) {
                    throw new ODKIncompleteSubmissionData(
                            "The non-root submission element in the data model does not have an id attribute.  Add an id=\"your.domain.org:formId\" attribute to the submission element of your form.",
                            Reason.ID_MISSING);
                }
            }
        }

        if (p != null) {
            String altUrl = p.getAction();
            isNotUploadableForm = (altUrl == null || !altUrl.startsWith("http") || p.getMethod() == null || !p
                    .getMethod().equals("form-data-post"));
        }

        this.isNotUploadableForm = isNotUploadableForm;

        if (isNotUploadableForm) {
            Log.d(t, "Form "
                    + submissionElementDefn.formId
                    + " is not uploadable (submission method is not form-data-post or does not have an http: or https: url. ");
        }

        // insist that the submission element and root element have the same
        // formId, modelVersion and uiVersion.
        if (!submissionElementDefn.equals(rootElementDefn)) {
            throw new ODKIncompleteSubmissionData(
                    "submission element and root element differ in their values for: formId or version.",
                    Reason.MISMATCHED_SUBMISSION_ELEMENT);
        }

        if (p != null) {
            base64RsaPublicKey = p.getAttribute(BASE64_RSA_PUBLIC_KEY);
        } else {
            base64RsaPublicKey = null;
        }

        // the form def to store is the root form def unless we have an encrypted
        // form...
        FormDef formDef = rootJavaRosaFormDef;

        // now see if we are encrypted -- if so, fake the submission element to
        // be
        // the parsing of the ENCRYPTED_FORM_DEFINITION
        if (base64RsaPublicKey == null || base64RsaPublicKey.length() == 0) {
            // not encrypted...
            submissionElement = trueSubmissionElement;
            isFileEncryptedForm = false;
            base64EncryptedFieldRsaPublicKey = extractBase64FieldEncryptionKey(trueSubmissionElement);
            isFieldEncryptedForm = (base64EncryptedFieldRsaPublicKey != null);
        } else {
            isFileEncryptedForm = true;
            // encrypted -- use the encrypted form template (above) to define
            // the
            // storage for this form.
            XFormParserWithBindEnhancements exfp = parseFormDefinition(ENCRYPTED_FORM_DEFINITION, this);
            try {
                formDef = exfp.parse();
            } catch (IOException e) {
                throw new ODKIncompleteSubmissionData("Exception " + e.toString() + " during parsing!", Reason.BAD_JR_PARSE);
            }

            if (formDef == null) {
                throw new ODKIncompleteSubmissionData("Javarosa failed to construct Encrypted FormDef!",
                        Reason.BAD_JR_PARSE);
            }
            dataModel = formDef.getInstance();
            if (dataModel == null) {
                throw new ODKIncompleteSubmissionData(
                        "Javarosa failed to construct Encrypted FormInstance!", Reason.BAD_JR_PARSE);
            }
            submissionElement = dataModel.getRoot();
            base64EncryptedFieldRsaPublicKey = extractBase64FieldEncryptionKey(trueSubmissionElement);
            isFieldEncryptedForm = (base64EncryptedFieldRsaPublicKey != null);
        }

        // obtain form title either from the xform itself or from user entry
        String formTitle = rootJavaRosaFormDef.getTitle();
        if (formTitle == null) {
            if (existingTitle == null) {
                throw new ODKIncompleteSubmissionData(Reason.TITLE_MISSING);
            } else {
                formTitle = existingTitle;
            }
        }
        // clean illegal characters from title
        title = formTitle.replace(BasicConsts.FORWARDSLASH, BasicConsts.EMPTY_STRING);
    }

    @SuppressWarnings("unused")
    private void printTreeElementInfo(TreeElement treeElement) {
        System.out.println("processing te: " + treeElement.getName() + " type: " + treeElement.getDataType()
                + " repeatable: " + treeElement.isRepeatable());
    }

    public String getTreeElementPath(AbstractTreeElement<?> e) {
        if (e == null)
            return null;
        String s = getTreeElementPath(e.getParent());
        if (s == null)
            return e.getName();
        return s + "/" + e.getName();
    }

    /**
     * Get all recorded bindings for a given TreeElement
     *
     * @param treeElement
     * @return
     */
    private List<Element> getBindingsForTreeElement(TreeElement treeElement) {
        List<Element> l = new ArrayList<Element>();
        String nodeset = "/" + getTreeElementPath(treeElement);

        for (int i = 0; i < this.bindElements.size(); i++) {
            Element element = this.bindElements.get(i);
            if (element.getAttributeValue("", NODESET_ATTR).equalsIgnoreCase(nodeset)) {
                l.add(element);
            }
        }

        return (l);
    }

    public String getFormId() {
        return rootElementDefn.formId;
    }

    /**
     * Compare two XML files to assess their level of structural difference (if
     * any).
     *
     * @param incomingParser -- parsed version of incoming form
     * @param existingXml    -- the existing Xml for this form
     * @return XFORMS_SHARE_INSTANCE when bodies differ but instances and bindings
     * are identical; XFORMS_SHARE_SCHEMA when bodies and/or bindings
     * differ, but database structure remains unchanged; XFORMS_DIFFERENT
     * when forms are different enough to affect database structure and/or
     * encryption.
     * @throws ODKIncompleteSubmissionData
     */
    public static DifferenceResult compareXml(BaseFormParserForJavaRosa incomingParser,
                                              String existingXml, String existingTitle, boolean isWithinUpdateWindow)
            throws ODKIncompleteSubmissionData {
        if (incomingParser == null || existingXml == null) {
            throw new ODKIncompleteSubmissionData(Reason.MISSING_XML);
        }

        // generally only the case within Briefcase
        if (incomingParser.xml.equals(existingXml)) {
            return DifferenceResult.XFORMS_IDENTICAL;
        }

        // parse XML
        FormDef formDef1, formDef2;
        BaseFormParserForJavaRosa existingParser = new BaseFormParserForJavaRosa(existingXml,
                existingTitle, true);
        formDef1 = incomingParser.rootJavaRosaFormDef;
        formDef2 = existingParser.rootJavaRosaFormDef;
        if (formDef1 == null || formDef2 == null) {
            throw new ODKIncompleteSubmissionData(
                    "Javarosa failed to construct a FormDef.  Is this an XForm definition?",
                    Reason.BAD_JR_PARSE);
        }

        // check that the version is advancing from the earlier
        // form upload. The comparison is string-based, not
        // numeric-based (OpenRosa compliance). The recommended
        // version format is: yyyymmddnn e.g., 2012060100
        String ivs = incomingParser.rootElementDefn.versionString;
        if (ivs == null) {
            // if we are changing the file, the new file must have a version string
            return DifferenceResult.XFORMS_MISSING_VERSION;
        }

        String evs = existingParser.rootElementDefn.versionString;
        boolean modelVersionSame = (incomingParser.rootElementDefn.modelVersion == null) ? (existingParser.rootElementDefn.modelVersion == null)
                : incomingParser.rootElementDefn.modelVersion
                .equals(existingParser.rootElementDefn.modelVersion);

        boolean isEarlierVersion = false;
        if (!(evs == null || (modelVersionSame && ivs.length() > evs.length()) || (!modelVersionSame && ivs
                .compareTo(evs) > 0))) {
            // disallow updates if none of the following applies:
            // (1) if the existing form does not have a version (the new one does).
            // (2) if the existing form and new form have the same model version
            // and the new form has more leading zeros.
            // (3) if the existing form and new form have different model versions
            // and the new version string is lexically greater than the old one.
            isEarlierVersion = true;
            return DifferenceResult.XFORMS_EARLIER_VERSION;
        }

    /*
     * Changes in encryption (either on or off, or change in key) are a major
     * change. We could allow the public key to be revised, but most users won't
     * understand that this is possible or know how to do it.
     *
     * Ignore whether a submission profile is present or absent provided it does
     * not affect encryption or change the portion of the form being returned.
     */
        SubmissionProfile subProfile1 = formDef1.getSubmissionProfile();
        SubmissionProfile subProfile2 = formDef2.getSubmissionProfile();
        if (subProfile1 != null && subProfile2 != null) {
            // we have two profiles -- check that any encryption key matches...
            String publicKey1 = subProfile1.getAttribute(BASE64_RSA_PUBLIC_KEY);
            String publicKey2 = subProfile2.getAttribute(BASE64_RSA_PUBLIC_KEY);
            if (publicKey1 != null && publicKey2 != null) {
                // both have encryption
                if (!publicKey1.equals(publicKey2)) {
                    // keys differ
                    return (DifferenceResult.XFORMS_DIFFERENT);
                }
            } else if (publicKey1 != null || publicKey2 != null) {
                // one or the other has encryption (and the other doesn't)...
                return (DifferenceResult.XFORMS_DIFFERENT);
            }

            // get the TreeElement (e1, e2) that identifies the portion of the form
            // that will be submitted to Aggregate.
            IDataReference r;
            r = subProfile1.getRef();
            AbstractTreeElement<?> e1 = (r != null) ? formDef1.getInstance().resolveReference(r) : null;
            r = subProfile2.getRef();
            AbstractTreeElement<?> e2 = (r != null) ? formDef2.getInstance().resolveReference(r) : null;

            if (e1 != null && e2 != null) {
                // both return only a portion of the form.

                // Compare up each tree, verifying that all the tag names match.
                // Ignore all namespace differences (Aggregate ignores them)...
                while (e1 != null && e2 != null) {
                    if (!e1.getName().equals(e2.getName())) {
                        return (DifferenceResult.XFORMS_DIFFERENT);
                    }
                    e1 = e1.getParent();
                    e2 = e2.getParent();
                }

                if (e1 != null || e2 != null) {
                    // they should both terminate at the same time...
                    return (DifferenceResult.XFORMS_DIFFERENT);
                }
                // we may still have differences, but if the overall form
                // is identical, we are golden...
            } else if (e1 != null || e2 != null) {
                // one returns a portion of the form and the other doesn't
                return (DifferenceResult.XFORMS_DIFFERENT);
            }

        } else if (subProfile1 != null) {
            if (subProfile1.getAttribute(BASE64_RSA_PUBLIC_KEY) != null) {
                // xml1 does encryption, the other doesn't
                return (DifferenceResult.XFORMS_DIFFERENT);
            }
            IDataReference r = subProfile1.getRef();
            if (r != null && formDef1.getInstance().resolveReference(r) != null) {
                // xml1 returns a portion of the form, the other doesn't
                return (DifferenceResult.XFORMS_DIFFERENT);
            }
        } else if (subProfile2 != null) {
            if (subProfile2.getAttribute(BASE64_RSA_PUBLIC_KEY) != null) {
                // xml2 does encryption, the other doesn't
                return (DifferenceResult.XFORMS_DIFFERENT);
            }
            IDataReference r = subProfile2.getRef();
            if (r != null && formDef2.getInstance().resolveReference(r) != null) {
                // xml2 returns a portion of the form, the other doesn't
                return (DifferenceResult.XFORMS_DIFFERENT);
            }
        }

        // get data model to compare instances
        FormInstance dataModel1 = formDef1.getInstance();
        FormInstance dataModel2 = formDef2.getInstance();
        if (dataModel1 == null || dataModel2 == null) {
            throw new ODKIncompleteSubmissionData(
                    "Javarosa failed to construct a FormInstance.  Is this an XForm definition?",
                    Reason.BAD_JR_PARSE);
        }

        // return result of element-by-element instance/binding comparison
        DifferenceResult rc = compareTreeElements(dataModel1.getRoot(), incomingParser,
                dataModel2.getRoot(), existingParser);
        if (DifferenceResult.XFORMS_DIFFERENT == rc) {
            return rc;
        } else if (isEarlierVersion) {
            return DifferenceResult.XFORMS_EARLIER_VERSION;
        } else {
            return rc;
        }
    }

    /**
     * Compare two parsed TreeElements to assess their level of structural
     * difference (if any).
     *
     * @param treeElement1
     * @param treeElement2
     * @return XFORMS_SHARE_INSTANCE when bodies differ but instances and bindings
     * are identical; XFORMS_SHARE_SCHEMA when bodies and/or bindings
     * differ, but database structure remains unchanged; XFORMS_DIFFERENT
     * when forms are different enough to affect database structure and/or
     * encryption.
     */
    public static DifferenceResult compareTreeElements(TreeElement treeElement1,
                                                       BaseFormParserForJavaRosa parser1, TreeElement treeElement2, BaseFormParserForJavaRosa parser2) {
        boolean smalldiff = false, bigdiff = false;

        // compare names
        if (!treeElement1.getName().equals(treeElement2.getName())) {
            bigdiff = true;
        }

        // compare core instance attributes one-by-one (starting with those in
        // treeElement1)
        for (int i = 0; i < treeElement1.getAttributeCount(); i++) {
            String attributeNamespace = treeElement1.getAttributeNamespace(i);
            if (attributeNamespace != null && attributeNamespace.length() == 0) {
                attributeNamespace = null;
            }
            String attributeName = treeElement1.getAttributeName(i);
            String fullAttributeName = (attributeNamespace == null ? attributeName : attributeNamespace
                    + ":" + attributeName);

            // see if there's a difference in this attribute
            if (!treeElement1.getAttributeValue(i).equals(
                    treeElement2.getAttributeValue(attributeNamespace, attributeName))) {
                // flag differences as small or large based on list in
                // NonchangeableInstanceAttributes[]
                // here, changes are ALLOWED by default, unless to a listed attribute
                if (!NonchangeableInstanceAttributes.contains(
                        fullAttributeName.toLowerCase())) {
                    smalldiff = true;
                } else {
                    bigdiff = true;
                }
            }
        }
        // check core instance attributes only in treeElement2
        for (int i = 0; i < treeElement2.getAttributeCount(); i++) {
            String attributeNamespace = treeElement2.getAttributeNamespace(i);
            if (attributeNamespace != null && attributeNamespace.length() == 0) {
                attributeNamespace = null;
            }
            String attributeName = treeElement2.getAttributeName(i);
            String fullAttributeName = (attributeNamespace == null ? attributeName : attributeNamespace
                    + ":" + attributeName);

            // see if this is an attribute only in treeElement2
            if (treeElement1.getAttributeValue(attributeNamespace, attributeName) == null) {
                // flag differences as small or large based on list in
                // NonchangeableInstanceAttributes[]
                // here, changes are ALLOWED by default, unless to a listed attribute
                if (!NonchangeableInstanceAttributes.contains(
                        fullAttributeName.toLowerCase())) {
                    smalldiff = true;
                } else {
                    bigdiff = true;
                }
            }
        }

        // note attributes don't actually include bindings; thus, check raw bindings
        // also one-by-one (starting with bindings1)
        List<Element> bindings1 = parser1.getBindingsForTreeElement(treeElement1);
        List<Element> bindings2 = parser2.getBindingsForTreeElement(treeElement2);
        for (int i = 0; i < bindings1.size(); i++) {
            Element binding = bindings1.get(i);
            for (int j = 0; j < binding.getAttributeCount(); j++) {
                String attributeNamespace = binding.getAttributeNamespace(j);
                if (attributeNamespace != null && attributeNamespace.length() == 0) {
                    attributeNamespace = null;
                }
                String attributeName = binding.getAttributeName(j);
                String fullAttributeName = (attributeNamespace == null ? attributeName : attributeNamespace
                        + ":" + attributeName);

                if (!fullAttributeName.equalsIgnoreCase(NODESET_ATTR)) {
                    // see if there's a difference in this attribute
                    String value1 = binding.getAttributeValue(j);
                    String value2 = getBindingAttributeValue(bindings2, attributeNamespace, attributeName);
                    if (!value1.equals(value2)) {
                        if (fullAttributeName.toLowerCase().equals(TYPE_ATTR)
                                && value1 != null
                                && value2 != null
                                && ((value1.toLowerCase().equals("string") && value2.toLowerCase()
                                .equals("select1")) || (value1.toLowerCase().equals("select1") && value2
                                .toLowerCase().equals("string")))) {
                            // handle changes between string and select1 data types as special
                            // (allowable) case
                            smalldiff = true;
                        } else {
                            // flag differences as small or large based on list in
                            // ChangeableBindAttributes[]
                            // here, changes are NOT ALLOWED by default, unless to a listed
                            // attribute
                            if (ChangeableBindAttributes.contains(fullAttributeName.toLowerCase())) {
                                smalldiff = true;
                            } else {
                                bigdiff = true;
                            }
                        }
                    }
                }
            }
        }
        // check binding attributes only in bindings2
        for (int i = 0; i < bindings2.size(); i++) {
            Element binding = bindings2.get(i);
            for (int j = 0; j < binding.getAttributeCount(); j++) {
                String attributeNamespace = binding.getAttributeNamespace(j);
                if (attributeNamespace != null && attributeNamespace.length() == 0) {
                    attributeNamespace = null;
                }
                String attributeName = binding.getAttributeName(j);
                String fullAttributeName = (attributeNamespace == null ? attributeName : attributeNamespace
                        + ":" + attributeName);

                if (!fullAttributeName.equalsIgnoreCase(NODESET_ATTR)) {
                    // see if this is an attribute only in bindings2
                    if (getBindingAttributeValue(bindings1, attributeNamespace, attributeName) == null) {
                        // flag differences as small or large based on list in
                        // ChangeableBindAttributes[]
                        // here, changes are NOT ALLOWED by default, unless to a listed
                        // attribute
                        if (ChangeableBindAttributes.contains(fullAttributeName.toLowerCase())) {
                            smalldiff = true;
                        } else {
                            bigdiff = true;
                        }
                    }
                }
            }
        }

        // Issue 786 -- we need to handle repeat groups.
        // If we have a repeat without a jr:template="" attribute, then
        // that element appears as a index [0] repeat within the form
        // definition and as an INDEX_TEMPLATE element (it is copied).
        // Otherwise, if you have specified the jr:template attribute,
        // it appears only as an INDEX_TEMPLATE element.

        @SuppressWarnings("unused")
        int template1DropCount = 0;
        // get non-template entries for treeElement1
        List<TreeElement> element1ExcludingRepeatIndex0Children = new ArrayList<TreeElement>();

        for (int i = 0; i < treeElement1.getNumChildren(); i++) {
            TreeElement child = treeElement1.getChildAt(i);
            if (child.isRepeatable()) {
                if (child.getMult() != TreeReference.INDEX_TEMPLATE) {
                    template1DropCount++;
                    Log.d(t, "element1:dropping " + child.getName());
                    continue;
                }
                Log.d(t, "element1:retaining " + child.getName());
            }
            element1ExcludingRepeatIndex0Children.add(child);
        }

        @SuppressWarnings("unused")
        int template2DropCount = 0;
        // get non-template entries for treeElement2
        Map<String, TreeElement> element2ExcludingRepeatIndex0Children = new HashMap<String, TreeElement>();

        for (int i = 0; i < treeElement2.getNumChildren(); i++) {
            TreeElement child = treeElement2.getChildAt(i);
            if (child.isRepeatable()) {
                if (child.getMult() != TreeReference.INDEX_TEMPLATE) {
                    template2DropCount++;
                    Log.d(t, "element2:dropping " + child.getName());
                    continue;
                }
                Log.d(t, "element2:retaining " + child.getName());
            }
            if (element2ExcludingRepeatIndex0Children.get(child.getName()) != null) {
                // consider children not uniquely named as big differences
                bigdiff = true;
            }
            element2ExcludingRepeatIndex0Children.put(child.getName(), child);
        }

        // compare children
        if (element1ExcludingRepeatIndex0Children.size() != element2ExcludingRepeatIndex0Children
                .size()) {
            // consider differences in basic structure (e.g., number and grouping of
            // fields) as big
            bigdiff = true;
        } else {
            for (int i = 0; i < element1ExcludingRepeatIndex0Children.size(); i++) {
                TreeElement childElement1 = element1ExcludingRepeatIndex0Children.get(i);
                TreeElement childElement2 = element2ExcludingRepeatIndex0Children.get(childElement1
                        .getName());

                if (childElement2 != null) {
                    // recursively compare children...
                    switch (compareTreeElements(childElement1, parser1, childElement2, parser2)) {
                        case XFORMS_SHARE_SCHEMA:
                            smalldiff = true;
                            break;
                        case XFORMS_DIFFERENT:
                            bigdiff = true;
                            break;
                        default:
                            // no update for the other cases (IDENTICAL, EARLIER, MISSING, SHARE_INSTANCE)
                            break;
                    }
                } else {
                    // consider children not found as big differences
                    bigdiff = true;
                }
            }
        }

        // return appropriate value
        if (bigdiff) {
            return (DifferenceResult.XFORMS_DIFFERENT);
        } else if (smalldiff) {
            return (DifferenceResult.XFORMS_SHARE_SCHEMA);
        } else {
            return (DifferenceResult.XFORMS_SHARE_INSTANCE);
        }
    }

    // search list of recorded bindings for a particular attribute; return its
    // value
    private static String getBindingAttributeValue(List<Element> bindings,
                                                   String attributeNamespace, String attributeName) {
        String retval = null;

        for (int i = 0; i < bindings.size(); i++) {
            Element element = bindings.get(i);
            if ((retval = element.getAttributeValue(attributeNamespace, attributeName)) != null) {
                return (retval);
            }
        }
        return (retval);
    }

    // copy binding and associated attributes to a new binding element (to help
    // with maintaining list of original bindings)
    private static Element copyBindingElement(Element element) {
        Element retval = new Element();
        retval.createElement(element.getNamespace(), element.getName());
        for (int i = 0; i < element.getAttributeCount(); i++) {
            retval.setAttribute(element.getAttributeNamespace(i), element.getAttributeName(i),
                    element.getAttributeValue(i));
        }
        return (retval);
    }
}