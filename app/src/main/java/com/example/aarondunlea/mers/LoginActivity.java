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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button regSubmit2;

    private EditText email;
    private EditText password;
    private TextView registerHereText;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){

            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));

        }

        password = (EditText)findViewById(R.id.password);
        email = (EditText)findViewById(R.id.email);
        registerHereText = (TextView) findViewById(R.id.registerHereText);

        regSubmit2 = (Button)findViewById(R.id.regSubmit2);

        regSubmit2.setOnClickListener(this);
        registerHereText.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

    }

    @Override
    public void onClick(View view) {

        if(view == regSubmit2) {
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();

            if (TextUtils.isEmpty(emailText)) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();

                return;
            }

            if (TextUtils.isEmpty(passwordText)) {

                Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
                return;
            }
            progressDialog.setMessage("Logging in user...");
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));

                                //move on to home page
                            }


                        }
                    });

        }

        else if (view == registerHereText){
            finish();
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        }

    }
}
