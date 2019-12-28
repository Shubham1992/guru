package com.example.helperapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.helperapp.models.AppModel;

public class AppDetail extends AppCompatActivity {

    private ImageView image;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);
        image = findViewById(R.id.image);
        textView = findViewById(R.id.name);

        AppModel appModel = (AppModel) getIntent().getSerializableExtra("appModel");
        textView.setText(appModel.getName());
        try {
            image.setImageDrawable(getPackageManager().getApplicationIcon(appModel.getPackageName()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }
}
