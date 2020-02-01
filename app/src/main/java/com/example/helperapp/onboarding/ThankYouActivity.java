package com.example.helperapp.onboarding;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.helperapp.R;
import com.example.helperapp.models.AppModel;

public class ThankYouActivity extends AppCompatActivity {

    private ImageView image;
    private TextView tv4;
    private TextView tv5;
    private TextView tv1, tv2, tv6;
    private Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);
        image = findViewById(R.id.image);
        tv4 = findViewById(R.id.tv4);
        tv5 = findViewById(R.id.tv5);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv6 = findViewById(R.id.tv6);
        nextBtn = findViewById(R.id.nextBtn);

        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/mPLUSRounded1cMedium.ttf");
        Typeface faceBold = Typeface.createFromAsset(getAssets(),
                "fonts/mPLUSRounded1cExtraBold.ttf");

        tv4.setTypeface(faceBold);
        tv5.setTypeface(face);
        tv1.setTypeface(faceBold);
        tv2.setTypeface(face);
        tv6.setTypeface(faceBold);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Log.e("metrics", "" + metrics.densityDpi);

        if (metrics.density <= 2) {
            tv4.setTextSize(18f);
            tv5.setTextSize(16f);
            tv6.setTextSize(16f);
            image.getLayoutParams().height = 210;
            image.getLayoutParams().width = 210;
            image.requestLayout();
        }


        AppModel appModel = (AppModel) getIntent().getSerializableExtra("appModel");

        tv4.setText(appModel.getName());
        tv5.setText(appModel.getDescription());

        if (appModel.getIcon() != null) {
            Glide.with(ThankYouActivity.this).load(appModel.getIcon()).into(image);

        }

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ThankYouActivity.this, "Hum apko inform kr denge", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
