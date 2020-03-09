package com.example.switchingshifts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;
import android.app.DatePickerDialog;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import backend.Shift;
import java.util.Date;


public class AddShift extends AppCompatActivity {
    /* private data members */
    private FirebaseAuth firebase_auth;
    private FirebaseFirestore db;
    private TextView date;
    private Button select_date, ok_button;
    private Calendar calendar;
    private DatePickerDialog dpd;
    private Shift shift;
    private Spinner s_shift_type, s_worker_type, s_workers_names;
    private ArrayAdapter<CharSequence> adapter_shift_type, adapter_worker_type;
    private List<String> names = new ArrayList<>();
    private List<String> id_names = new ArrayList<>();
    private String shift_role, shift_type, worker_name, worker_id, shift_id, shift_timestemp, shift_date;
    private ArrayAdapter<String> adapter_workers;
    private Date chosen_date;
    private Timestamp timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shift);
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        /* Initialize Firebase Auth  and firestore*/
        firebase_auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        s_shift_type = findViewById(R.id.spinner_shift_type);
        adapter_shift_type = ArrayAdapter.createFromResource(this,R.array.shift_type,android.R.layout.simple_spinner_item);
        adapter_shift_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_shift_type.setAdapter(adapter_shift_type);
        /*Choosing the shift type- morning or evening */
        s_shift_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("בחר משמרת")) {}
                else {
                    shift_type = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        s_worker_type= findViewById(R.id.spinner_worker_type);
        adapter_worker_type= ArrayAdapter.createFromResource(this,R.array.role_type,android.R.layout.simple_spinner_item);
        adapter_worker_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_worker_type.setAdapter(adapter_worker_type);
        /*Choosing the worker role and create a list of all the workers names with the chosen role */
        s_worker_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("בחר תפקיד")) {}
                else {
                    shift_role = parent.getItemAtPosition(position).toString();
                    db.collection("workers").whereEqualTo("role",shift_role).get()
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
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /*Choosing the worker that you will add him a new shift from the list of worker with the role you selected*/
        names.add("בחר שם");
        s_workers_names = findViewById(R.id.spinner_workers_names);
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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        select_date = findViewById(R.id.button_day);
        date = findViewById(R.id.txt_date);

        /*Choosing the date for the shift*/
        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                dpd=new DatePickerDialog(AddShift.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                        date.setText(mDay + "/" + (mMonth+1) + "/"+ mYear);
                        shift_date = mDay + "." + (mMonth+1) + "." + mYear;
                        calendar.set(Calendar.YEAR, mYear);
                        calendar.set(Calendar.MONTH, mMonth);
                        calendar.set(Calendar.DAY_OF_MONTH, mDay);
                        shift_timestemp = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
                        chosen_date = new Date(shift_timestemp);
                        timestamp = new Timestamp(chosen_date);
                    }
                },year, month, day);
                dpd.getDatePicker().setMinDate(System.currentTimeMillis());
                dpd.show();
            }
        });

        /*When press the ok button we'll check if all the fields are filled correctly
          if so we'll create a new document in the collection "shifts" and add ths data
          else, we'll get am error message*/
        ok_button = findViewById(R.id.button2);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = false;
                if(TextUtils.isEmpty(shift_timestemp)){
                    select_date.setError("חובה למלא שדה זה");
                    flag = true;
                }
                if(TextUtils.isEmpty(shift_type)){
                    ((TextView)s_shift_type.getSelectedView()).setError("חובה למלא שדה זה");
                    flag = true;
                }
                if(TextUtils.isEmpty(shift_role)){
                    ((TextView)s_worker_type.getSelectedView()).setError("חובה למלא שדה זה");
                    flag = true;
                }
                if(TextUtils.isEmpty(worker_name)){
                    ((TextView)s_workers_names.getSelectedView()).setError("חובה למלא שדה זה");
                    flag = true;
                }
                if(!flag){
                    shift = new Shift(timestamp, shift_type, shift_role, false);
                    shift_id = shift_date + shift_type + shift_role;
                    db.collection("workers").document(worker_id).collection("shifts").document(shift_id).set(shift)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getBaseContext(), " נוספה משמרת חדשה ל" + worker_name , Toast.LENGTH_LONG).show();
                            startActivity(new Intent(AddShift.this, AddShift.class));
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

    /*
    When press one of the items in the toolbar we will go to the required screen.
     */
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.my_shift){
            Intent intent = new Intent(AddShift.this, WorkerShifts.class);
            startActivity(intent);
        }
        if(id == R.id.personal_info){
            Intent intent = new Intent(AddShift.this, PersonalDetails.class);
            startActivity(intent);
        }
        if(id == R.id.home_page){
            Intent intent = new Intent(AddShift.this, ManagerScreen.class);
            startActivity(intent);
        }
        if(id == R.id.logout){
            Intent intent = new Intent(AddShift.this, Login.class);
            startActivity(intent);
        }
        return true;
    }
}
