package com.example.helperapp.onboarding;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helperapp.R;
import com.example.helperapp.adapters.QuizCardAdapter;
import com.example.helperapp.models.AppModel;
import com.example.helperapp.utils.AppHelper;
import com.example.helperapp.utils.SharedPrefUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class QuizActivity extends AppCompatActivity {

    private ProgressBar progress;
    private int countProgress = 0;
    private TextView tvCount;
    JSONArray jsonArray = new JSONArray();

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
        cardStackLayoutManager.setCanScrollHorizontal(false);
        cardStackLayoutManager.setCanScrollVertical(false);

        progress = findViewById(R.id.progress);
        tvCount = findViewById(R.id.tvCount);


        final ArrayList<Object> featuredList = AppHelper.userAppList;
        final ArrayList<AppModel> featuredListAppModels = new ArrayList<>();
        for (int i = 0; i < featuredList.size(); i++) {
            AppModel appModel = new AppModel();
            appModel.setName((String) ((HashMap) featuredList.get(i)).get("name"));
            appModel.setDescription((String) ((HashMap) featuredList.get(i)).get("description"));
            appModel.setIcon((String) ((HashMap) featuredList.get(i)).get("image"));
            featuredListAppModels.add(appModel);
        }

        tvCount.setText("" + (countProgress++) + "/" + featuredList.size());


        progress.setMax(featuredList.size());
        progress.setScaleY(5f);

        QuizCardAdapter quizCardAdapter = new QuizCardAdapter(featuredListAppModels, QuizActivity.this);
        cardStackView.setAdapter(quizCardAdapter);

        final ImageView like_button = findViewById(R.id.like_button);
        like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                like_button.startAnimation(AnimationUtils.loadAnimation(QuizActivity.this, R.anim.zoomout));

                SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Right)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();
                cardStackLayoutManager.setSwipeAnimationSetting(setting);
                cardStackView.swipe();

                progress.setProgress(progress.getProgress() + 1);

                tvCount.setText("" + (countProgress) + "/" + featuredList.size());

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", featuredListAppModels.get(countProgress - 1).getName());
                    AppHelper.selectedappModels.add(featuredListAppModels.get(countProgress - 1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(jsonObject);

                if ((countProgress) == featuredListAppModels.size()) {
                    completeQuiz();
                    return;
                }
                countProgress++;

            }
        });

        final ImageView dislike_button = findViewById(R.id.skip_button);
        dislike_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dislike_button.startAnimation(AnimationUtils.loadAnimation(QuizActivity.this, R.anim.zoomout));

                SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Left)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();
                cardStackLayoutManager.setSwipeAnimationSetting(setting);
                cardStackView.swipe();

                progress.setProgress(progress.getProgress() + 1);
                tvCount.setText("" + (countProgress) + "/" + featuredList.size());


                if (countProgress == featuredListAppModels.size() - 1) {
                    completeQuiz();
                }
                countProgress++;

            }
        });

        Typeface faceBold = Typeface.createFromAsset(getAssets(),
                "fonts/mPLUSRounded1cExtraBold.ttf");
        tvCount.setTypeface(faceBold);

    }

    private void completeQuiz() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRefAppStrings = database.getReference(SharedPrefUtil.getPref(QuizActivity.this, "phone") + "_selected");
        myRefAppStrings.setValue(jsonArray.toString());
        myRefAppStrings.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Intent intent = new Intent(QuizActivity.this, AccountInfoAfterQuiz.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
