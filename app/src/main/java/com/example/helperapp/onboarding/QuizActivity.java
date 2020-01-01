package com.example.helperapp.onboarding;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;

import com.example.helperapp.R;
import com.example.helperapp.adapters.QuizCardAdapter;
import com.example.helperapp.models.AppModel;
import com.example.helperapp.utils.AppHelper;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;

public class QuizActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Window window = getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        final CardStackView cardStackView = findViewById(R.id.card_stack_view);
        final CardStackLayoutManager cardStackLayoutManager = new CardStackLayoutManager(QuizActivity.this);
        cardStackView.setLayoutManager(cardStackLayoutManager);



        ArrayList<Object> featuredList = AppHelper.userAppList;
        ArrayList<AppModel> featuredListAppModels = new ArrayList<>();
        for (int i = 0; i < featuredList.size(); i++) {
            AppModel appModel = new AppModel();
            appModel.setName((String) ((HashMap) featuredList.get(i)).get("name"));
            appModel.setDescription((String) ((HashMap) featuredList.get(i)).get("description"));
            appModel.setIcon((String) ((HashMap) featuredList.get(i)).get("image"));
            featuredListAppModels.add(appModel);
        }
        QuizCardAdapter quizCardAdapter = new QuizCardAdapter(featuredListAppModels, QuizActivity.this);
        cardStackView.setAdapter(quizCardAdapter);

        Button like_button = findViewById(R.id.like_button);
        like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Right)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();
                cardStackLayoutManager.setSwipeAnimationSetting(setting);
                cardStackView.swipe();
            }
        });

        Button dislike_button = findViewById(R.id.skip_button);
        dislike_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Left)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();
                cardStackLayoutManager.setSwipeAnimationSetting(setting);
                cardStackView.swipe();
            }
        });

    }
}
