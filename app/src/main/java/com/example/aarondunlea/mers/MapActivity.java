package com.example.aarondunlea.mers;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.LocaleList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {

    private static final String TAG = "MapActivity";
    //Location variables
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    //map variables
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location lastKnownLocation;
    private Location crashLocation;
    private Location comparisonLocation1, comparisonLocation2;
    private Location[] locations = new Location[15];

    //sensor variables
    private Sensor mySensor;
    private SensorManager SM;

    private float xRead, yRead, zRead;
    private float maxX, maxY, maxZ;
    private float accelVal;
    private float accelLast;
    private float shake, maxShake;
    private float cancelDistance;

    //countdown timer variables
    private CountDownTimer cdt1, cdt2;
    private int counter = 30;
    private int counter2 = 30;
    private boolean timerRunning;
    private boolean isOk;

    float[] distanceBetweenReadings = new float[1];
    float[] speeds = new float[15];

    private Button btnStopTracking;

    private String ecContactNumber;
    private String firstName;
    private String surname;

    //firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userID;
    private String email;

    private DatabaseReference databasePossibleCrash;


    private ListView mListView;

    private double lat1, lat2;
    private double lng1, lng2;

    private int arrayCounter = 0;


    DatabaseReference databaseEmergencyContact;
    DatabaseReference databasePersonalInfo;


    //SMS variables
    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;

    private Uri notification;
    private Ringtone r;

    private TextView xText, yText;

    private boolean sensorDelay = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((R.layout.activity_map));

        //firebase auth instance


        //get location permissions from user
        getLocationPermission();

        btnStopTracking = (Button) findViewById(R.id.btnStopTracking);

        btnStopTracking.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                finish();
                Intent intent = new Intent(MapActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        //Assign textviews
        //xText = (TextView)findViewById(R.id.xText);
        // yText = (TextView)findViewById(R.id.yText) ;


        //create Sensor Manager
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Accelerometer Sensor
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Register Sensor Listener
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

        accelVal = SensorManager.GRAVITY_EARTH;
        accelLast = SensorManager.GRAVITY_EARTH;
        shake = 0.00f;

        //SMS
        sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);


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
        databaseEmergencyContact = firebaseDatabase.getReference("EmergencyContacts").child(userID);

        databasePersonalInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                firstName = dataSnapshot.child("_firstName").getValue(String.class);
                surname = dataSnapshot.child("_surname").getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseEmergencyContact.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ecContactNumber = dataSnapshot.child("_phone").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void onMapReady(GoogleMap googleMap) {


        Log.d(TAG, "onMapReady: Map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocationForMap();


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (sensorDelay == false) {
            xRead = event.values[0];
            yRead = event.values[1];
            zRead = event.values[2];


            accelLast = accelVal;
            accelVal = (float) Math.sqrt((double) (xRead * xRead + yRead * yRead + zRead * zRead));

            shake = accelVal - accelLast;

            //  yText.setText("Shake Reading = " + shake);
            if (shake > 60) {
                sensorDelay = true;
               // Toast.makeText(this, "Shake = " + shake, Toast.LENGTH_SHORT).show();
                possibleCrash(shake);
                shake = 0.00f;
            }
        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //not in use
    }

    public void possibleCrash(float shakeAtReading) {


        counter = 30;
        Toast.makeText(this, "Possible crash - Checking location 1", Toast.LENGTH_SHORT).show();
        crashLocation = getDeviceLocation();
        lat1 = crashLocation.getLatitude();
        lng1 = crashLocation.getLongitude();


        cdt1 = new CountDownTimer(30000, 1000) {
            public void onTick(long millsUntilFinished) {
                if (counter != 0) {
                    counter--;

//                        if(arrayCounter < 15 &&arrayCounter >0) {
//                            locations[arrayCounter] = getDeviceLocation();
//                            float[] speedDistances = new float[1];
//                            Location.distanceBetween(locations[arrayCounter-1].getLatitude(),locations[arrayCounter-1].getLongitude(),locations[arrayCounter].getLatitude(),locations[arrayCounter].getLongitude(),speedDistances);
//                            speeds[arrayCounter-1] = speedDistances[0]/2;
//
//                        }
//                        if (arrayCounter == 0){
//                            locations[arrayCounter] = crashLocation;
//                        }
                    //  arrayCounter++;

                }


                if (counter == 10) {
                    Toast.makeText(MapActivity.this, "Checking location 2", Toast.LENGTH_SHORT).show();
                    comparisonLocation2 = getDeviceLocation();
                    lastKnownLocation = comparisonLocation2;
                    lat2 = comparisonLocation2.getLatitude();
                    lng2 = comparisonLocation2.getLongitude();
//                        float averageSpeed = 0;
//                        for(int i = 0;i<=14; i++){
//                            averageSpeed = averageSpeed + speeds[i];
//                        }
//                        averageSpeed = averageSpeed/14;
                }

                if (counter == 5) {
                    Location.distanceBetween(lat1, lng1, lat2, lng2, distanceBetweenReadings);
                    Toast.makeText(MapActivity.this, "Comparing locations", Toast.LENGTH_SHORT).show();
                    // cancelDistance = averageSpeed *

                    if (distanceBetweenReadings[0] < 30.0) {
                        Toast.makeText(MapActivity.this, "Acivating crash mode difference = " + distanceBetweenReadings[0], Toast.LENGTH_SHORT).show();
                        crashMode();
                        shake = 0.00f;
                        counter = 30;
                        cdt1.cancel();
                        return;
                    } else {
                        Toast.makeText(MapActivity.this, "Cancelling crash mode difference = " + distanceBetweenReadings[0], Toast.LENGTH_SHORT).show();
                        String status = "Cancelled - Out Of Range";
                        updatePossibleCrash(status, shake);
                        cdt1.cancel();
                        counter = 30;
                        shake = 0.00f;
                        sensorDelay = false;
                        return;
                    }

                }


            }

            @Override
            public void onFinish() {
                Toast.makeText(MapActivity.this, "Entering Crash Mode", Toast.LENGTH_SHORT).show();
                crashMode();


            }

        }.start();


    }

    public void crashMode() {

        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();

        cdt2 = new CountDownTimer(30000, 1000) {
            public void onTick(long millsUntilFinished) {

                if (counter2 != 0) {
                    counter2--;
                }
                if (counter2 == 1) {
                    cdt2.cancel();
                    //  r.stop();
                    String status = "Registered Crash - Timer Ran Out";
                    updatePossibleCrash(status, shake);

                    shake = 0.00f;
                    sendSMS(lastKnownLocation);
                    sensorDelay = false;

                }

            }

            @Override
            public void onFinish() {
                String status = "Registered Crash - Timer Ran Out";
                updatePossibleCrash(status, shake);
                shake = 0.00f;
                sendSMS(lastKnownLocation);
                sensorDelay = false;


            }
        }.start();


        AlertDialog.Builder a_builder = new AlertDialog.Builder(MapActivity.this);
        a_builder.setMessage("Are you OK?\nIf no response is received in 30 seconds an emergency SMS will be sent out").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                String status = "Cancelled - User Selected OK";
                updatePossibleCrash(status, shake);
                cdt2.cancel();
                r.stop();
                counter = 30;
                shake = 0.00f;
                sensorDelay = false;


            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MapActivity.this, "Entering Crash Mode", Toast.LENGTH_SHORT).show();
                        String status = "Registered Crash - User Selected Not OK";
                        updatePossibleCrash(status, shake);
                        dialog.cancel();
                        r.stop();
                        sendSMS(lastKnownLocation);
                        cdt2.cancel();
                        counter2 = 30;
                        shake = 0.00f;
                        sensorDelay = false;


                    }
                });

        final AlertDialog alert = a_builder.create();
        alert.setTitle("Possible Crash Detected");

        if(!(MapActivity.this).isFinishing())
        {
            alert.show();
        }





        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alert.isShowing()) {
                    alert.cancel();
                }
            }
        };

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, 30000);


    }


    private void getDeviceLocationForMap() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            lastKnownLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);
                            //Toast.makeText(MapActivity.this, "Devices Current Location Lat: " + currentLocation.getLatitude() + " Long: " + currentLocation.getLongitude() , Toast.LENGTH_SHORT).show();

                        } else {
                            Log.d(TAG, "onComplete: Current location is null");
                            //Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private Location getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            lastKnownLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);

                            // Toast.makeText(MapActivity.this, "Devices Current Location Lat: " + currentLocation.getLatitude() + " Long: " + currentLocation.getLongitude() , Toast.LENGTH_SHORT).show();

                        } else {
                            Log.d(TAG, "onComplete: Current location is null");
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
        return lastKnownLocation;
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to lat:" + latLng.latitude + ", long: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap() {
        Log.d(TAG, "initMap: Initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);


    }

    private void getLocationPermission() {

        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();

            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    //initialize map
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;
                    initMap();
                }
            }
        }

    }


    public void sendSMS(Location lastKnownLocation) {

        String message = "" + firstName + " " + surname + " has possibly been in an accident. Their last known location was https://www.google.com/maps/?q=" + lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude() + "";

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(ecContactNumber, null, message, sentPI, deliveredPI);
        Toast.makeText(this, "Sent SMS", Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onResume() {
        super.onResume();

        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {


                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent", Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic Failure", Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "No Service", Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "Null PDU", Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio off", Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        };

        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {


                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS Delivered", Toast.LENGTH_SHORT).show();
                        break;

                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "SMS Not delivered", Toast.LENGTH_SHORT).show();


                }
            }
        };

        registerReceiver(smsSentReceiver, new IntentFilter(SENT));
        registerReceiver(smsDeliveredReceiver, new IntentFilter(DELIVERED));
    }

    public void updatePossibleCrash(String crashStatus, float shakeAtReading) {

        Date currentTime = Calendar.getInstance().getTime();
        databasePossibleCrash = FirebaseDatabase.getInstance().getReference("PossibleCrash").child(userID).push();

        PossibleCrash pCrash = new PossibleCrash(email, lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), currentTime, shakeAtReading, crashStatus);

        databasePossibleCrash.setValue(pCrash);


    }
}
