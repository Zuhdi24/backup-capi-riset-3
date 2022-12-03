package org.odk.collect.android.pkl.object;

/**
 * Created by user on 2/3/2017.
 */
public class PopUpTest {

    private String text;
    private int resource;

    public PopUpTest(String text, int resource) {
        this.text = text;
        this.resource = resource;
    }

    public int getResource() {
        return resource;
    }

    public String getText() {
        return text;
    }
}
