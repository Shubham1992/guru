package com.example.helperapp.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.helperapp.MainActivityNew;
import com.example.helperapp.R;
import com.example.helperapp.adapters.FeaturedAppListAdapter;
import com.example.helperapp.models.AppModel;
import com.example.helperapp.utils.AppHelper;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Onboarding2 extends AppCompatActivity {

    private RecyclerView rvFeatured;
    private Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding2);
        rvFeatured = findViewById(R.id.rvFeatured);
        ArrayList<Object> featuredList = AppHelper.featuredList;
        ArrayList<AppModel> featuredListAppModels = new ArrayList<>();
        for (int i = 0; i < featuredList.size(); i++) {
            AppModel appModel = new AppModel();
            appModel.setName((String) ((HashMap) featuredList.get(i)).get("name"));
            appModel.setDescription((String) ((HashMap) featuredList.get(i)).get("description"));
            appModel.setIcon((String) ((HashMap) featuredList.get(i)).get("image"));
            featuredListAppModels.add(appModel);
        }

        FeaturedAppListAdapter featuredAppListAdapter
                = new FeaturedAppListAdapter(featuredListAppModels, Onboarding2.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Onboarding2.this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rvFeatured.setLayoutManager(linearLayoutManager);
        rvFeatured.setAdapter(featuredAppListAdapter);
        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Onboarding2.this, Onboarding3.class);
                startActivity(intent);
            }
        });
    }
}
