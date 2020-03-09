package com.example.switchingshifts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import backend.Worker;


public class AddWorker extends AppCompatActivity {
    /* private data members */
    private String first_name, last_name, email, role, password, user_id;
    private EditText text_input_email, text_input_first_name, text_input_last_name;
    private Button ok;
    private Spinner s_worker_type;
    private ArrayAdapter<CharSequence> adapter_worker_type;
    private Worker worker;
    private FirebaseAuth firebase_auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_worker);
        /* Initialize Firebase Auth  and firestore*/
        firebase_auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        s_worker_type = findViewById(R.id.spinner_worker_type);
        adapter_worker_type = ArrayAdapter.createFromResource(this, R.array.role_type, android.R.layout.simple_spinner_item);
        adapter_worker_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_worker_type.setAdapter(adapter_worker_type);
        /*Choosing the role for ths new worker */
        s_worker_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("בחר תפקיד")) {
                } else {
                    role = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        text_input_first_name = findViewById(R.id.Etext_first_name);
        text_input_last_name = findViewById(R.id.Etext_last_name);
        text_input_email = findViewById(R.id.Etext_mail);
        ok = findViewById(R.id.button_ok);
        /*When press the ok button we'll check if all the fields are filled correctly
          if so we'll create a new worker and add ths data
          else, we'll get am error message*/
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                first_name = text_input_first_name.getText().toString().trim();
                last_name = text_input_last_name.getText().toString().trim();
                email = text_input_email.getText().toString().trim();
                password = new String(email);
                boolean flag = false;

                if(TextUtils.isEmpty(first_name)){
                    text_input_first_name.setError("חובה למלא שדה זה");
                    flag = true;
                }
                if(TextUtils.isEmpty(last_name)){
                    text_input_last_name.setError("חובה למלא שדה זה");
                    flag = true;
                }
                if(TextUtils.isEmpty(email)){
                    text_input_email.setError("חובה למלא שדה זה");
                    flag = true;
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    text_input_email.setError("כתובת המייל לא תקינה");
                    flag = true;
                }
                if(TextUtils.isEmpty(role)){
                    ((TextView)s_worker_type.getSelectedView()).setError("חובה למלא שדה זה");
                    flag = true;
                }

                if(!flag){

                    /* register the worker to firebase */
                    firebase_auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        user_id = firebase_auth.getCurrentUser().getUid();
                                        worker = new Worker(first_name, last_name, role, email);
                                        db.collection("workers").document(user_id).set(worker).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(AddWorker.this, " נוסף בהצלחה למערכת " + worker.getFirst_name() + " " + worker.getLast_name(), Toast.LENGTH_LONG).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(AddWorker.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });


                                        startActivity(new Intent(getApplicationContext(), ManagerScreen.class));
                                    }else{
                                        Toast.makeText(AddWorker.this, task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }




            }
        });


    }


    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    /* When press one of the items in the toolbar we will go to the required screen. */
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.my_shift){
            Intent intent = new Intent(AddWorker.this, WorkerShifts.class);
            startActivity(intent);
        }
        if(id == R.id.personal_info){
            Intent intent = new Intent(AddWorker.this, PersonalDetails.class);
            startActivity(intent);
        }
        if(id == R.id.home_page){
            Intent intent = new Intent(AddWorker.this, ManagerScreen.class);
            startActivity(intent);
        }
        if(id == R.id.logout) {
            Intent intent = new Intent(AddWorker.this, Login.class);
            startActivity(intent);
        }
        return true;
    }
}