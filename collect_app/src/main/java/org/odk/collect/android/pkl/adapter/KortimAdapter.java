package org.odk.collect.android.pkl.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.pkl.object.Notifikasi;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;
import org.odk.collect.android.pkl.preference.StaticFinal;
import org.odk.collect.android.provider.InstanceProviderAPI;

import java.util.LinkedList;


public class KortimAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private LinkedList<Notifikasi> barangs;

    public KortimAdapter(Activity activity, LinkedList<Notifikasi> barangs) {
        this.activity = activity;
        this.barangs = barangs;
    }

    @Override
    public int getCount() {
        return barangs.size();
    }

    @Override
    public Object getItem(int location) {
        return barangs.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.pkl_kortim2, parent, false);
            holder.namepcl = (TextView) convertView.findViewById(R.id.nim_pcl);
            holder.uuidkos = (TextView) convertView.findViewById(R.id.blok_sensus);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.statusisian = (TextView) convertView.findViewById(R.id.statusisian);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Notifikasi item = barangs.get(position);
        Log.d("saNotifikasi kortima", "" + item.getStatus_isian());
        holder.namepcl.setText(item.getNama());
        holder.uuidkos.setText(item.getFilename());
        holder.status.setText(item.getStatus());
        holder.statusisian.setText(item.getStatus_isian());

        CapiPreference pref = CapiPreference.getInstance();
        String isKortim = (String) pref.get(CapiKey.KEY_ID_JABATAN);
        String status = item.getStatus();

        if (isKortim.equalsIgnoreCase(StaticFinal.JABATAN_KORTIM_ID)
                && !status.equalsIgnoreCase(InstanceProviderAPI.STATUS_SUBMITTED)
                || isKortim.equalsIgnoreCase(StaticFinal.JABATAN_PENCACAH_ID )
                && !status.equalsIgnoreCase(InstanceProviderAPI.STATUS_RETURNED)) {
//                    container.setBackgroundColor(ContextCompat.getColor(KortimListActivity.this, R.color.grey));
            holder.uuidkos.setTextColor(ContextCompat.getColor(activity, android.R.color.darker_gray));
            holder.status.setTextColor(ContextCompat.getColor(activity, android.R.color.darker_gray));
            holder.statusisian.setTextColor(ContextCompat.getColor(activity, android.R.color.darker_gray));
//                    listV.setSelector(android.R.color.transparent);
        } else {
            holder.uuidkos.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
            holder.status.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
            holder.statusisian.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
        }

        return convertView;
    }

    private class ViewHolder {
        TextView namepcl, uuidkos, status, statusisian;
    }
}
