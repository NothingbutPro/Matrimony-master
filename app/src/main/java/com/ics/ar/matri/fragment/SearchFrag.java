package com.ics.ar.matri.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ics.ar.matri.Models.CommanDTO;
import com.ics.ar.matri.Models.LoginDTO;
import com.ics.ar.matri.R;
import com.ics.ar.matri.SysApplication;
import com.ics.ar.matri.activity.dashboard.Dashboard;
import com.ics.ar.matri.activity.search.SearchResultMain;
import com.ics.ar.matri.database.TestAdapter;
import com.ics.ar.matri.interfaces.Consts;
import com.ics.ar.matri.interfaces.OnSpinerItemClick;
import com.ics.ar.matri.sharedprefrence.SharedPrefrence;
import com.ics.ar.matri.utils.SpinnerDialog;
import com.ics.ar.matri.view.CustomButton;
import com.ics.ar.matri.view.CustomEditText;
import com.ics.ar.matri.view.CustomTextView;
import com.ics.ar.matri.view.InputFieldView;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchFrag extends Fragment implements View.OnClickListener  {

    private View view;
    private Dashboard dashboard;
    private String TAG = SearchFrag.class.getSimpleName();
    private Context mContext;
    private LinearLayout llBride, llGroom, llBack;
    private CustomTextView tvBride, tvGroom;
    private InputFieldView inf_district;
    private CustomEditText etMaritial, etManglik, etState, etDistrict, etCaste;
    public EditText etAge;
    private ArrayList<CommanDTO> stateList = new ArrayList<>();
    private ArrayList<CommanDTO> districtList = new ArrayList<>();
    private SpinnerDialog spinnerMaritial, spinnerManglik, spinnerState, spinnerDistrict, spinnerCaste;
    public SysApplication sysApplication;
    HashMap<String, String> parms = new HashMap<>();
    TestAdapter mDbHelper;
    private CustomButton btnSearch;
    private ArrayList<CommanDTO> casteList = new ArrayList<>();
    private SharedPrefrence prefrence;
    private LoginDTO loginDTO;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        dashboard.headerNameTV.setText(getResources().getString(R.string.nav_search));
        sysApplication = SysApplication.getInstance(getActivity());
        mDbHelper = new TestAdapter(getActivity());
   etCaste = view.findViewById(R.id.etCaste);
   etAge = view.findViewById(R.id.etAge);
   etCaste.setText("Muslim");
        prefrence = SharedPrefrence.getInstance(getActivity());
        loginDTO = prefrence.getLoginResponse(Consts.LOGIN_DTO);
       // parms.put(Consts.USER_ID, loginDTO.getData().getId());



        mDbHelper.createDatabase();
        mDbHelper.open();
        parms.put(Consts.GENDER, "F");
        setUiAction(view);
        return view;

    }

    public void setUiAction(View v) {
        llBride = v.findViewById(R.id.llBride);
        llGroom = v.findViewById(R.id.llGroom);
        tvBride = v.findViewById(R.id.tvBride);
        tvGroom = v.findViewById(R.id.tvGroom);

        llBride.setOnClickListener(this);
        llGroom.setOnClickListener(this);

        etMaritial = v.findViewById(R.id.etMaritial);
    //    etManglik = v.findViewById(R.id.etManglik);
        etState = v.findViewById(R.id.etState);
        etDistrict = v.findViewById(R.id.etDistrict);



        etMaritial.setOnClickListener(this);
//        etManglik.setOnClickListener(this);
        etState.setOnClickListener(this);
        etDistrict.setOnClickListener(this);


        btnSearch = v.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);

        inf_district = v.findViewById(R.id.inf_district);

        spinnerMaritial = new SpinnerDialog(getActivity(), sysApplication.getMaritalList(), getResources().getString(R.string.select_maritial_status), R.style.DialogAnimations_SmileWindow, getResources().getString(R.string.close));// With 	Animation


        spinnerMaritial.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, String id, int position) {
                etMaritial.setText(item);
                parms.put(Consts.MARITAL_STATUS, id);
            }
        });
        spinnerManglik = new SpinnerDialog(getActivity(), sysApplication.getManglikList(), getResources().getString(R.string.select_manglik), R.style.DialogAnimations_SmileWindow, getResources().getString(R.string.close));// With 	Animation


        spinnerManglik.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, String id, int position) {
                etManglik.setText(item);
                parms.put(Consts.MANGLIK, id);

            }
        });
        stateList = new ArrayList<>();
        stateList = mDbHelper.getAllState("en");
        spinnerState = new SpinnerDialog(getActivity(), stateList, getResources().getString(R.string.select_state), R.style.DialogAnimations_SmileWindow, getResources().getString(R.string.close));// With 	Animation
        spinnerState.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, String id, int position) {
                etState.setText(item);
                parms.put(Consts.STATE, id);
                inf_district.setVisibility(View.VISIBLE);

                districtList = new ArrayList<>();
                districtList = mDbHelper.getAllDistrict(id, "en");
                showDistrict();
            }
        });

    }

    public void showDistrict() {
        spinnerDistrict = new SpinnerDialog(getActivity(), districtList, getResources().getString(R.string.select_district), R.style.DialogAnimations_SmileWindow, getResources().getString(R.string.close));// With 	Animation
        spinnerDistrict.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, String id, int position) {
                etDistrict.setText(item);
                parms.put(Consts.DISTRICT, id);

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.etMaritial:
                spinnerMaritial.showSpinerDialog();
                break;
            case R.id.etManglik:
                spinnerManglik.showSpinerDialog();
                break;
            case R.id.etState:
                spinnerState.showSpinerDialog();
                break;
            case R.id.etDistrict:
                spinnerDistrict.showSpinerDialog();
                break;
            case R.id.llBride:
                setSelected(true, false);
                parms.put(Consts.GENDER, "F");
                break;
            case R.id.llGroom:
                setSelected(false, true);
                parms.put(Consts.GENDER, "M");
                break;
            case R.id.btnSearch:
              //  Intent in = new Intent(getActivity(), SearchResultMain.class);
                parms.put(Consts.USER_ID, prefrence.getKEY_UID());
                parms.put(Consts.TOKEN, loginDTO.getAccess_token());
                parms.put(Consts.Age , etAge.getText().toString());
                Intent in = new Intent(getActivity(), SearchResultMain.class);
                Toast.makeText(dashboard, "age is"+etAge.getText().toString(), Toast.LENGTH_SHORT).show();
                in.putExtra(Consts.SEARCH_PARAM, parms);
                startActivity(in);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
                break;

        }
    }

    public void setSelected(boolean firstBTN, boolean secondBTN) {
        if (firstBTN) {
            llBride.setBackground(getResources().getDrawable(R.drawable.gender_selecte));
            llGroom.setBackground(getResources().getDrawable(R.drawable.gender_unselecte));
            tvBride.setTextColor(getResources().getColor(R.color.lllgreen));
            tvGroom.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        if (secondBTN) {
            llBride.setBackground(getResources().getDrawable(R.drawable.gender_unselecte));
            llGroom.setBackground(getResources().getDrawable(R.drawable.gender_selecte));

            tvBride.setTextColor(getResources().getColor(R.color.colorPrimary));
            tvGroom.setTextColor(getResources().getColor(R.color.lllgreen));
        }

        llBride.setSelected(firstBTN);
        llGroom.setSelected(secondBTN);
    }




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dashboard = (Dashboard) activity;

    }

}
