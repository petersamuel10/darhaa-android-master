package com.itsoluation.vavisa.darhaa.fargments;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.itsoluation.vavisa.darhaa.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactUS extends Fragment {

    LinearLayout call, send_email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_support, container, false);

        call = view.findViewById(R.id.call);
        send_email = view.findViewById(R.id.send_email);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setMessage(getResources().getString(R.string.call_to)+"123456789");
                builder1.setPositiveButton(
                        getString(R.string.ok),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                boolean call = getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
                                if(call) {
                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "123456789"));
                                    startActivity(intent);
                                    dialog.cancel();
                                }
                            }
                        });

                builder1.setNegativeButton(
                        getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                              dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });

        send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"vavisa@vavisa-kw.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                i.putExtra(Intent.EXTRA_TEXT   , "body of email");
                try {
                    startActivity(i);
                } catch (android.content.ActivityNotFoundException ex) {
                }

            }
        });

        return view;
    }

}