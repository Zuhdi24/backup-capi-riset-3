/*
 * Copyright (C) 2011 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.listeners.AudioPlayListener;
import org.odk.collect.android.utilities.TextUtils;
import org.odk.collect.android.views.MediaLayout;

import java.util.ArrayList;
import java.util.List;

public abstract class QuestionWidget extends LinearLayout implements AudioPlayListener {

    @SuppressWarnings("unused")
    private final static String t = "QuestionWidget";
    private static int idGenerator = 1211322;

    /**
     * Generate a unique ID to keep Android UI happy when the screen orientation
     * changes.
     *
     * @return
     */
    public static int newUniqueId() {
        return ++idGenerator;
    }

    private LayoutParams mLayout;
    protected FormEntryPrompt mPrompt;

    protected final int mQuestionFontsize;
    protected final int mAnswerFontsize;

    private TextView mQuestionText;
    private MediaLayout mediaLayout;
    private TextView mHelpText;

    protected MediaPlayer mPlayer;

    protected int mPlayColor = Color.BLUE;
    protected int mPlayBackgroundColor = Color.WHITE;

    public QuestionWidget(Context context, FormEntryPrompt p) {
        super(context);

        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaLayout.resetTextFormatting();
                mediaPlayer.reset();
            }

        });
        mQuestionFontsize = Collect.getQuestionFontsize();
        mAnswerFontsize = mQuestionFontsize + 2;

        mPrompt = p;

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.TOP);
        setPadding(0, 7, 0, 0);

        mLayout = new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);
        mLayout.setMargins(10, 0, 10, 0);

        addQuestionText(p);
        addHelpText(p);

        String playColorString = p.getFormElement().getAdditionalAttribute(null, "playColor");
        if (playColorString != null) {
            try {
                mPlayColor = Color.parseColor(playColorString);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        Log.d("IIIIIII", "QuestionWidget: media layout" + mediaLayout);
        Log.d("IIIIIII", "QuestionWidget: media color" + mPlayColor);
        mediaLayout.setPlayTextColor(mPlayColor);

        String playBackgroundColorString = p.getFormElement().getAdditionalAttribute(null, "playBackgroundColor");
        if (playBackgroundColorString != null) {
            try {
                mPlayBackgroundColor = Color.parseColor(playBackgroundColorString);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        mediaLayout.setPlayTextBackgroundColor(mPlayBackgroundColor);
    }

    public void playAudio() {
        playAllPromptText();
    }

    public void playVideo() {
        mediaLayout.playVideo();
    }

    public FormEntryPrompt getPrompt() {
        return mPrompt;
    }

    // http://code.google.com/p/android/issues/detail?id=8488
    private void recycleDrawablesRecursive(ViewGroup viewGroup, List<ImageView> images) {

        int childCount = viewGroup.getChildCount();
        for (int index = 0; index < childCount; index++) {
            View child = viewGroup.getChildAt(index);
            if (child instanceof ImageView) {
                images.add((ImageView) child);
            } else if (child instanceof ViewGroup) {
                recycleDrawablesRecursive((ViewGroup) child, images);
            }
        }
        viewGroup.destroyDrawingCache();
    }

    // http://code.google.com/p/android/issues/detail?id=8488
    public void recycleDrawables() {
        List<ImageView> images = new ArrayList<ImageView>();
        // collect all the image views
        recycleDrawablesRecursive(this, images);
        for (ImageView imageView : images) {
            imageView.destroyDrawingCache();
            Drawable d = imageView.getDrawable();
            if (d != null && d instanceof BitmapDrawable) {
                imageView.setImageDrawable(null);
                BitmapDrawable bd = (BitmapDrawable) d;
                Bitmap bmp = bd.getBitmap();
                if (bmp != null) {
                    bmp.recycle();
                }
            }
        }
    }

    // Abstract methods
    public abstract IAnswerData getAnswer();

    public abstract void clearAnswer();

    public abstract void setFocus(Context context);

    public abstract void setOnLongClickListener(OnLongClickListener l);

    /**
     * Override this to implement fling gesture suppression (e.g. for embedded WebView treatments).
     *
     * @param e1
     * @param e2
     * @param velocityX
     * @param velocityY
     * @return true if the fling gesture should be suppressed
     */
    public boolean suppressFlingGesture(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    /**
     * Add a Views containing the question text, audio (if applicable), and image (if applicable).
     * To satisfy the RelativeLayout constraints, we add the audio first if it exists, then the
     * TextView to fit the rest of the space, then the image if applicable.
     */
    protected void addQuestionText(FormEntryPrompt p) {
        String imageURI = p.getImageText();
        String audioURI = p.getAudioText();
        String videoURI = p.getSpecialFormQuestionText("video");

        // shown when image is clicked
        String bigImageURI = p.getSpecialFormQuestionText("big-image");

        String promptText = p.getLongText();
        // Add the text view. Textview always exists, regardless of whether there's text.
        Typeface QUICKSAND = ResourcesCompat.getFont(getContext(), R.font.quicksand);
        mQuestionText = new TextView(getContext());
        mQuestionText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mQuestionFontsize);
        mQuestionText.setTypeface(QUICKSAND);
        mQuestionText.setPadding(0, 0, 0, 7);
        mQuestionText.setId(QuestionWidget.newUniqueId()); // assign random id
        mQuestionText.setText(promptText == null ? "" : TextUtils.textToHtml(promptText));
        mQuestionText.setMovementMethod(LinkMovementMethod.getInstance());

        // Wrap to the size of the parent view
        mQuestionText.setHorizontallyScrolling(false);

        if (promptText == null || promptText.length() == 0) {
            mQuestionText.setVisibility(GONE);
        }

        // Create the layout for audio, image, text
        mediaLayout = new MediaLayout(getContext(), mPlayer);
        mediaLayout.setAVT(p.getIndex(), "", mQuestionText, audioURI, imageURI, videoURI, bigImageURI);
        mediaLayout.setAudioListener(this);

        addView(mediaLayout, mLayout);
    }

    /**
     * Add a TextView containing the help text.
     */
    private void addHelpText(FormEntryPrompt p) {

        String s = p.getHelpText();

        if (s != null && !s.equals("")) {
            mHelpText = new TextView(getContext());
            mHelpText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mQuestionFontsize - 3);
            mHelpText.setPadding(0, -5, 0, 7);
            // wrap to the widget of view
            mHelpText.setHorizontallyScrolling(false);
            mHelpText.setTypeface(null, Typeface.ITALIC);
            mHelpText.setText(TextUtils.textToHtml(s));
            mHelpText.setMovementMethod(LinkMovementMethod.getInstance());

            addView(mHelpText, mLayout);
        }
    }

    /**
     * Every subclassed widget should override this, adding any views they may contain, and calling
     * super.cancelLongPress()
     */
    public void cancelLongPress() {
        super.cancelLongPress();
        if (mQuestionText != null) {
            mQuestionText.cancelLongPress();
        }
        if (mHelpText != null) {
            mHelpText.cancelLongPress();
        }
    }

    /*
     * Prompts with items must override this
     */
    public void playAllPromptText() {
        mediaLayout.playAudio();
    }

    public void setQuestionTextColor(int color) {
        mediaLayout.setTextcolor(color);
    }

    public void resetQuestionTextColor() {
        mediaLayout.resetTextFormatting();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if (visibility == INVISIBLE || visibility == GONE) {
            mPlayer.stop();
            mPlayer.reset();
        }
    }

    public void stopAudio() {
        mPlayer.stop();
        mPlayer.reset();
    }
}
