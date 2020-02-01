package com.example.helperapp.onboarding;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helperapp.R;
import com.example.helperapp.adapters.NotEnoughAppListAdapter;
import com.example.helperapp.adapters.SelectedAppListAdapter;
import com.example.helperapp.models.AppModel;
import com.example.helperapp.utils.AppHelper;
import com.example.helperapp.utils.SharedPrefUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class NotEnoughSelected extends AppCompatActivity implements NotEnoughAppListAdapter.ClickedRadio {
    private RecyclerView rvAppList;
    private ProgressBar progressBar;
    private TextView tvCount;
    ArrayList<AppModel> allAppModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_enough_selected);
        SharedPrefUtil.savePref(NotEnoughSelected.this, "gotoaccountPage", "true");


        allAppModels = new ArrayList<>();
        for (int i = 0; i < AppHelper.AllappModels.size(); i++) {
            AppModel appModel = new AppModel();
            appModel.setName((String) ((HashMap) AppHelper.AllappModels.get(i)).get("name"));
            appModel.setDescription((String) ((HashMap) AppHelper.AllappModels.get(i)).get("description"));
            appModel.setIcon((String) ((HashMap) AppHelper.AllappModels.get(i)).get("image"));
            allAppModels.add(appModel);
        }


        rvAppList = findViewById(R.id.rvAppList);
        NotEnoughAppListAdapter selectedAppListAdapter = new NotEnoughAppListAdapter(allAppModels, NotEnoughSelected.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(NotEnoughSelected.this);
        rvAppList.setLayoutManager(linearLayoutManager);
        rvAppList.setAdapter(selectedAppListAdapter);
        TextView tv1 = findViewById(R.id.tv1);

        Typeface faceBold = Typeface.createFromAsset(getAssets(),
                "fonts/mPLUSRounded1cExtraBold.ttf");
        tv1.setTypeface(faceBold);

        progressBar = findViewById(R.id.progress);
        progressBar.setScaleY(5f);
        progressBar.setProgress(AppHelper.selectedappModels.size());
        tvCount = findViewById(R.id.tvCount);
        tvCount.setText(progressBar.getProgress() + "/3");
        final ProgressDialog progressDialog = new ProgressDialog(NotEnoughSelected.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRefUserAppList = database.getReference(SharedPrefUtil.getPref(NotEnoughSelected.this, "phone") + "_selected");

        myRefUserAppList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                String value = dataSnapshot.getValue(String.class);
                if (value == null) {
                    return;
                }
                Log.d("tag", "Value is: " + value);
                try {
                    JSONArray jsonArray = new JSONArray(value);
                    createAppList(jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                progressDialog.dismiss();
            }
        });

    }


    @Override
    public void clickedRadio(AppModel appModel) {
        progressBar.setProgress(progressBar.getProgress() + 1);
        tvCount.setText(progressBar.getProgress() + "/3");

        if (progressBar.getProgress() == 3) {
            Intent intent = new Intent(NotEnoughSelected.this, AccountInfoAfterQuiz.class);
            startActivity(intent);
            finish();
        }
    }

    private void createAppList(JSONArray jsonArray) {
        allAppModels = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            AppModel appModel = new AppModel();
            try {
                appModel.setName((String) (jsonArray.getJSONObject(i)).get("name"));
                appModel.setDescription((String) (jsonArray.getJSONObject(i)).get("description"));
                appModel.setIcon((String) (jsonArray.getJSONObject(i)).get("image"));
                allAppModels.add(appModel);


                SelectedAppListAdapter selectedAppListAdapter = new SelectedAppListAdapter(allAppModels, NotEnoughSelected.this);
                rvAppList.setAdapter(selectedAppListAdapter);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
