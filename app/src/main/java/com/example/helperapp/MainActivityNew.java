package com.example.helperapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.helperapp.onboarding.Onboarding1;
import com.example.helperapp.utils.AppHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivityNew extends AppCompatActivity {

    JSONObject jsonObject = new JSONObject();
    private Button nextBtn;
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


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                        Log.d("tag", "Value is: " + value);
                        AppHelper.userAppList = value;

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
