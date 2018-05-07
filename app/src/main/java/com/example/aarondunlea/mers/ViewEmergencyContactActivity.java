package com.example.aarondunlea.mers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewEmergencyContactActivity  extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userID;

    DatabaseReference databaseEmergencyContact;

    private String ecFname, ecSurname, ecphone, ecRelationship;
    private String newFname, newSurname, newPhone, newRelationship;
    private String email;

    private EditText editTextECfirstName, editTextECsurname, editTextECphone, editTextECrelationship;


    private Button btnCancelEC, btnUpdateEC;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((R.layout.activity_view_emergency_contacts));

        btnCancelEC = (Button) findViewById(R.id.btnCancelEC);
        btnUpdateEC = (Button) findViewById(R.id.btnUpdateEC);


        editTextECfirstName = (EditText) findViewById(R.id.editTextECfirstName);
        editTextECsurname = (EditText) findViewById(R.id.editTextECsurname);
        editTextECphone = (EditText) findViewById(R.id.editTextECphone);
        editTextECrelationship = (EditText) findViewById(R.id.editTextECrelationship);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        firebaseDatabase = FirebaseDatabase.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        userID = user.getUid();
        email = user.getEmail();
        databaseEmergencyContact = firebaseDatabase.getReference("EmergencyContacts").child(userID);

        databaseEmergencyContact.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ecFname = dataSnapshot.child("_firstName").getValue(String.class);
                ecSurname = dataSnapshot.child("_surname").getValue(String.class);
                ecphone = dataSnapshot.child("_phone").getValue(String.class);
                ecRelationship = dataSnapshot.child("_relationship").getValue(String.class);
//
                setEditTexts();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnUpdateEC.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                updateEmergencyContact();
            }
        });

        btnCancelEC.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                finish();
                Intent intent = new Intent(ViewEmergencyContactActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setEditTexts() {
        editTextECfirstName.setText(ecFname);
        editTextECsurname.setText(ecSurname);
        editTextECphone.setText(ecphone);
        editTextECrelationship.setText(ecRelationship);
    }

    public void updateEmergencyContact() {
        newFname = editTextECfirstName.getText().toString().trim();
        newSurname = editTextECsurname.getText().toString().trim();
        newPhone = editTextECphone.getText().toString().trim();
        newRelationship = editTextECrelationship.getText().toString().trim();

        EmergencyContact emergencyContactInfo = new EmergencyContact(email, newFname, newSurname, newPhone, newRelationship);

        databaseEmergencyContact.setValue(emergencyContactInfo);
    }

    public void onClick(View v) {
        if (v == btnUpdateEC) {
            updateEmergencyContact();
        }
    }
}
