package com.example.helperapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helperapp.onboarding.Onboarding1;
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

public class MainActivityNew extends AppCompatActivity {

    JSONObject jsonObject = new JSONObject();
    private ImageView nextBtn;
    private EditText phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        nextBtn = findViewById(R.id.nextBtn);
        phoneNumber = findViewById(R.id.phoneNumber);

        Window window = getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

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
            //dialog.show();

            DatabaseReference myRefUserAppList = database.getReference(SharedPrefUtil.getPref(MainActivityNew.this, "phone"));

            myRefUserAppList.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<ArrayList<Object>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Object>>() {
                    };
                    ArrayList<Object> value = dataSnapshot.getValue(genericTypeIndicator);
                    if (value == null) {
                        Toast.makeText(MainActivityNew.this, "Sahi phone number dalein", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.d("tag", "Value is: " + value);
                    AppHelper.userAppList = value;

                    SharedPrefUtil.savePref(MainActivityNew.this, "phone", phoneNumber.getText().toString());
                    Intent intent = new Intent(MainActivityNew.this, Onboarding1.class);
                    startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {


                }
            });
        }


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                            Toast.makeText(MainActivityNew.this, "Sahi phone number dalein", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.d("tag", "Value is: " + value);
                        AppHelper.userAppList = value;

                        SharedPrefUtil.savePref(MainActivityNew.this, "phone", phoneNumber.getText().toString());
                        Intent intent = new Intent(MainActivityNew.this, Onboarding1.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });


    }
}
