package com.example.helperapp.onboarding;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helperapp.R;
import com.example.helperapp.utils.Constants;
import com.example.helperapp.utils.SharedPrefUtil;
import com.hanks.htextview.base.AnimationListener;
import com.hanks.htextview.base.HTextView;
import com.hanks.htextview.typer.TyperTextView;

public class Onboarding1 extends AppCompatActivity {

    private Button nextBtn;
    private int counter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding1);
        Window window = getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        SharedPrefUtil.savePref(Onboarding1.this, "currentScreen", Constants.ONBOARDING1);

        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Onboarding1.this, Onboarding3.class);
                startActivity(intent);
            }
        });
        final TyperTextView textView = findViewById(R.id.tvMain);

        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/mPLUSRounded1cMedium.ttf");
        textView.setTypeface(face);
        nextBtn.setTypeface(face);

        textView.animateText(getCurrentText());
        counter++;

        textView.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(HTextView hTextView) {

                Log.e("Animation", "END");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (counter > 7) {
                            nextBtn.setVisibility(View.VISIBLE);
                            return;
                        }

                        textView.animateText(getCurrentText());
                        counter++;

                    }
                }, (counter == 6) ? 3500 : 2500);

            }
        });


    }

    String getCurrentText() {
        if (counter == 1) {
            return "Namaste \uD83D\uDE4F\n" +
                    " mei hu Smartphone Guru";
        }
        if (counter == 2) {
            return "Kya aap smartphone chalane mei kabhi kabhi takleef mehsoos karte hai? \uD83D\uDE14";
        }
        if (counter == 3) {
            return "Jaise nayi apps install na kar pana ya unpe account setup na kar pana?";
        }
        if (counter == 4) {
            return "Ya fir online payment karne mei tension hona? \uD83D\uDCB3\uD83D\uDCB0\uD83D\uDECD️";
        }
        if (counter == 5) {
            return "Ghabrayein nahi, smartphones se confused aap akele nahi hai. \n" +
                    "Ye problems kaafi logo ko aati hai.️";
        }
        if (counter == 6) {
            return "Mei aapko in sab problems mei support karunga taaki aap smartphone use kar sake with confidence and independence.️";
        }
        if (counter == 7) {
            return "Isse pehle ki mei apki help kar saku, mei janana chahta hu aap kin cheezo mei interested hai\n.️";
        }
        return "";
    }
}
