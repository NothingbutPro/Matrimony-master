package com.ics.ar.matri.adapter;

/**
 * Created by varun on 28/08/18.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.ics.ar.matri.Models.PackagesDTO;
import com.ics.ar.matri.R;
import com.ics.ar.matri.activity.subscription.MemberShipActivity;
import com.ics.ar.matri.sharedprefrence.SharedPrefrence;
import com.ics.ar.matri.utils.ProjectUtils;
import com.ics.ar.matri.view.CustomTextViewBold;

import java.util.ArrayList;

public class PackageslistAdapter extends RecyclerView.Adapter<PackageslistAdapter.MyViewHolder> {

    private MemberShipActivity memberShipActivity;
    private ArrayList<PackagesDTO> packagesDTOlist;
    private SharedPrefrence sharedPrefrence;


    public PackageslistAdapter(MemberShipActivity memberShipActivity, ArrayList<PackagesDTO> packagesDTOlist) {
        this.memberShipActivity = memberShipActivity;
        this.packagesDTOlist = packagesDTOlist;
        sharedPrefrence = SharedPrefrence.getInstance(memberShipActivity);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_packages, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.packagesDTO = packagesDTOlist.get(position);

        holder.tvbRS.setText(holder.packagesDTO.getPrice());
        holder.tvbTitle.setText(holder.packagesDTO.getTitle());
        holder.tvbDis.setText(holder.packagesDTO.getDescription());
        holder.tvCurrency.setText("INR");
        holder.tvPackageName.setText(holder.packagesDTO.getTitle());

        holder.llChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                memberShipActivity.updateList(position);
                if (holder.packagesDTO != null) {
                   memberShipActivity.callInstamojoPay();
//                    Intent intent = new Intent( v.getContext() , Dashboard.class);
//                    v.getContext().startActivity(intent);

                }
                else {
                    ProjectUtils.showToast(memberShipActivity, memberShipActivity.getResources().getString(R.string.plan_select));

                }
            }
        });

    }

    @Override
    public int getItemCount() {

        return packagesDTOlist.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CustomTextViewBold tvbTitle, tvbRS, tvbDis,tvCurrency,tvPackageName;
        public LinearLayout llChoose, llOne;
        public PackagesDTO packagesDTO;

        public MyViewHolder(View view) {
            super(view);

            tvbTitle = (CustomTextViewBold) view.findViewById(R.id.tvbTitle);
            tvbRS = (CustomTextViewBold) view.findViewById(R.id.tvbRS);
            tvbDis = (CustomTextViewBold) view.findViewById(R.id.tvbDis);
            tvCurrency = (CustomTextViewBold) view.findViewById(R.id.tvCurrency);
            tvPackageName = (CustomTextViewBold) view.findViewById(R.id.tvPackageName);
            llChoose = (LinearLayout) view.findViewById(R.id.llChoose);
            llOne = (LinearLayout) view.findViewById(R.id.llOne);

        }
    }


}