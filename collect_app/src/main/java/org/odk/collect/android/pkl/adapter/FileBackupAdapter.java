package org.odk.collect.android.pkl.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.pkl.object.FileBackup;
import org.odk.collect.android.pkl.preference.SyncManager;

import java.util.ArrayList;

/**
 * Created by isfann on 1/11/2017.
 */

public class FileBackupAdapter extends RecyclerView.Adapter<FileBackupAdapter.FileBackupViewHolder> {
    Context context;
    LayoutInflater inflater;
    ArrayList<FileBackup> fileList;
    final String TAG = "FILEBACKUP";

    OnClickCallback onClickCallback;

    public void setOnClickCallback(OnClickCallback onClickCallback) {
        this.onClickCallback = onClickCallback;
    }

    public FileBackupAdapter(Context context, ArrayList<FileBackup> fileList) {
        this.context = context;
        this.fileList = fileList;
    }

    @Override
    public void onBindViewHolder(FileBackupViewHolder holder, int position) {
        holder.setText(fileList.get(position));
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    @Override
    public FileBackupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.kotak_file_backup, parent, false);
        return new FileBackupViewHolder(v);
    }

    class FileBackupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvDeviceId, tvFileDate, tvBackupSource;
        ImageButton tombolAmbil;
        SyncManager sm;

        public FileBackupViewHolder(View itemView) {
            super(itemView);

            tombolAmbil = (ImageButton) itemView.findViewById(R.id.tombol_ambil);
            tvDeviceId = (TextView) itemView.findViewById(R.id.device_id);
            tvBackupSource = (TextView) itemView.findViewById(R.id.sumber);
            tvFileDate = (TextView) itemView.findViewById(R.id.tanggal_file);
            tombolAmbil.setOnClickListener(this);
        }

        public void setText(FileBackup file) {
            sm = new SyncManager(context);
            String thisDeviceId = sm.getDeviceId();

            tvFileDate.setText(file.getDate());
            tvBackupSource.setText(file.getSource());
            if (file.getDeviceId().equals(thisDeviceId)) {
                tvDeviceId.setText("This Device");
            } else {
                tvDeviceId.setText("Other Device");
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tombol_ambil) {
                onClickCallback.onClickDownloadBackup(getAdapterPosition());
                Log.d(TAG, "onClick: Clicked at adapter pos " + String.valueOf(getAdapterPosition()));
            }
        }
    }

    public interface OnClickCallback {
        void onClickDownloadBackup(int p);
    }
}