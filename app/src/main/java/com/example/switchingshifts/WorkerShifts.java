package com.example.switchingshifts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import backend.Worker;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;import android.widget.ListView;

import static java.util.Objects.*;


public class WorkerShifts extends AppCompatActivity  {

    private TextView shifts_list;
    private String shifts_list_intent = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_shifts);

        shifts_list_intent = getIntent().getStringExtra("shifts_to_show");


        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        shifts_list = findViewById(R.id.my_text_view);
        shifts_list.setMovementMethod(new ScrollingMovementMethod());



       shifts_list.setText(shifts_list_intent);






    }

    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.my_shift){
            startActivity(new Intent(WorkerShifts.this, WorkerShifts.class));
        }
        if(id == R.id.personal_info){
            startActivity(new Intent(WorkerShifts.this, PersonalDetails.class));
        }
        if(id == R.id.home_page){

            startActivity(new Intent(WorkerShifts.this, WorkerScreen.class));

        }
        if(id == R.id.logout){
            Intent intent = new Intent(WorkerShifts.this, Login.class);
            startActivity(intent);
        }
        return true;
    }
}
