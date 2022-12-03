package org.odk.collect.android.pkl.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import org.odk.collect.android.R;
import org.odk.collect.android.pkl.adapter.OnboardAdapter;
import org.odk.collect.android.pkl.object.OnBoardItem;

import java.util.ArrayList;

public class OnBoardingActivity extends AppCompatActivity {


    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;


    private ViewPager onboard_pager;

    private OnboardAdapter mAdapter;

    private Button btn_get_started;
    private Button bSkip;
    private ImageButton ibNext;
    RoundCornerProgressBar progress1;

    int previous_pos = 0;


    ArrayList<OnBoardItem> onBoardItems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        btn_get_started = (Button) findViewById(R.id.btn_get_started);
        onboard_pager = (ViewPager) findViewById(R.id.pager_introduction);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        bSkip = (Button) findViewById(R.id.aob_b_skip);
        ibNext = (ImageButton) findViewById(R.id.intro_btn_next);
        progress1 = (RoundCornerProgressBar) findViewById(R.id.progress_1);

        loadData();


        mAdapter = new OnboardAdapter(this, onBoardItems);
        onboard_pager.setAdapter(mAdapter);
        onboard_pager.setCurrentItem(0);
        progress1.setProgressColor(Color.parseColor("#048ebf"));
        progress1.setProgressBackgroundColor(Color.parseColor("#b3b3b3"));
        progress1.setMax(mAdapter.getCount());
        progress1.setProgress(1);

        onboard_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                progress1.setProgress(position + 1);
                progress1.setProgressColor(Color.parseColor("#048ebf"));
                progress1.setProgressBackgroundColor(Color.parseColor("#b3b3b3"));
                progress1.setMax(dotsCount);


//                 Change the current position intimation

                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(OnBoardingActivity.this, R.drawable.non_selected_item_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(OnBoardingActivity.this, R.drawable.selected_item_dot));


                int pos = position + 1;

                if (pos == dotsCount && previous_pos == (dotsCount - 1)) {
                    ibNext.setVisibility(View.GONE);
                    btn_get_started.setVisibility(View.VISIBLE);
                } else {
                    if (pos == (dotsCount - 1) && previous_pos == dotsCount) {
                        btn_get_started.setVisibility(View.GONE);
                        ibNext.setVisibility(View.VISIBLE);
                    }
                }
                previous_pos = pos;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btn_get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ibNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onboard_pager.setCurrentItem(onboard_pager.getCurrentItem() + 1, true);
            }
        });

        setUiPageViewController();
    }
    // Load data into the viewpager

    public void loadData() {

        int[] header = {R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong, R.string.kosong};
        int[] desc = {R.string.kosong, R.string.l01, R.string.l01_1, R.string.l01_2, R.string.l02, R.string.l03, R.string.l04, R.string.l05, R.string.l06, R.string.l07, R.string.l08, R.string.l09, R.string.l10, R.string.l11, R.string.l12, R.string.l13, R.string.l14, R.string.l15, R.string.l16, R.string.l17, R.string.l18, R.string.l19, R.string.l20, R.string.l21, R.string.l22, R.string.l23, R.string.l24};
        int[] imageId = {R.drawable.i00, R.drawable.i01, R.drawable.i01_1, R.drawable.i01_2, R.drawable.i02, R.drawable.i03, R.drawable.i04, R.drawable.i05, R.drawable.i06, R.drawable.i07, R.drawable.i08, R.drawable.i09, R.drawable.i10, R.drawable.i11, R.drawable.i12, R.drawable.i13, R.drawable.i14, R.drawable.i15, R.drawable.i16, R.drawable.i17,R.drawable.i18, R.drawable.i19, R.drawable.i20, R.drawable.i21, R.drawable.i22, R.drawable.i23, R.drawable.i24};
        for (int i = 0; i < imageId.length; i++) {
            OnBoardItem item = new OnBoardItem();
            item.setImageID(imageId[i]);
            item.setTitle(getResources().getString(header[i]));
            item.setDescription(getResources().getString(desc[i]));

            onBoardItems.add(item);
        }
    }

    // Button bottomUp animation

    public void show_animation() {
        Animation show = AnimationUtils.loadAnimation(this, R.anim.slide_up_anim);

        btn_get_started.startAnimation(show);

        show.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                btn_get_started.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                btn_get_started.clearAnimation();

            }

        });


    }

    // Button Topdown animation

    public void hide_animation() {
        Animation hide = AnimationUtils.loadAnimation(this, R.anim.slide_down_anim);

        btn_get_started.startAnimation(hide);

        hide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                btn_get_started.clearAnimation();
                btn_get_started.setVisibility(View.GONE);

            }

        });


    }

    // setup the
    private void setUiPageViewController() {
        dotsCount = mAdapter.getCount();


        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(OnBoardingActivity.this, R.drawable.non_selected_item_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(6, 0, 6, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(OnBoardingActivity.this, R.drawable.selected_item_dot));
    }
}
