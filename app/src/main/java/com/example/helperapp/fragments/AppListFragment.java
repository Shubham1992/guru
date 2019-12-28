package com.example.helperapp.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.helperapp.MainActivity;
import com.example.helperapp.R;
import com.example.helperapp.adapters.AppListAdapter;
import com.example.helperapp.models.AppModel;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AppListFragment} interface
 * to handle interaction events.
 * Use the {@link AppListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final int REQUEST_READ_PHONE_STATE = 1001;
    private String TAG = "tag";
    private FirebaseAnalytics mFirebaseAnalytics;
    private String mPhoneNumber = "";
    private List<ApplicationInfo> packages;
    private RecyclerView rvAppList;
    private PackageManager pm;

    public AppListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AppListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AppListFragment newInstance(String param1, String param2) {
        AppListFragment fragment = new AppListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_app_list, container, false);

        rvAppList = v.findViewById(R.id.rvAppList);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        pm = getActivity().getPackageManager();
        packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        ArrayList<AppModel> appModels = new ArrayList<>();
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo == null || packageInfo.name == null)
                continue;
            AppModel appModel = new AppModel();
            appModel.setName(String.valueOf(pm.getApplicationLabel(packageInfo)));
            appModel.setPackageName(packageInfo.packageName);
            if (isSystemPackage(packageInfo)) {
                continue;
            }
            if (appModel.getName().toLowerCase().contains("whatsapp") ||
                    appModel.getName().toLowerCase().contains("facebook") ||
                    appModel.getName().toLowerCase().contains("pay") ||
                    appModel.getName().toLowerCase().contains("amazon")) {
                appModels.add(0, appModel);
                continue;
            } else if (appModel.getName().toLowerCase().contains("com.")) {
                continue;
            }
            appModels.add(appModel);
        }

        AppListAdapter appListAdapter = new AppListAdapter(appModels, getActivity());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        rvAppList.setLayoutManager(gridLayoutManager);
        rvAppList.setAdapter(appListAdapter);


        return v;
    }

    private boolean isSystemPackage(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
