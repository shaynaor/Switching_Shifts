package com.example.switchingshifts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RemoveWorker extends AppCompatActivity {
    private FirebaseAuth firebase_auth;
    private FirebaseFirestore db;
    private Spinner s_workers_names, s_worker_type;
    private ArrayAdapter<CharSequence> adapter_workers, adapter_worker_type;
    private String worker_role, worker_name, worker_id, user_id, worker_birthday, worker_mail, worker_last_name;
    private List<String> names = new ArrayList<>();
    private List<String> id_names = new ArrayList<>();
    private Button ok_button;
    private CheckBox checkBox;
    private TextView worker_details;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_worker);
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        /* Initialize Firebase Auth  and firestore*/
        firebase_auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        s_worker_type= findViewById(R.id.spinner_worker_type);
        adapter_worker_type= ArrayAdapter.createFromResource(this,R.array.role_type,android.R.layout.simple_spinner_item);
        adapter_worker_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_worker_type.setAdapter(adapter_worker_type);
        s_worker_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("בחר תפקיד")) {}
                else {
                    worker_role = parent.getItemAtPosition(position).toString();
                    db.collection("workers").whereEqualTo("role",worker_role).get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if(!queryDocumentSnapshots.isEmpty()){
                                        names.clear();
                                        id_names.clear();
                                        names.add("בחר שם");
                                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                        for(DocumentSnapshot d : list){
                                            id_names.add(d.getId());
                                            names.add(d.getString("first_name"));
                                        }
                                    }
                                }
                            });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        worker_details = findViewById(R.id.text_worker_details);
        names.add("בחר שם");
        s_workers_names = findViewById(R.id.spinner_workers_name);
        adapter_workers = new ArrayAdapter(this, android.R.layout.simple_spinner_item, names);
        adapter_workers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_workers_names.setAdapter(adapter_workers);
        s_workers_names.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("בחר שם")){}
                else {
                    worker_name = parent.getItemAtPosition(position).toString();
                    worker_id = id_names.get(position-1);
                    DocumentReference documentReference = db.collection("workers").document(worker_id);
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Date birthday = documentSnapshot.getTimestamp("birthday").toDate();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            worker_birthday = sdf.format(birthday);
                            worker_last_name = documentSnapshot.get("last_name").toString();
                            worker_mail = documentSnapshot.get("email").toString();

                            worker_details.setText("First name: " + worker_name + "\n" + "Last name: " + worker_last_name
                                    + "\n" + "Mail: " + worker_mail + "\n" + "Birthday: " + worker_birthday + "\n" + "Role: " + worker_role);
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        checkBox = findViewById(R.id.delete_for_sure);
        ok_button = findViewById(R.id.button2);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = false;
                if(TextUtils.isEmpty(worker_role)){
                    ((TextView)s_worker_type.getSelectedView()).setError("חובה למלא שדה זה");
                    flag = true;
                }
                if(TextUtils.isEmpty(worker_name)){
                    ((TextView)s_workers_names.getSelectedView()).setError("חובה למלא שדה זה");
                    flag = true;
                }
                if(!checkBox.isChecked()){
                    checkBox.setError("חובה למלא שדה זה");
                    flag = true;
                }
                if(!flag){

                    db.collection("workers").document(worker_id).delete();

                    /* write a toasts to the screen and go to manager screen */
                    Toast.makeText(RemoveWorker.this, " נמחק בהצלחה " + worker_name + " " + worker_last_name, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RemoveWorker.this, ManagerScreen.class));


                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.my_shift){
            Intent intent = new Intent(RemoveWorker.this, WorkerShifts.class);
            startActivity(intent);
        }
        if(id == R.id.personal_info){
            Intent intent = new Intent(RemoveWorker.this, PersonalDetails.class);
            startActivity(intent);
        }
        if(id == R.id.home_page){
            Intent intent = new Intent(RemoveWorker.this, ManagerScreen.class);
            startActivity(intent);
        }
        if(id == R.id.logout) {
            Intent intent = new Intent(RemoveWorker.this, Login.class);
            startActivity(intent);
        }
        return true;
    }
}
