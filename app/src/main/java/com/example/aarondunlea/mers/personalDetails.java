package com.example.aarondunlea.mers;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class personalDetails extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewDOB;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private FirebaseAuth firebaseAuth;
  //  private FirebaseAuth.AuthStateListener authListener;

    private String email;
    private EditText firstName;
    private EditText surname;
    private EditText address;

    private TextView textViewUserEmail;

    private String userID;

    private Spinner bloodTypeDropdown;
    ArrayAdapter<CharSequence> adapter;

    private String bloodtype;

    private String DOB;

    private DatabaseReference databaseReference;

    private Button btnPersonalDetails;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);
        textViewDOB = (TextView)findViewById(R.id.textViewDOB);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("PersonalDetails");




        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        textViewUserEmail.setText("Welcome " + user.getEmail());
        // email = user.getEmail();

        btnPersonalDetails = (Button) findViewById(R.id.btnSubmitPersonalInfo);

        firstName = (EditText) findViewById(R.id.editTextPIfName);
        surname = (EditText) findViewById(R.id.editTextPIsurname);
        address = (EditText) findViewById(R.id.editTextPIaddress);
        bloodTypeDropdown = (Spinner)findViewById(R.id.dropdownPIbloodType);
        adapter = ArrayAdapter.createFromResource(this, R.array.bloodtypes,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodTypeDropdown.setAdapter(adapter);
        bloodTypeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                bloodtype = (String) parent.getItemAtPosition(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        btnPersonalDetails.setOnClickListener(this);




        textViewDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        personalDetails.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.BLACK)));
                dialog.show();

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;

                 DOB = dayOfMonth + "/" + month + "/" + year;
                textViewDOB.setText(DOB);
            }
        };
    }

//    protected void onStart(){
//        super.onStart();
//
//        firebaseAuth.addAuthStateListener(authListener);
//    }

    private void saveUserInfo(){
        String firstNameText = firstName.getText().toString().trim();
        String surnameText = surname.getText().toString().trim();
        String addressText = address.getText().toString().trim();

        bloodTypeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                bloodtype = (String) parent.getItemAtPosition(position);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });






        FirebaseUser fbUser = firebaseAuth.getCurrentUser();

        if (fbUser != null){
            
            email = fbUser.getEmail();

            userID = fbUser.getUid();
           // String id = databaseReference.push().getKey();

            Users userInfo = new Users(email, firstNameText,surnameText,addressText,DOB,bloodtype);
            databaseReference.child(userID).setValue(userInfo);


            Toast.makeText(this, "information saved", Toast.LENGTH_SHORT).show();
            moveEmergencyContactDetails();

        }
        else{
            Toast.makeText(this, "fbUser =  null", Toast.LENGTH_SHORT).show();
        }
        

    }


    public void moveEmergencyContactDetails(){
        finish();
        //firebaseAuth.signInWithEmailAndPassword(emailText, passwordText);
        startActivity(new Intent(this, EmergencyContactDetailsActivity.class));

    }

    public void onClick(View view){
        if(view == btnPersonalDetails){
            saveUserInfo();
        }
    }
}
