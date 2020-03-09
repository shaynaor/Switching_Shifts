
package com.example.switchingshifts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ChangePass extends AppCompatActivity implements View.OnClickListener {

    private EditText first_pass, second_pass;
    private Button button;
    private String first_pass_input, second_pass_input;
    private FirebaseAuth firebase_auth;
    private FirebaseFirestore db;
    private String user_id;
    private String role;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        /* Initialize Firebase Auth  and firestore*/
        firebase_auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        /* gets the auth unique id */
        user_id = firebase_auth.getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("workers").document(user_id);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                role = documentSnapshot.getString("role");
            }
        });


        first_pass = findViewById(R.id.first_pass);
        second_pass = findViewById(R.id.second_pass);
        button = findViewById(R.id.button);

        button.setOnClickListener(this);

    }
    /* When press the button to change the password we check if they are equal and if the password length is less then 6 characters,
    if they are not equal or less the 6 characters we'll get an error message
    else we'll go to the main screen of the worker. */
    public void onClick(View view){
        if(view.getId() == R.id.button){
            first_pass_input = first_pass.getText().toString().trim();
            second_pass_input = second_pass.getText().toString().trim();

            if(!first_pass_input.equals(second_pass_input)){
                first_pass.setError("הסיסמאות לא שוות");
                second_pass.setError("הסיסמאות לא שוות");
            }
            if (first_pass_input.length() < 6 || second_pass_input.length() < 6){
                first_pass.setError("הסיסמה צריכה להיות באורך של לפחות 6 תווים");
                second_pass.setError("הסיסמה צריכה להיות באורך של לפחות 6 תווים");
            }
            else{
                /* if the password are same- update tha users password */
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.updatePassword(first_pass_input)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(ChangePass.this,"הסיסמה שונתה בהצלחה", Toast.LENGTH_LONG).show();

                                    if(role.equals("Manager")){
                                        startActivity(new Intent(ChangePass.this, ManagerScreen.class));
                                    }
                                    else {
                                        startActivity(new Intent(ChangePass.this, WorkerScreen.class));
                                    }

                                }else {
                                    Toast.makeText(ChangePass.this, task.getException().getMessage() , Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(ChangePass.this, ChangePass.class));

                                }
                            }
                        });


            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /* When press one of the items in the toolbar we will go to the required screen. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.my_shift){
            if(role.equals("Manager")){
                startActivity(new Intent(ChangePass.this, ManagerShifts.class));
            }
            else {
                startActivity(new Intent(ChangePass.this, WorkerShifts.class));
            }
        }
        if(id == R.id.personal_info){
            startActivity(new Intent(ChangePass.this, PersonalDetails.class));
        }
        if(id == R.id.home_page){

            if(role.equals("Manager")){
                startActivity(new Intent(ChangePass.this, ManagerScreen.class));
            }
            else {
                startActivity(new Intent(ChangePass.this, WorkerScreen.class));
            }


        }
        if(id == R.id.logout){
            Intent intent = new Intent(ChangePass.this, Login.class);
            startActivity(intent);
        }
        return true;
    }
}