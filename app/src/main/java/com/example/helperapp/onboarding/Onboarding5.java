package com.example.helperapp.onboarding;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helperapp.R;

public class Onboarding5 extends AppCompatActivity {

    private Button nextBtn;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding5);
        Window window = getWindow();
        nextBtn = findViewById(R.id.nextBtn);
        TextView tv1 = findViewById(R.id.tv1);
        TextView tv2 = findViewById(R.id.tv2);
        TextView tv3 = findViewById(R.id.tv3);
        ImageView imgView = findViewById(R.id.imgView);

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/mPLUSRounded1cMedium.ttf");
        Typeface faceBold = Typeface.createFromAsset(getAssets(),
                "fonts/mPLUSRounded1cExtraBold.ttf");

        tv1.setTypeface(faceBold);
        nextBtn.setTypeface(faceBold);
        tv2.setTypeface(face);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        image = findViewById(R.id.image);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Log.e("metrics", "" + metrics.densityDpi);

        if (metrics.density <= 2) {
            imgView.getLayoutParams().height = 210;
            imgView.getLayoutParams().width = 210;
            imgView.requestLayout();
            tv1.setTextSize(17f);
            tv2.setTextSize(18f);
            tv3.setTextSize(15f);
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Onboarding5.this, Onboarding6.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
