package com.example.switchingshifts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ManagerShifts extends AppCompatActivity {
    private FirebaseAuth firebase_auth;
    private FirebaseFirestore db;
    private Spinner s_worker_type;
    private ArrayAdapter<CharSequence> adapter_worker_type;
    private String role, shifts = "";
    private TextView shifts_list;
    private SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy");
    private Calendar calendar;
    private Button display_shift;
    private String current_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_shifts);
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        /* Initialize Firebase Auth  and firestore*/
        firebase_auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        calendar = Calendar.getInstance();
        current_date = sfd.format(calendar.getTime());
        shifts_list = findViewById(R.id.shifts_list);
        display_shift = findViewById(R.id.button_display_shift);

        s_worker_type= findViewById(R.id.spinner_worker_type);
        adapter_worker_type= ArrayAdapter.createFromResource(this,R.array.role_type,android.R.layout.simple_spinner_item);
        adapter_worker_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_worker_type.setAdapter(adapter_worker_type);
        s_worker_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("בחר תפקיד")) {}
                else {
                    role = parent.getItemAtPosition(position).toString();
                    shifts = "";
                    db.collection("workers").whereEqualTo("role", role).get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if(!queryDocumentSnapshots.isEmpty()){
                                        /*getting all the documents where the worker role is what the manager choose*/
                                        List<DocumentSnapshot> list_workers = queryDocumentSnapshots.getDocuments();
                                        for(DocumentSnapshot worker : list_workers){
                                            final String worker_name = worker.getString("first_name") + " " + worker.getString("last_name");
                                            final String worker_id = worker.getId();
                                            db.collection("workers").document(worker.getId()).collection("shifts").orderBy("date").get()
                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            if(!queryDocumentSnapshots.isEmpty()){
                                                                shifts += worker_name + "\n";
                                                                List<DocumentSnapshot> list_shifts = queryDocumentSnapshots.getDocuments();
                                                                /*for each worker we take all the shifts that the date has not passed
                                                                  if the date has passed, we will delete the shift*/
                                                                for(DocumentSnapshot shift : list_shifts){
                                                                    if(shift.exists()) {
                                                                        Date shift_date = shift.getDate("date");
                                                                        if((shift_date.after(calendar.getTime()) || sfd.format(shift_date).equals(current_date)) && shift.get("delete").equals(false)){
                                                                            shifts += sfd.format(shift_date) + " " + shift.get("type") + "\n";
                                                                        }
                                                                        else{
                                                                            db.collection("workers").document(worker_id).collection("shifts").document(shift.getId()).delete();
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    });
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


        /*When press on the display_shift button we'll see list of all the shifts */
        display_shift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shifts_list.setText(shifts);
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
            Intent intent = new Intent(ManagerShifts.this, ManagerShifts.class);
            startActivity(intent);
        }
        if(id == R.id.personal_info){
            Intent intent = new Intent(ManagerShifts.this, PersonalDetails.class);
            startActivity(intent);
        }
        if(id == R.id.home_page){
            Intent intent = new Intent(ManagerShifts.this, ManagerScreen.class);
            startActivity(intent);
        }
        if(id == R.id.logout){
            Intent intent = new Intent(ManagerShifts.this, Login.class);
            startActivity(intent);
        }
        return true;
    }
}
