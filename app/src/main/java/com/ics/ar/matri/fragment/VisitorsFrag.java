package com.ics.ar.matri.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ics.ar.matri.Models.LoginDTO;
import com.ics.ar.matri.Models.UserDTO;
import com.ics.ar.matri.R;
import com.ics.ar.matri.activity.dashboard.Dashboard;
import com.ics.ar.matri.adapter.AdapterVisitor;
import com.ics.ar.matri.https.HttpsRequest;
import com.ics.ar.matri.interfaces.Consts;
import com.ics.ar.matri.interfaces.Helper;
import com.ics.ar.matri.network.NetworkManager;
import com.ics.ar.matri.sharedprefrence.SharedPrefrence;
import com.ics.ar.matri.utils.ProjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VisitorsFrag extends Fragment {

    private View view;
    public Dashboard dashboard;
    private String TAG = VisitorsFrag.class.getSimpleName();
    private RecyclerView rvMatch;
    LinearLayoutManager mLayoutManager;
    private AdapterVisitor adapterVisitor;
    private ArrayList<UserDTO> joinDTOList;
    private SharedPrefrence prefrence;
    private LoginDTO loginDTO;

    private int current = 1;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private LinearLayout rlView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_visitors, container, false);
        dashboard.headerNameTV.setText(getResources().getString(R.string.nav_visitor));
        prefrence = SharedPrefrence.getInstance(getActivity());
        loginDTO = prefrence.getLoginResponse(Consts.LOGIN_DTO);
        setUiAction(view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dashboard = (Dashboard) activity;

    }

    public void setUiAction(View view) {
        rvMatch = view.findViewById(R.id.rvMatch);
        rlView = view.findViewById(R.id.rlView);


        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        rvMatch.setLayoutManager(mLayoutManager);


    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetworkManager.isConnectToInternet(getActivity())) {
            getUsers();

        } else {
            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
        }
    }

    public void getUsers() {
        ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.GET_VISITORS_API, getparm(), getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    try {
                        joinDTOList = new ArrayList<>();
                        Type getpetDTO = new TypeToken<List<UserDTO>>() {
                        }.getType();
                        joinDTOList = (ArrayList<UserDTO>) new Gson().fromJson(response.getJSONArray("data").toString(), getpetDTO);
                        showData();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        rlView.setVisibility(View.VISIBLE);
                        rvMatch.setVisibility(View.GONE);
                    }


                } else {
                    ProjectUtils.showToast(getActivity(), msg);
                    rlView.setVisibility(View.VISIBLE);
                    rvMatch.setVisibility(View.GONE);
                }
            }
        });
    }

    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
       // parms.put(Consts.USER_ID, loginDTO.getData().getId());
        parms.put(Consts.USER_ID, prefrence.getKEY_UID());
        parms.put(Consts.TOKEN, loginDTO.getAccess_token());

        if (loginDTO.getData().getGender().equalsIgnoreCase("M")) {
            parms.put(Consts.GENDER, "M");
        } else {
            parms.put(Consts.GENDER, "F");
        }
        Log.e("parms", parms.toString());
        return parms;
    }

    public void showData() {
        if (joinDTOList.size() > 0) {
            rlView.setVisibility(View.GONE);
            rvMatch.setVisibility(View.VISIBLE);
        } else {
            rlView.setVisibility(View.VISIBLE);
            rvMatch.setVisibility(View.GONE);
        }
        adapterVisitor = new AdapterVisitor(joinDTOList, VisitorsFrag.this);
        rvMatch.setAdapter(adapterVisitor);
    }

}