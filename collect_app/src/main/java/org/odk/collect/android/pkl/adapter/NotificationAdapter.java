package org.odk.collect.android.pkl.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.pkl.database.DatabaseHandler;
import org.odk.collect.android.pkl.object.NotificationModel;

import java.util.LinkedList;

/**
 * @author Rahadi
 */

public class NotificationAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private LinkedList<NotificationModel> notifications, allNotifications;
    private ValueFilter valueFilter;
    private String query;

    public NotificationAdapter(Context context, LinkedList<NotificationModel> notifications) {
        this.context = context;
        this.notifications = notifications;
        this.allNotifications = notifications;
        this.query = "";
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {

        ViewHolder holder = null;

        if (v == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_notification, parent, false);
            holder.title = (TextView) v.findViewById(R.id.title);
            holder.category = (TextView) v.findViewById(R.id.category);
            holder.date = (TextView) v.findViewById(R.id.date);
            holder.content = (TextView) v.findViewById(R.id.content);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        final boolean isExpanded = false;
        final LinearLayout container = (LinearLayout) v.findViewById(R.id.answer_container);
        final LinearLayout toggle = (LinearLayout) v.findViewById(R.id.toggle_button);
        final ImageView toggleimg = (ImageView) v.findViewById(R.id.toggle_img);
        final ImageView unread = (ImageView) v.findViewById(R.id.unread_marker);

        container.setVisibility(View.GONE);
        unread.setVisibility(DatabaseHandler.getInstance(context).getIsReadStatusById(notifications.get(position).getId()) ? View.GONE : View.VISIBLE);

        toggleimg.setImageResource(R.drawable.ic_keyboard_arrow_down);
        toggle.setOnClickListener(new View.OnClickListener() {
            boolean isExpanded1 = isExpanded;

            @Override
            public void onClick(View v) {
                if (!DatabaseHandler.getInstance(context).getIsReadStatusById(notifications.get(position).getId())) {
                    DatabaseHandler.getInstance(context).setRead(notifications.get(position).getId(), true);
                    unread.setVisibility(View.GONE);
                }
                if (isExpanded1) {
                    container.setVisibility(View.GONE);
                    toggleimg.setImageResource(R.drawable.ic_keyboard_arrow_down);
                    isExpanded1 = false;
                } else {
                    container.setVisibility(View.VISIBLE);
                    toggleimg.setImageResource(R.drawable.ic_keyboard_arrow_up);
                    isExpanded1 = true;
                }
            }
        });

        holder.title.setText(notifications.get(position).getJudul());
        holder.date.setText(notifications.get(position).getDate());
        holder.content.setText(notifications.get(position).getKontenHTML());

//        holder.content.setText(notifications.get(position).getKonten());
        holder.category.setText(notifications.get(position).getKategori());

        String notifikasi = notifications.get(position).getJudul().toLowerCase();
        if (notifikasi.contains(query) && query.length() != 0) {
            int startPos = notifikasi.indexOf(query);
            int endPos = startPos + query.length();

            Spannable spanText = Spannable.Factory.getInstance().newSpannable(holder.title.getText());
            spanText.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.title.setText(spanText, TextView.BufferType.SPANNABLE);
        }

        return v;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults fr = new FilterResults();
            if (constraint == null) {
                fr.count = allNotifications.size();
                fr.values = allNotifications;
                query = "";
            } else {
                LinkedList<NotificationModel> filtered = new LinkedList<>();
                for (NotificationModel p : allNotifications) {
                    if (p.getJudul().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filtered.addLast(p);
                    }
                }
                fr.count = filtered.size();
                fr.values = filtered;
                query = constraint.toString();
            }
            return fr;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notifications = (LinkedList<NotificationModel>) results.values;
            notifyDataSetChanged();
        }
    }

    private class ViewHolder {
        TextView title, date, content, category;
    }
}
