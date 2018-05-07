package com.example.aarondunlea.mers;
import java.util.Date;

public class Users {

    private int id;
    private String _email;
    private String _firstName;
    private String _surname;
    private String _address;
    private String _dob;
    private String _bloodType;

    public Users(){

    }
    public Users(String _email, String _firstName, String _surname, String _address, String _dob, String _bloodType) {

        this._email = _email;
        this._firstName = _firstName;
        this._surname = _surname;
        this._address = _address;
        this._dob = _dob;
        this._bloodType = _bloodType;

    }

    public int getId() {
        return id;
    }

    public String get_email() {
        return _email;
    }

    public String get_firstName() {
        return _firstName;
    }

    public String get_surname() {
        return _surname;
    }

    public String get_address() {
        return _address;
    }

    public String get_dob() {
        return _dob;
    }

    public String get_bloodType() {
        return _bloodType;
    }

    public void set_email(String _email) {

        this._email = _email;
    }

    public void set_firstName(String _firstName) {
        this._firstName = _firstName;
    }

    public void set_surname(String _surname) {
        this._surname = _surname;
    }

    public void set_address(String _address) {
        this._address = _address;
    }

    public void set_dob(String _dob) {
        this._dob = _dob;
    }

    public void set_bloodType(String _bloodType) {
        this._bloodType = _bloodType;
    }


}
