package com.ics.ar.matri.fragment;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;

import com.ics.ar.matri.R;

public class Divider_Profile_for extends DividerItemDecoration {

    public Divider_Profile_for(Context context, int orientation) {
        super(context, orientation);
        setDrawable(ContextCompat.getDrawable(context, R.drawable.divider_profile_for));
    }
}
