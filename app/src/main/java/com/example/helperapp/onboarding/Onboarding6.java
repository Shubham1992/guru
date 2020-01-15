package com.example.helperapp.onboarding;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helperapp.R;

public class Onboarding6 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding6);
        Window window = getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        final ImageView imageView = findViewById(R.id.counter);


        new Handler().postDelayed(new Runnable() {
            public void run() {


                imageView.setImageResource(R.drawable.two);

                new Handler().postDelayed(new Runnable() {
                    public void run() {

                        imageView.setImageResource(R.drawable.one);

                        new Handler().postDelayed(new Runnable() {
                            public void run() {

                                Intent intent = new Intent();
                                intent.setClass(Onboarding6.this,
                                        QuizActivity.class);

                                startActivity(intent);

                            }
                        }, 1000);
                    }
                }, 1000);
            }
        }, 1000);
    }
}
