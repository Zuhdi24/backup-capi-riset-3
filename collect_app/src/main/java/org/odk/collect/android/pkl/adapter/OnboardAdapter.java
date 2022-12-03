package org.odk.collect.android.pkl.adapter;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.odk.collect.android.R;
import org.odk.collect.android.pkl.object.OnBoardItem;

import java.util.ArrayList;

public class OnboardAdapter extends PagerAdapter {
    private Context mContext;
    ArrayList<OnBoardItem> onBoardItems=new ArrayList<>();

    public OnboardAdapter(Context mContext, ArrayList<OnBoardItem> items) {
        this.mContext = mContext;
        this.onBoardItems = items;
    }
    @Override
    public int getCount() {
        return onBoardItems.size();
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.activity_on_board_item, container, false);

        OnBoardItem item=onBoardItems.get(position);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.iv_onboard);
        imageView.setImageResource(item.getImageID());

        TextView tv_title=(TextView)itemView.findViewById(R.id.tv_header);
        tv_title.setText(item.getTitle());

        TextView tv_content=(TextView)itemView.findViewById(R.id.tv_desc);
        tv_content.setText(item.getDescription());

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout) object);
    }

}
