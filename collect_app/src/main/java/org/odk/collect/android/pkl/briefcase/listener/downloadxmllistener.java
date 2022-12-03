package org.odk.collect.android.pkl.briefcase.listener;

import org.odk.collect.android.pkl.briefcase.getxml.DocumentFetchResult;

/**
 * Created by Cloud Walker on 24/02/2016.
 */
public interface downloadxmllistener {
    void ondownloadxmlcomplete(boolean complete, DocumentFetchResult doc) throws Exception;
}
