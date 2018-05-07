package com.example.aarondunlea.mers;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

     private static final String SEND_SMS = Manifest.permission.SEND_SMS;

    private FirebaseAuth firebaseAuth;

    private TextView textViewUserEmail;
    private ImageView buttonLogout,btnEmergencyContact,btnPersonalInfo;

    final int SEND_SMS_PERMISSION_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();

       requestSmsPermission();


        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)

         if (checkPermission(Manifest.permission.SEND_SMS)){
             Toast.makeText(this, "SMS Permission Ok", Toast.LENGTH_SHORT).show();

         }else{
             ActivityCompat.requestPermissions(this,
                     new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
         }


        textViewUserEmail = (TextView) findViewById(R.id.textViewuserEmail);
        textViewUserEmail.setText("Welcome " + user.getEmail());

        buttonLogout = (ImageView)findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener(){
                  public void onClick(View v) {
                      firebaseAuth.signOut();
                      finish();
                     Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                      startActivity(intent);
                  }
              });

        btnEmergencyContact = (ImageView)findViewById(R.id.btnEmergencyContact);
        btnEmergencyContact.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(MainActivity.this, ViewEmergencyContactActivity.class);
                startActivity(intent);
            }
        });

        btnPersonalInfo = (ImageView)findViewById(R.id.btnPersonalInfo);
        btnPersonalInfo.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(MainActivity.this, ViewPersonalInfo.class);
                startActivity(intent);
            }
        });

        if(isServicesOK())
        {
            init();
        }
    }

    private void requestSmsPermission() {
        String permission = Manifest.permission.SEND_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if ( grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    private void init(){
        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 finish();
                 startActivity(new Intent(getApplicationContext(), MapActivity.class));
            }
        });
    }

    public boolean checkPermission(String permission){
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
        
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServices: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS) {
            //everything working fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else {
            Toast.makeText(this, "you cant make app requests", Toast.LENGTH_SHORT).show();
        }
        return false;

    }

    public void onClick(View view){

        if (view == buttonLogout)         {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

}
