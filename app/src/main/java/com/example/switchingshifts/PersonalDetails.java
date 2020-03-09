package com.example.switchingshifts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import backend.Worker;

public class PersonalDetails extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    /* private data members */
    private EditText edit_text_first_name, edit_text_last_name, edit_text_email, edit_text_birthday;
    private Button save_button;
    private FirebaseAuth firebase_auth;
    private FirebaseFirestore db;
    private String user_id;
    private String first_name, last_name, email, role;
    private Date birthday;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Initialize Firebase Auth  and firestore*/
        firebase_auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_personal_details);
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        Button button = findViewById(R.id.buttonNewPess);
        /* When press the change password button we will go to the change password screen. */
        button.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View view){
                        startActivity(new Intent(PersonalDetails.this, ChangePass.class));
                    }
                }
        );

        edit_text_first_name = findViewById(R.id.personalName);
        edit_text_last_name = findViewById(R.id.lastlName);
        edit_text_email = findViewById(R.id.mail);
        edit_text_birthday = findViewById(R.id.birthday);
        save_button = findViewById(R.id.saveButton);

        /* disable keyboard */
        edit_text_birthday.setShowSoftInputOnFocus(false);
        edit_text_email.setShowSoftInputOnFocus(false);

        save_button.setOnClickListener(this);



        findViewById(R.id.birthday).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });


        /* gets the current users' id */
        user_id = firebase_auth.getCurrentUser().getUid();

        /* Pulling the personal details from the data base. */
        DocumentReference documentReference = db.collection("workers").document(user_id);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                first_name = documentSnapshot.getString("first_name");
                last_name = documentSnapshot.getString("last_name");
                email = documentSnapshot.getString("email");
                birthday = documentSnapshot.getTimestamp("birthday").toDate();
                role = documentSnapshot.getString("role");

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                /* update the text view */
                edit_text_first_name.setText(first_name);
                edit_text_last_name.setText(last_name);
                edit_text_email.setText(email);
                edit_text_birthday.setText(sdf.format(birthday));
            }
        });



    }
    /* this function open calender dialog when the user try to change his birthday */
    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                );
        datePickerDialog.show();
    }



    /* Checking if all fields are filled correctly */
    @Override
    public void onClick(View view){


        if(view.getId() == R.id.saveButton){
            String f_name = edit_text_first_name.getText().toString().trim();
            String l_name = edit_text_last_name.getText().toString().trim();
            boolean flag = false;
            if(TextUtils.isEmpty(f_name)) {
                edit_text_first_name.setError("חובה למלא שדה זה");
                flag = true;
            }
            if(TextUtils.isEmpty(l_name)) {
                edit_text_last_name.setError("חובה למלא שדה זה");
                flag = true;
            }

            if(!flag){
                first_name = f_name;
                last_name = l_name;
                Worker worker = new Worker(first_name, last_name, role, email);

                worker.setBirthday(new Timestamp(birthday));

                db.collection("workers").document(user_id).set(worker);
                Toast.makeText(PersonalDetails.this,"הנתונים נשמרו בהצלחה", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PersonalDetails.this, WorkerScreen.class));
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /*When press one of the items in the toolbar we will go to the required screen.*/
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.my_shift){
            startActivity(new Intent(PersonalDetails.this, WorkerShifts.class));
        }
        if(id == R.id.personal_info){
            startActivity(new Intent(PersonalDetails.this, PersonalDetails.class));
        }
        if(id == R.id.home_page){
            /* if the current unique id equal to maneger unique id go to manager else worker */
            if(role.equals("Manager")){
                startActivity(new Intent(PersonalDetails.this, ManagerScreen.class));
            }
            else {
                startActivity(new Intent(PersonalDetails.this, WorkerScreen.class));
            }
        }
        if(id == R.id.logout) {
            startActivity (new Intent(PersonalDetails.this, Login.class));

        }
        return true;
    }

    /* when new birthday day set */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = "" + dayOfMonth + "/" + (month+1) + "/" + year;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        try {
            birthday = formatter.parse(date);
            edit_text_birthday.setText(formatter.format(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}