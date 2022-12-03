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

import org.javarosa.core.model.instance.TreeElement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.PrivateKey;


public class BriefcaseFormDefinition implements IFormDefinition {
    private final File formFolder;
    private final File revisedFormFile;
    private boolean needsMediaUpdate = false;
    private JavaRosaParserWrapper formDefn;
    private PrivateKey privateKey = null;

    private static final String readFile(File formDefinitionFile) throws BadFormDefinition {
        StringBuilder xmlBuilder = new StringBuilder();
        BufferedReader rdr = null;
        try {
            rdr = new BufferedReader(new InputStreamReader(new FileInputStream(formDefinitionFile),
                    "UTF-8"));
            String line = rdr.readLine();
            while (line != null) {
                xmlBuilder.append(line);
                line = rdr.readLine();
            }
        } catch (FileNotFoundException e) {
            throw new BadFormDefinition("Form not found");
        } catch (IOException e) {
            throw new BadFormDefinition("Unable to read form");
        } finally {
            if (rdr != null) {
                try {
                    rdr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String inputXml = xmlBuilder.toString();
        Log.d("Briefcase", inputXml);
        return inputXml;
    }

    public boolean needsMediaUpdate() {
        return needsMediaUpdate;
    }

    public void clearMediaUpdate() {
        needsMediaUpdate = false;
    }


    private BriefcaseFormDefinition(File briefcaseFormDirectory, JavaRosaParserWrapper formDefn,
                                    File revisedFormFile, boolean needsMediaUpdate) {
        this.needsMediaUpdate = needsMediaUpdate;
        this.formDefn = formDefn;
        this.revisedFormFile = revisedFormFile;
        this.formFolder = briefcaseFormDirectory;
    }

    public BriefcaseFormDefinition(File briefcaseFormDirectory, File formFile)
            throws BadFormDefinition {
        formFolder = briefcaseFormDirectory;
        needsMediaUpdate = false;
        if (!formFile.exists()) {
            throw new BadFormDefinition("Form directory does not contain form");
        }
        File revised = new File(formFile.getParentFile(), formFile.getName() + ".revised");

        try {
            if (revised.exists()) {
                revisedFormFile = revised;
                formDefn = new JavaRosaParserWrapper(revisedFormFile, readFile(revisedFormFile));
            } else {
                revisedFormFile = null;
                formDefn = new JavaRosaParserWrapper(formFile, readFile(formFile));
            }
        } catch (ODKIncompleteSubmissionData e) {
            Log.d("Briefcasepa bad", "" + e);
            throw new BadFormDefinition(e, e.getReason());
        }

    }

    @Override
    public String toString() {
        return getFormName();
    }

    public File getFormDirectory() {
        return formFolder;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opendatakit.briefcase.model.IFormDefinition#getFormName()
     */
    @Override
    public String getFormName() {
        return formDefn.getFormName();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opendatakit.briefcase.model.IFormDefinition#getFormId()
     */
    @Override
    public String getFormId() {
        return formDefn.getSubmissionElementDefn().formId;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opendatakit.briefcase.model.IFormDefinition#getVersionString()
     */
    @Override
    public String getVersionString() {
        return formDefn.getSubmissionElementDefn().versionString;
    }

    public File getFormDefinitionFile() {
        if (revisedFormFile != null) {
            return revisedFormFile;
        } else {
            return formDefn.getFormDefinitionFile();
        }
    }

    public boolean isInvalidFormXmlns() {
        return formDefn.isInvalidFormXmlns();
    }

    public String getSubmissionKey(String uri) {
        return formDefn.getSubmissionKey(uri);
    }

    public boolean isFieldEncryptedForm() {
        return formDefn.isFieldEncryptedForm();
    }

    public boolean isFileEncryptedForm() {
        return formDefn.isFileEncryptedForm();
    }

    public TreeElement getSubmissionElement() {
        return formDefn.getSubmissionElement();
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    @Override
    public LocationType getFormLocation() {
        return LocationType.LOCAL;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof BriefcaseFormDefinition) {
            BriefcaseFormDefinition lf = (BriefcaseFormDefinition) obj;

            String id = getFormId();
            String versionString = getVersionString();

            return (id.equals(lf.getFormId()) && ((versionString == null) ? (lf.getVersionString() == null)
                    : versionString.equals(lf.getVersionString())));
        }

        return false;
    }

    @Override
    public int hashCode() {
        String id = getFormId();
        String versionString = getVersionString();

        return id.hashCode() + 3 * (versionString == null ? -123121 : versionString.hashCode());
    }
}
