package com.example.switchingshifts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import backend.DFS;
import backend.Graph;
import backend.Request;
import backend.Shift;
import backend.Vetrex;


/*The worker main screen */
public class WorkerScreen extends AppCompatActivity {
    private FirebaseAuth firebase_auth;
    private FirebaseFirestore db;
    private static final int REQUEST_CALL=1;
    private String user_id, worker_role, shift_reg_selcted, shift_wanted_selcted, shift_reg_id, shift_wanted_id;
    private String request_id, current_id_user, current_id_shift_reg, current_id_shift_wanted, next_id_user, current_date, phone_number;
    private List<String> shifts_reg = new ArrayList<>();
    private List<String> id_shifts_reg = new ArrayList<>();
    private List<String> shifts_wanted = new ArrayList<>();
    private List<String> id_shifts_wanted = new ArrayList<>();
    private List<String> workers_id = new ArrayList();
    private List<String> shifts_to_delete = new ArrayList();
    private Spinner s_shift_reg, s_shift_wanted;
    private ArrayAdapter<CharSequence> adapter_shift_reg, adapter_shift_wanted;

    private Graph graph;
    private DFS dfs;
    private Vetrex v_worker_id, v_wanted_shift, v_reg_shift;
    private Shift new_shift;
    private int size, num_of_requests;
    private Button ok_button;
    private Request request;
    private Stack<Vetrex> path;
    SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy");
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_screen);
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        graph = new Graph();


        /* Initialize Firebase Auth  and firestore*/
        firebase_auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user_id = firebase_auth.getCurrentUser().getUid();

        calendar = Calendar.getInstance();
        current_date = sfd.format(calendar.getTime());

        db.collection("workers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult()) {
                            db.collection("workers").document(doc.getId())
                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.getString("role").equals("Manager"))
                                        phone_number=documentSnapshot.getString("phone_number");
                                }
                            });
                        }
                    }
                }
        });
        ImageView imageCall = findViewById(R.id.image_call);

        imageCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });


        final TextView textViewToChange = findViewById(R.id.Worker_Screen_title);

        final DocumentReference documentReference = db.collection("workers").document(user_id);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                worker_role = documentSnapshot.getString("role");
                textViewToChange.setText(
                        "Hello " + documentSnapshot.get("first_name"));
            }
        });

        db.collection("workers").document(user_id).collection("shifts").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            shifts_reg.clear();
                            id_shifts_reg.clear();
                            shifts_reg.add("");
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                Date shift_date = d.getDate("date");
                                if(shift_date.after(calendar.getTime()) && d.get("delete").equals(false)){
                                    id_shifts_reg.add(d.getId());
                                    shifts_reg.add(sfd.format(shift_date) + "  " + d.getString("type"));
                                }
                                else if(d.get("delete").equals(true) && !sfd.format(shift_date).equals(current_date)){
                                    db.collection("workers").document(user_id).collection("shifts")
                                            .document(d.getId()).delete();
                                }
                            }
                        }
                    }
                });
        shifts_reg.add("");
        s_shift_reg = findViewById(R.id.spinner_shifts_reg);
        adapter_shift_reg = new ArrayAdapter(this, android.R.layout.simple_spinner_item, shifts_reg);
        adapter_shift_reg.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_shift_reg.setAdapter(adapter_shift_reg);

        db.collection("workers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        shifts_wanted.clear();
                        id_shifts_wanted.clear();
                        shifts_wanted.add("");
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                if(!doc.getId().equals(user_id)) {
                                    final String name = doc.getString("first_name");
                                    final String id = doc.getId();
                                db.collection("workers").document(doc.getId()).collection("shifts").get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            //add Equal to role
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                if (!queryDocumentSnapshots.isEmpty()) {
                                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                                    for (DocumentSnapshot d : list) {
                                                        if (!shifts_reg.contains(d.getId())) {
                                                            if(d.getString("role").equals(worker_role)){
                                                                Date shift_date = d.getDate("date");
                                                                if(shift_date.after(calendar.getTime()) && d.get("delete").equals(false)){
                                                                    id_shifts_wanted.add(d.getId());
                                                                    shifts_wanted.add(sfd.format(shift_date) + "  " + d.getString("type") + " -" + name);
                                                                }
                                                                else if(d.get("delete").equals(true) && !sfd.format(shift_date).equals(current_date)) {
                                                                    db.collection("workers").document(id).collection("shifts")
                                                                            .document(d.getId()).delete();
                                                                }
                                                            }

                                                        }
                                                    }

                                                }
                                            }

                                        });
                                }
                            }
                        }

                    }
                });


        shifts_wanted.add("");
        s_shift_wanted = findViewById(R.id.spinner_shifts_wanted);
        adapter_shift_wanted = new ArrayAdapter(this, android.R.layout.simple_spinner_item, shifts_wanted);
        adapter_shift_wanted.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_shift_wanted.setAdapter(adapter_shift_wanted);

        s_shift_reg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                shift_reg_selcted = parent.getItemAtPosition(pos).toString();
                shift_reg_id = id_shifts_reg.get(pos);

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        s_shift_reg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (!parentView.getItemAtPosition(position).equals("")) {
                    shift_reg_selcted = parentView.getItemAtPosition(position).toString();
                    shift_reg_id = id_shifts_reg.get(position - 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        s_shift_wanted.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (!parentView.getItemAtPosition(position).equals("")) {
                    shift_wanted_selcted = parentView.getItemAtPosition(position).toString();
                    shift_wanted_id = id_shifts_wanted.get(position - 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });


        ok_button = findViewById(R.id.button_ok_worker_screen);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = false;
                if (TextUtils.isEmpty(shift_reg_selcted)) {
                    ((TextView) s_shift_reg.getSelectedView()).setError("חובה למלא שדה זה");
                    flag = true;
                }
                if (TextUtils.isEmpty(shift_wanted_selcted)) {
                    ((TextView) s_shift_wanted.getSelectedView()).setError("חובה למלא שדה זה");
                    flag = true;
                }
                if (!flag) {
                    request = new Request(shift_reg_id, shift_wanted_id, user_id);
                    request_id = shift_reg_id + "_" + shift_wanted_id;
                    db.collection("workers").document(user_id).collection("requests").document(request_id).set(request)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getBaseContext(), " בקשתך לחילוף התקבלה", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(WorkerScreen.this, WorkerScreen.class));
                                    read_requests_from_data();
                                }
                            });
                }


            }
        });


    }

    private void read_requests_from_data() {

        db.collection("workers").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                          @Override
                                          public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                              num_of_requests = 0;
                                              if (!queryDocumentSnapshots.isEmpty()) {
                                                  List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                                                  for (DocumentSnapshot doc : docs) {
                                                      db.collection("workers").document(doc.getId()).collection("requests").get()
                                                              .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                  //add Equal to role
                                                                  @Override
                                                                  public void onSuccess(QuerySnapshot queryDocumentSnapshots_in) {
                                                                      if (!queryDocumentSnapshots_in.isEmpty()) {
                                                                          List<DocumentSnapshot> list = queryDocumentSnapshots_in.getDocuments();
                                                                          for (DocumentSnapshot d : list) {
                                                                              v_worker_id = new Vetrex(true, d.getString("worker_id"));
                                                                              v_reg_shift = new Vetrex(false, d.getString("shift_reg_id"));
                                                                              v_wanted_shift = new Vetrex(false, d.getString("shift_wanted_id"));
                                                                              graph.add_edge(v_reg_shift, v_worker_id, v_wanted_shift);
                                                                              size = graph.graph_size();
                                                                              num_of_requests++;
                                                                              if (num_of_requests > 1){
                                                                                  start_dfs();
                                                                              }
                                                                          }
                                                                      }
                                                                  }
                                                              });
                                                  }
                                              }
                                          }
                                      }
                );
    }

    public void start_dfs() {
        dfs = new DFS(graph);
        boolean has_cycle = true;
        while (has_cycle) {
            path = dfs.dfsCycle();
            if (path.empty()) {
                has_cycle = false;
            } else {
                int count = 0;
                for (int i = 0; i < path.size(); i++) {
                    if (path.get(i).isIs_user())
                        count++;
                }
                while (count > 0) {
                    Vetrex current_shift = path.pop();
                    if (current_shift.isIs_user()) {
                        path.add(0, current_shift);
                        current_shift = path.pop();
                    }

                    current_id_shift_reg = current_shift.getId();
                    Vetrex user = path.pop();
                    Vetrex next_shift = path.pop();
                    current_id_user = user.getId();
                    current_id_shift_wanted = next_shift.getId();
                    next_id_user = path.peek().getId();
                    shifts_to_delete.add(current_id_shift_reg);
                    workers_id.add(current_id_user);
                    String current_user_request = current_id_shift_reg + "_" + current_id_shift_wanted;

                    switch_shift(next_id_user, current_id_user, current_id_shift_wanted);
                    db.collection("workers").document(current_id_user).collection("shifts").document(current_id_shift_reg)
                            .update("delete", true);


                    path.add(0, current_shift);
                    path.add(0, user);
                    path.push(next_shift);
                    graph.remove_edge(current_shift, user);
                    graph.remove_edge(user, next_shift);
                    graph.add_edge(next_shift, user);

                    db.collection("workers").document(current_id_user).collection("requests").document(current_user_request).delete();
                    count--;
                }
            }
        }
    }

    public void switch_shift(String next_id_user, final String current_id_user, final String current_id_shift_wanted){

        db.collection("workers").document(next_id_user).collection("shifts").document(current_id_shift_wanted).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot docSnapshot) {
                        new_shift = new Shift(docSnapshot.getTimestamp("date"), docSnapshot.getString("type"), docSnapshot.getString("role"), false);
                        db.collection("workers").document(current_id_user).collection("shifts")
                                .document(current_id_shift_wanted).set(new_shift).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getBaseContext(), " התבצע חילוף", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
    }

    private void makePhoneCall() {

            if (ContextCompat.checkSelfPermission(WorkerScreen.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(WorkerScreen.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + phone_number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /* When press one of the items in the toolbar we will go to the required screen. */
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.my_shift) {
            Intent intent = new Intent(WorkerScreen.this, WorkerShifts.class);
            String shifts_to_show = "";

            Collections.sort(shifts_reg);

            for(String s: shifts_reg) {
                shifts_to_show = shifts_to_show + s + "\n";
            }
            intent.putExtra("shifts_to_show",  shifts_to_show);
            startActivity(intent);
        }
        if (id == R.id.personal_info) {
            Intent intent = new Intent(WorkerScreen.this, PersonalDetails.class);
            startActivity(intent);
        }
        if (id == R.id.home_page) {
            Intent intent = new Intent(WorkerScreen.this, WorkerScreen.class);
            startActivity(intent);
        }
        if (id == R.id.logout) {
            Intent intent = new Intent(WorkerScreen.this, Login.class);
            startActivity(intent);
        }
        return true;
    }
}
