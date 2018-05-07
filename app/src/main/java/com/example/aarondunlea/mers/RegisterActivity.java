package com.example.aarondunlea.mers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button regSubmit1;
    private Button cancelRegister;
    private EditText password;
    private EditText confirmPassword;
    private EditText email;



    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth =  FirebaseAuth.getInstance() ;
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!= null){
                }
            }
        };

        progressDialog = new ProgressDialog(this);

        //initialize buttons
        regSubmit1 = (Button) findViewById(R.id.regSubmit1);
        //regSubmit1.setEnabled(false);
        cancelRegister = (Button)findViewById(R.id.cancelRegister);

        password = (EditText)findViewById(R.id.password);
        confirmPassword = (EditText)findViewById(R.id.confirmPassword);
        email = (EditText)findViewById(R.id.email);

        regSubmit1.setOnClickListener(this);
        cancelRegister.setOnClickListener(this);
    }

    protected void onStart(){
        super.onStart();

        firebaseAuth.addAuthStateListener(authListener);
    }

    private boolean registerUser(){

//        if(password. )

        final String emailText = email.getText().toString().trim();
        final String passwordText = password.getText().toString().trim();
        String confirmPasswordText = confirmPassword.getText().toString().trim();
        //regSubmit1.setEnabled(false);

        if (TextUtils.isEmpty(emailText)){
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();

            return false;
        }

        if (TextUtils.isEmpty(passwordText)){

            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(confirmPasswordText)){
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
            return false;

        }

        if (!confirmPasswordText.equals(passwordText)){
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if(confirmPasswordText.equals(passwordText)){
            // regSubmit1.setEnabled(true);


            progressDialog.setMessage("Registering user...");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Registration Successful. User Logged in.", Toast.LENGTH_SHORT).show();
                                //call register 112 service activity

                                signIn(emailText,passwordText);

                                progressDialog.dismiss();

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Registration Unsuccessful. Please try again.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

            return true;
        }
        return false;
    }

    public void signIn( String emailText, String passwordText){

        firebaseAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {


                            if(firebaseAuth.getCurrentUser() != null){

                                movePersonalDetails();

                            }
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "User Not logged in", Toast.LENGTH_SHORT).show();
                        }


                    }
                });


    }

    @Override
    public void onClick(View view) {
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        if(view == regSubmit1){
            boolean passMatch = false;

            passMatch = registerUser();
            if (passMatch == true) {


//                firebaseAuth.signOut();
//                firebaseAuth.signInWithEmailAndPassword(emailText,passwordText);
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                Toast.makeText(this, "User = " + user, Toast.LENGTH_SHORT).show();

                if(firebaseAuth.getCurrentUser() != null){

                    finish();
                    //firebaseAuth.signInWithEmailAndPassword(emailText, passwordText);
                    startActivity(new Intent(this, personalDetails.class));

                }



            }
            else{
                Toast.makeText(this, "Please Check Credentials and try again", Toast.LENGTH_SHORT).show();
            }
        }
        else if(view == cancelRegister){
            finish();
            startActivity(new Intent(this, LoginActivity.class));

        }

    }

    public void movePersonalDetails(){
        finish();
        //firebaseAuth.signInWithEmailAndPassword(emailText, passwordText);
        startActivity(new Intent(this, personalDetails.class));

    }

}
