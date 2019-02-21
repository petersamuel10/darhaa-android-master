package com.itsoluation.vavisa.darhaa.fargments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itsoluation.vavisa.darhaa.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactSupport extends Fragment {


    public ContactSupport() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_support, container, false);
    }

}
