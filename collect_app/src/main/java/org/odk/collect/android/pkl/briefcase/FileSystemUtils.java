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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class FileSystemUtils {
    private static final String t = "FileSystemUtils";

    public static final String BRIEFCASE_DIR = "ODK Briefcase Storage";
    static final String README_TXT = "readme.txt";
    static final String FORMS_DIR = "forms";

    // encryption support....
    static final String ASYMMETRIC_ALGORITHM = "RSA/NONE/OAEPWithSHA256AndMGF1Padding";
    static final String UTF_8 = "UTF-8";
    static final String ENCRYPTED_FILE_EXTENSION = ".enc";
    static final String MISSING_FILE_EXTENSION = ".missing";

    // Predicates to determine whether the folder is an ODK Device
    // ODK folder or underneath that folder.


    public static String asFilesystemSafeName(String formName) {
        return formName.replaceAll("[/\\\\:]", "").trim();
    }


    public static File getFormDefinitionFile(File formDirectory)
            throws FileSystemException {
        File formDefnFile = new File(formDirectory, formDirectory.getName() + ".xml");

        return formDefnFile;
    }


    public static boolean hasFormSubmissionDirectory(File formInstancesDir, String instanceID) {
        // create instance directory...
        String instanceDirName = asFilesystemSafeName(instanceID);
        File instanceDir = new File(formInstancesDir, instanceDirName);
        return instanceDir.exists();
    }


    public static File assertFormSubmissionDirectory(File formInstancesDir, String instanceID)
            throws FileSystemException {
        // create instance directory...
        String instanceDirName = asFilesystemSafeName(instanceID);
        File instanceDir = new File(formInstancesDir, instanceDirName);
        if (instanceDir.exists() && instanceDir.isDirectory()) {
            return instanceDir;
        }

        if (!instanceDir.mkdir()) {
            throw new FileSystemException("unable to create instance dir");
        }

        return instanceDir;
    }

    public static final String getMd5Hash(File file) {
        try {
            // CTS (6/15/2010) : stream file through digest instead of handing
            // it the
            // byte[]
            MessageDigest md = MessageDigest.getInstance("MD5");
            int chunkSize = 256;

            byte[] chunk = new byte[chunkSize];

            // Get the size of the file
            long lLength = file.length();

            if (lLength > Integer.MAX_VALUE) {
                Log.d(t, "File " + file.getName() + "is too large");
                return null;
            }

            int length = (int) lLength;

            InputStream is = null;
            is = new FileInputStream(file);

            int l = 0;
            for (l = 0; l + chunkSize < length; l += chunkSize) {
                is.read(chunk, 0, chunkSize);
                md.update(chunk, 0, chunkSize);
            }

            int remaining = length - l;
            if (remaining > 0) {
                is.read(chunk, 0, remaining);
                md.update(chunk, 0, remaining);
            }
            byte[] messageDigest = md.digest();

            BigInteger number = new BigInteger(1, messageDigest);
            String md5 = number.toString(16);
            while (md5.length() < 32)
                md5 = "0" + md5;
            is.close();
            return md5;

        } catch (NoSuchAlgorithmException e) {
            Log.d(t, "MD5 calculation failed: " + e.getMessage());
            return null;

        } catch (FileNotFoundException e) {
            Log.d(t, "No File: " + e.getMessage());
            return null;
        } catch (IOException e) {
            Log.d(t, "Problem reading from file: " + e.getMessage());
            return null;
        }

    }


}
