package com.example.aarondunlea.mers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmergencyContactDetailsActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btnEmerConDetails;

    private EditText emerConFirstName;
    private EditText emerConSurname;
    private EditText emerConPhone;

    private TextView textViewUserEmail;
    private String email;

    private String userID;


    private EditText emerConRelationsip;

    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact_details);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        userID = user.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("EmergencyContacts");

        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        textViewUserEmail.setText("Welcome " + user.getEmail());


        btnEmerConDetails = (Button) findViewById(R.id.btnSubmitPersonalInfo);

        emerConFirstName = (EditText) findViewById(R.id.editTextPIfName);
        emerConSurname = (EditText) findViewById(R.id.editTextPIsurname);
        emerConPhone = (EditText) findViewById(R.id.emerConPhone);
        emerConRelationsip = (EditText)findViewById(R.id.emerConRelationship);

        btnEmerConDetails.setOnClickListener(this);


    }

    public void registerEmergencyContact() {


        String ecFirstName = emerConFirstName.getText().toString().trim();
        String ecSurname = emerConSurname.getText().toString().trim();
        String ecPhoneNumber = emerConPhone.getText().toString().trim();

        String ecRelationship = emerConRelationsip.getText().toString().trim();

        if (emerConFirstName.getText().toString().matches("")) {
            Toast.makeText(this, "Please enter a valid first name ", Toast.LENGTH_SHORT).show();

            return;
        }

        if (emerConSurname.getText().toString().matches("")) {
            Toast.makeText(this, "Please enter a valid surname", Toast.LENGTH_SHORT).show();

            return;
        }

        if (emerConPhone.getText().toString().matches("")) {
            Toast.makeText(this, "Please enter a valid phone number ", Toast.LENGTH_SHORT).show();

            return;
        }

        if (emerConRelationsip.getText().toString().matches("")) {
            Toast.makeText(this, "Please enter a relationship", Toast.LENGTH_SHORT).show();

            return;
        }


        FirebaseUser fbUser = firebaseAuth.getCurrentUser();

        if (fbUser != null) {

            email = fbUser.getEmail();
            EmergencyContact emergencyContactInfo = new EmergencyContact(email,ecFirstName, ecSurname, ecPhoneNumber, ecRelationship);
            databaseReference.child(fbUser.getUid()).setValue(emergencyContactInfo);

            progressDialog.setMessage("Saving Information...");
            Toast.makeText(this, "information saved", Toast.LENGTH_SHORT).show();
            moveMainActivity();

        } else {
            Toast.makeText(this, "fbUser =  null", Toast.LENGTH_SHORT).show();
        }
        progressDialog.setMessage("Saving Emergency Contact... ");
        progressDialog.show();

    }



    public void moveMainActivity(){
        finish();
        //firebaseAuth.signInWithEmailAndPassword(emailText, passwordText);
        startActivity(new Intent(this, MainActivity.class));

    }

    @Override
    public void onClick(View v) {
        if(v == btnEmerConDetails){
            registerEmergencyContact();
        }
    }
}