package com.example.helperapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helperapp.onboarding.AccountInfoAfterQuiz;
import com.example.helperapp.onboarding.NotEnoughSelected;
import com.example.helperapp.onboarding.Onboarding1;
import com.example.helperapp.onboarding.Onboarding3;
import com.example.helperapp.utils.AppHelper;
import com.example.helperapp.utils.SharedPrefUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivityNew extends AppCompatActivity {

    JSONObject jsonObject = new JSONObject();
    private ImageView nextBtn;
    private EditText phoneNumber;
    private AlertDialog alertDialog;
    private Button textRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        nextBtn = findViewById(R.id.nextBtn);
        phoneNumber = findViewById(R.id.phoneNumber);
        final TextView tvError = findViewById(R.id.tvError);
        Window window = getWindow();
        final ProgressDialog progressDialog = new ProgressDialog(MainActivityNew.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);

        textRead = findViewById(R.id.textRead);
        textRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.package.address");
                if (launchIntent != null) {
                    startActivity(launchIntent);//null pointer check in case package name was not found
                }
            }
        });


        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/mPLUSRounded1cMedium.ttf");
        TextView textView = (TextView) findViewById(R.id.tvMain);
        textView.setTypeface(face);


        DisplayMetrics metrics = getResources().getDisplayMetrics();

        Log.e("metrics", "" + metrics.densityDpi);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("featured_app_list");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<Object>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Object>>() {
                };
                ArrayList<Object> value = dataSnapshot.getValue(genericTypeIndicator);
                Log.d("tag", "Value is: " + value);
                AppHelper.featuredList = value;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference myRefApplist = database.getReference("usecase_list");
        myRefApplist.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<Object>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Object>>() {
                };
                ArrayList<Object> value = dataSnapshot.getValue(genericTypeIndicator);
                Log.d("tag", "Value is: " + value);
                //AppHelper.AllappModels = value;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference all_apps_ref = database.getReference("all_aps");
        all_apps_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<Object>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Object>>() {
                };
                ArrayList<Object> value = dataSnapshot.getValue(genericTypeIndicator);
                Log.d("tag", "Value is: " + value);
                AppHelper.AllappModels = value;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference myRefAppStrings = database.getReference("strings");
        myRefAppStrings.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, Object>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                HashMap<String, Object> value = dataSnapshot.getValue(genericTypeIndicator);
                Log.d("tag", "Value is: " + value);
                AppHelper.stringList = value;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //final ProgressDialog dialog = ProgressDialog.show(MainActivityNew.this, "", "Loading. Please wait...", true);

        if (!SharedPrefUtil.getPref(MainActivityNew.this, "phone").equalsIgnoreCase("")) {
            progressDialog.show();

            DatabaseReference myRefUserAppList = database.getReference(SharedPrefUtil.getPref(MainActivityNew.this, "phone"));

            myRefUserAppList.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    GenericTypeIndicator<ArrayList<Object>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Object>>() {
                    };
                    ArrayList<Object> value = dataSnapshot.getValue(genericTypeIndicator);
                    if (value == null) {
                        return;
                    }
                    Log.d("tag", "Value is: " + value);
                    AppHelper.userAppList = value;

                    SharedPrefUtil.savePref(MainActivityNew.this, "phone", phoneNumber.getText().toString());
                    Intent intent = null;


                    if (SharedPrefUtil.getPref(MainActivityNew.this, "gotoOnb3").equalsIgnoreCase("true")) {
                        intent = new Intent(MainActivityNew.this, Onboarding3.class);
                        startActivity(intent);
                        finish();
                        return;
                    }


                    if (SharedPrefUtil.getPref(MainActivityNew.this, "gotoaccountPage").equalsIgnoreCase("true") &&
                            (SharedPrefUtil.getPref(MainActivityNew.this, "totalCountSelected") != null)) {
                        if (Integer.parseInt(SharedPrefUtil.getPref(MainActivityNew.this, "totalCountSelected")) >= 3) {
                            intent = new Intent(MainActivityNew.this, AccountInfoAfterQuiz.class);
                        } else {
                            intent = new Intent(MainActivityNew.this, NotEnoughSelected.class);
                        }
                    } else {

                        intent = new Intent(MainActivityNew.this, Onboarding1.class);
                    }
                    startActivity(intent);
                    finish();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    progressDialog.dismiss();
                }
            });
        }


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvError.setVisibility(View.GONE);


                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if (phoneNumber.getText().length() < 10) {
                    Toast.makeText(MainActivityNew.this, "Sahi phone number dalein", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference myRefUserAppList = database.getReference(phoneNumber.getText().toString());

                myRefUserAppList.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<ArrayList<Object>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Object>>() {
                        };
                        ArrayList<Object> value = dataSnapshot.getValue(genericTypeIndicator);
                        if (value == null) {
                            tvError.setVisibility(View.VISIBLE);
                            return;
                        }
                        Log.d("tag", "Value is: " + value);
                        AppHelper.userAppList = value;

                        SharedPrefUtil.savePref(MainActivityNew.this, "phone", phoneNumber.getText().toString());
                        Intent intent = new Intent(MainActivityNew.this, Onboarding1.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!AppHelper.isNetworkAvailable(MainActivityNew.this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityNew.this);
            builder.setMessage("Apke phone me internet nahi chal raha hai");
            builder.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog = builder.create();
            alertDialog.show();
        }
    }
}
