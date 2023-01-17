package org.odk.collect.android.pkl.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import org.odk.collect.android.R;
import org.odk.collect.android.pkl.adapter.holder.HolderChildAnggotaTim;
import org.odk.collect.android.pkl.adapter.holder.HolderParentAnggotaTim;
import org.odk.collect.android.pkl.adapter.holder.ParentListAnggotaTim;
import org.odk.collect.android.pkl.object.AnggotaTim;
import org.odk.collect.android.pkl.object.BlokSensus;
import org.odk.collect.android.pkl.object.PopUpTest;

import java.util.List;

/**
 * @author TimHode
 */

public class AnggotaTimAdapter extends ExpandableRecyclerAdapter<HolderParentAnggotaTim, HolderChildAnggotaTim> {

    private LayoutInflater mInflater;
    private Context mContext;
    private Resources resources;

    public AnggotaTimAdapter(@NonNull List<? extends ParentListItem> parentItemList, Context context, Resources resources) {
        super(parentItemList);
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.resources = resources;
    }

    @Override
    public HolderParentAnggotaTim onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View parentView = mInflater.inflate(R.layout.list_anggota, parentViewGroup, false);
        return new HolderParentAnggotaTim(parentView);
    }

    @Override
    public HolderChildAnggotaTim onCreateChildViewHolder(ViewGroup childViewGroup) {
        View childView = mInflater.inflate(R.layout.list_bebanbs_exp, childViewGroup, false);
        return new HolderChildAnggotaTim(childView);
    }

    @Override
    public void onBindParentViewHolder(HolderParentAnggotaTim parentViewHolder, int position, ParentListItem parentListItem) {
        ParentListAnggotaTim ls = (ParentListAnggotaTim) parentListItem;
        final AnggotaTim at = ls.getAt();
        String prog = at.getProgres();

        // Hide progress bar
        parentViewHolder.getProgress().setVisibility(View.GONE);
        int pro;
        if (prog.isEmpty() || at.getProgres() == null) {
            parentViewHolder.getProgress().setProgressInTime(0, 0, 1000);
            parentViewHolder.getProgress().setTextVisible(true);
        } else {
            String[] progs = prog.split("%");
            String[] progres = progs[0].split(".");
            if (progres.length > 0) {
                Log.e("Progress", progres[0]);
                prog = progres[0];
            } else {
                prog = progs[0];
            }
            pro = (int) Math.floor(Double.parseDouble(prog));
            parentViewHolder.getProgress().setProgressInTime(0, pro, 1000);
            parentViewHolder.getProgress().setTextVisible(true);

        }
        parentViewHolder.getNama().setText(at.getNamaAng());
        parentViewHolder.getNim().setText(at.getNimAng());


        parentViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                View customJudul = LayoutInflater.from(mContext).inflate(R.layout.judul_popup, null);
                TextView judul = (TextView) customJudul.findViewById(R.id.judulBS);
                TextView nomorPclKortim = (TextView) customJudul.findViewById(R.id.nomor_pclkortim);
                judul.setText(at.getNamaAng());
                judul.setTextSize(16.0f);
                String nomor = at.getNomorAng();
                final Boolean hasNomor;
                if (nomor.equalsIgnoreCase("")) {
                    hasNomor = false;
                    nomorPclKortim.setVisibility(View.GONE);
                } else {
                    nomorPclKortim.setVisibility(View.VISIBLE);
                    nomorPclKortim.setText("+" + nomor);
                    hasNomor = true;
                }

                final PopUpTest[] popUpTests = {
//                        new PopUpTest("WhatsApp", R.drawable.wa_unf),
                        new PopUpTest("Hubungi lewat telepon", R.drawable.ic_phone_black_24dp),
                        new PopUpTest("Kirim pesan (SMS)", R.drawable.ic_email_black_24dp),
                        new PopUpTest("Kirim pesan (WhatsApp)", R.drawable.whatsapp),
                };
                ListAdapter la = new ArrayAdapter<PopUpTest>(
                        mContext,
                        R.layout.list_popup_anggota,
                        R.id.text_pop,
                        popUpTests) {

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        TextView tv = (TextView) v.findViewById(R.id.text_pop);
                        tv.setText(popUpTests[position].getText());

                        tv.setCompoundDrawablesWithIntrinsicBounds(popUpTests[position].getResource(), 0, 0, 0);
                        //Add margin between image and text (support various screen densities)
                        int dp5 = (int) (10 * resources.getDisplayMetrics().density + 0.5f);
                        tv.setCompoundDrawablePadding(dp5);
                        return v;
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setCustomTitle(customJudul);
                builder.setAdapter(la, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        switch (i) {
//                            case 0:
//                                if (hasNomor) {
//                                    openWhatsApp(at.getNomorAng());
//                                } else {
//                                    Toast.makeText(mContext, "Nomor Tidak Tersedia", Toast.LENGTH_SHORT).show();
//                                }
//                                break;
                            case 0:
                                if (hasNomor) {
                                    callContact("+" + at.getNomorAng());
                                } else {
                                    Toast.makeText(mContext, "Nomor Tidak Tersedia", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 1:
                                if (hasNomor) {
                                    sendMessage("+" + at.getNomorAng());
                                } else {
                                    Toast.makeText(mContext, "Nomor Tidak Tersedia", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 2:
                                if (hasNomor) {
                                    openWhatsapp("+" + at.getNomorAng());
                                } else {
                                    Toast.makeText(mContext, "Nomor Tidak Tersedia", Toast.LENGTH_SHORT).show();
                                }
                        }
                    }
                }).show();
                return true;
            }
        });
    }

    @Override
    public void onBindChildViewHolder(HolderChildAnggotaTim childViewHolder, int position, Object childListItem) {
        BlokSensus b = (BlokSensus) childListItem;
        childViewHolder.getBeban_bs().setText(b.getNoBs());
        childViewHolder.getKel().setText("KEL: " + b.getNamaDesa());
    }

//    private void openWhatsApp(String smsNumber) {
//
//        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
//        if (isWhatsappInstalled) {
//
//            Intent sendIntent = new Intent("android.intent.action.MAIN");
//            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
//            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(smsNumber) + "@s.whatsapp.net");//phone number without "+" prefix
//
//            mContext.startActivity(sendIntent);
//        } else {
//            Uri uri = Uri.parse("market://details?id=com.whatsapp");
//            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//            Toast.makeText(mContext, "WhatsApp not Installed",
//                    Toast.LENGTH_SHORT).show();
//            mContext.startActivity(goToMarket);
//        }
//    }

//    private boolean whatsappInstalledOrNot(String uri) {
//        PackageManager pm = mContext.getPackageManager();
//        boolean app_installed = false;
//        try {
//            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
//            app_installed = true;
//        } catch (PackageManager.NameNotFoundException e) {
//            app_installed = false;
//        }
//        return app_installed;
//    }

    private void callContact(String nomor) {
        Uri uri = Uri.parse("tel:" + nomor);
        Intent it = new Intent(Intent.ACTION_DIAL, uri);

        try {
            mContext.startActivity(it);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mContext, "Failed Call", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessage(String nomor) {
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto: " + nomor));
        smsIntent.putExtra("sms_body", "");

        try {
            mContext.startActivity(smsIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mContext,
                    "SMS faild, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openWhatsapp(String nomor) {
        String url = "https://api.whatsapp.com/send?phone=" + nomor;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        mContext.startActivity(i);
    }
}
