package com.ics.ar.matri.fragment.registration;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TimePicker;

import com.ics.ar.matri.Models.CommanDTO;
import com.ics.ar.matri.R;
import com.ics.ar.matri.activity.loginsignup.Registration;
import com.ics.ar.matri.database.TestAdapter;
import com.ics.ar.matri.interfaces.OnSpinerItemClick;
import com.ics.ar.matri.sharedprefrence.SharedPrefrence;
import com.ics.ar.matri.utils.ProjectUtils;
import com.ics.ar.matri.utils.SpinnerDialog;
import com.ics.ar.matri.view.CustomEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SocialDetails extends Fragment implements View.OnClickListener {
    private View view;
    private Registration registration;
    private  SharedPrefrence preferences;
    public CustomEditText etMaritial, etGotra, etManglik, etBirthTime, etBirthPlace,etGotraNanihal,etCaste;
    public EditText etsubCaste;
    SpinnerDialog spinnerMaritial, spinnerManglik, spinnerCaste;
    public String marital = "", manglik = "";
    private ProjectUtils.CustomTimePickerDialog dialog;
    private Calendar myCalendar = Calendar.getInstance();
    TestAdapter mDbHelper;
    private ArrayList<CommanDTO> casteList = new ArrayList<>();
    public String caste = "";
    public String sub_caste = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_social_details, container, false);
        mDbHelper = new TestAdapter(getActivity());

        mDbHelper.createDatabase();
        mDbHelper.open();
        setUiAction(view);
        return view;
    }

    public void setUiAction(View v) {
        etMaritial = v.findViewById(R.id.etMaritial);
        etGotra = v.findViewById(R.id.etGotra);
        etsubCaste = v.findViewById(R.id.etsubCaste);
        etGotraNanihal = v.findViewById(R.id.etGotraNanihal);
        etManglik = v.findViewById(R.id.etManglik);
        etBirthTime = v.findViewById(R.id.etBirthTime);
        etBirthPlace = v.findViewById(R.id.etBirthPlace);
        etCaste = v.findViewById(R.id.etCaste);
          preferences = new SharedPrefrence();
        etMaritial.setOnClickListener(this);
        etManglik.setOnClickListener(this);
        etBirthTime.setOnClickListener(this);
        etCaste.setOnClickListener(this);


        spinnerMaritial = new SpinnerDialog(getActivity(), registration.sysApplication.getMaritalList(), getResources().getString(R.string.select_maritial_status), R.style.DialogAnimations_SmileWindow, getResources().getString(R.string.close));// With 	Animation


        spinnerMaritial.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, String id,int position) {
                etMaritial.setText(item);
                marital =id;
            }
        });
        spinnerManglik = new SpinnerDialog(getActivity(), registration.sysApplication.getManglikList(), getResources().getString(R.string.select_manglik), R.style.DialogAnimations_SmileWindow, getResources().getString(R.string.close));// With 	Animation


        spinnerManglik.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item,String id, int position) {
                etManglik.setText(item);
                manglik =id;
            }
        });

        casteList = new ArrayList<>();
      //  casteList = mDbHelper.getAllCaste(registration.lang);
        casteList.add(new CommanDTO("1","Muslim"));
        spinnerCaste = new SpinnerDialog(getActivity(), casteList, getResources().getString(R.string.select_caste), R.style.DialogAnimations_SmileWindow, getResources().getString(R.string.close));// With 	Animation
        spinnerCaste.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, String id, int position) {
                etCaste.setText(item);
                caste = id;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.etMaritial:
                spinnerMaritial.showSpinerDialog();
                break;
            case R.id.etCaste:
                spinnerCaste.showSpinerDialog();
                break;
            case R.id.etManglik:
                spinnerManglik.showSpinerDialog();
                break;
            case R.id.etBirthTime:

                    dialog = new ProjectUtils.CustomTimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            myCalendar.set(Calendar.MINUTE, minute);

                            SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a");
                            etBirthTime.setText(sdf1.format(myCalendar.getTime()));
                        }
                    },
                            myCalendar.getTime().getHours(), myCalendar.getTime().getMinutes(), false);
                    dialog.show();

                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        registration = (Registration) context;

    }

}
