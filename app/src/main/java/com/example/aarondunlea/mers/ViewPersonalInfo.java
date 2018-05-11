package com.example.aarondunlea.mers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewPersonalInfo extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private String userID;

    DatabaseReference databasePersonalInfo;

    private String piFname, piSurname, piAddress, piDateOfBirth, piBloodType;
    private String newPIFname, newPISurname, newPIAddress, newPIBloodType;
    private String email;

    private EditText editTextPIfName, editTextPIsurname, editTextPIaddress;
    private TextView textViewPIdateOfBirth;
    private Spinner dropdownPIbloodType;
    ArrayAdapter<CharSequence> adapter;


    private Button btnCancelPersonalInfo, btnSubmitPersonalInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((R.layout.activity_view_personal_info));

        btnCancelPersonalInfo = (Button) findViewById(R.id.btnCancelPersonalInfo);
        btnSubmitPersonalInfo = (Button) findViewById(R.id.btnSubmitPersonalInfo);


        editTextPIfName = (EditText) findViewById(R.id.editTextPIfName);
        editTextPIsurname = (EditText) findViewById(R.id.editTextPIsurname);
        editTextPIaddress = (EditText) findViewById(R.id.editTextPIaddress);

        textViewPIdateOfBirth = (TextView)findViewById(R.id.textViewPIdateOfBirth);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        firebaseDatabase = FirebaseDatabase.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        userID = user.getUid();
        email = user.getEmail();
        databasePersonalInfo = firebaseDatabase.getReference("PersonalDetails").child(userID);

        dropdownPIbloodType = (Spinner)findViewById(R.id.dropdownPIbloodType);
        adapter = ArrayAdapter.createFromResource(this, R.array.bloodtypes,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);





        databasePersonalInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                piFname = dataSnapshot.child("_firstName").getValue(String.class);
                piSurname = dataSnapshot.child("_surname").getValue(String.class);
                piAddress = dataSnapshot.child("_address").getValue(String.class);
                piBloodType = dataSnapshot.child("_bloodType").getValue(String.class);
                piDateOfBirth = dataSnapshot.child("_dob").getValue(String.class);

                setVariables();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnSubmitPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePersonalInfo();
            }
        });

        btnCancelPersonalInfo.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                finish();
                Intent intent = new Intent(ViewPersonalInfo.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setVariables() {
        editTextPIfName.setText(piFname);
        editTextPIsurname.setText(piSurname);
        editTextPIaddress.setText(piAddress);

        textViewPIdateOfBirth.setText(piDateOfBirth);
        dropdownPIbloodType.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(piBloodType);
        dropdownPIbloodType.setSelection(spinnerPosition);

    }

    public void updatePersonalInfo() {
        newPIFname = editTextPIfName.getText().toString().trim();
        newPISurname = editTextPIsurname.getText().toString().trim();
        newPIAddress = editTextPIaddress.getText().toString().trim();
        newPIBloodType = dropdownPIbloodType.getSelectedItem().toString();

        Users userInfo = new Users(email, newPIFname,newPISurname,newPIAddress,piDateOfBirth,newPIBloodType);

        databasePersonalInfo.setValue(userInfo);
        Toast.makeText(this, "Information Updated", Toast.LENGTH_SHORT).show();

    }


}
