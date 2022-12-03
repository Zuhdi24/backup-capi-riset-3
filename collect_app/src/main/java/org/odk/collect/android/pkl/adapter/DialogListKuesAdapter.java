package org.odk.collect.android.pkl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.pkl.object.DialogListKuesModel;

import java.util.LinkedList;

/**
 * Created by Rahadi on 20/02/2017.
 */

public class DialogListKuesAdapter extends BaseAdapter {

    private Context context;
    private LinkedList<DialogListKuesModel> list;

    public DialogListKuesAdapter(Context context, LinkedList<DialogListKuesModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder = null;

        if (v == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_dialog_kues_chooser, parent, false);
            holder.displayName = (TextView) v.findViewById(R.id.judul_kues);
            holder.JRVersion = (TextView) v.findViewById(R.id.id_kues);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.displayName.setText(list.get(position).getDisplayName());
        holder.JRVersion.setText(list.get(position).getJRVersion());

        return v;
    }

    private class ViewHolder {
        TextView displayName, displaySubText, JRVersion;
    }
}
