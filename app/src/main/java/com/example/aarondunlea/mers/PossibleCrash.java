package com.example.aarondunlea.mers;

import android.location.Location;

import java.util.Date;

public class PossibleCrash {



    private String _email;
    private double _lastKnownLocationLatitude;
    private double _lastKnownLocationLongitude;
    private Date _timeOfRecording;
    private float _accelerometerReading;
    private String _status;


    public PossibleCrash() {

    }

    public PossibleCrash(String _email, double _lastKnownLocationLatitude, double _lastKnownLocationLongitude, Date _timeOfRecording, float _accelerometerReading, String _status){


        this._email = _email;
        this._lastKnownLocationLatitude = _lastKnownLocationLatitude;
        this._lastKnownLocationLongitude = _lastKnownLocationLongitude;
        this._timeOfRecording = _timeOfRecording;
        this._accelerometerReading = _accelerometerReading;
        this._status = _status;

    }

    public String get_email() {
        return _email;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public double get_lastKnownLocationLatitude() {
        return _lastKnownLocationLatitude;
    }

    public void set_lastKnownLocationLatitude(double _lastKnownLocationLatitude) {
        this._lastKnownLocationLatitude = _lastKnownLocationLatitude;
    }

    public double get_lastKnownLocationLongitude() {
        return _lastKnownLocationLongitude;
    }

    public void set_lastKnownLocationLongitude(double _lastKnownLocationLongitude) {
        this._lastKnownLocationLongitude = _lastKnownLocationLongitude;
    }

    public Date get_timeOfRecording() {
        return _timeOfRecording;
    }

    public void set_timeOfRecording(Date _timeOfRecording) {
        this._timeOfRecording = _timeOfRecording;
    }

    public float get_accelerometerReading() {
        return _accelerometerReading;
    }

    public void set_accelerometerReading(float _accelerometerReading) {
        this._accelerometerReading = _accelerometerReading;
    }

    public String get_status() {
        return _status;
    }

    public void set_status(String _status) {
        this._status = _status;
    }
}
