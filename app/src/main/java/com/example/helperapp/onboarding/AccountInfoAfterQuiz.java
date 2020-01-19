package com.example.helperapp.onboarding;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helperapp.R;
import com.example.helperapp.adapters.SelectedAppListAdapter;
import com.example.helperapp.utils.AppHelper;

public class AccountInfoAfterQuiz extends AppCompatActivity {

    private RecyclerView rvAppList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info_after_quiz);
        rvAppList = findViewById(R.id.rvAppList);
        SelectedAppListAdapter selectedAppListAdapter = new SelectedAppListAdapter(AppHelper.selectedappModels, AccountInfoAfterQuiz.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AccountInfoAfterQuiz.this);
        rvAppList.setLayoutManager(linearLayoutManager);
        rvAppList.setAdapter(selectedAppListAdapter);
        TextView tv3 = findViewById(R.id.tv3);
        TextView tv1 = findViewById(R.id.tv1);

        Typeface faceBold = Typeface.createFromAsset(getAssets(),
                "fonts/mPLUSRounded1cExtraBold.ttf");
        tv3.setTypeface(faceBold);
        tv1.setTypeface(faceBold);

        tv3.setText("Aapke seekhne ke liye " + AppHelper.selectedappModels.size() + " items ready hain");


    }
}
