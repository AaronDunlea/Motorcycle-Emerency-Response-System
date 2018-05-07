package com.example.aarondunlea.mers;

public class EmergencyContact {

    private int id;
    private String _email;
    private String _firstName;
    private String _surname;
    private String _phone;
    private String _relationship;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String get_firstName() {
        return _firstName;
    }

    public void set_firstName(String _firstName) {
        this._firstName = _firstName;
    }

    public String get_surname() {
        return _surname;
    }

    public void set_surname(String _surname) {
        this._surname = _surname;
    }

    public String get_phone() {
        return _phone;
    }

    public void set_phone(String _phone) {
        this._phone = _phone;
    }

    public String get_relationship() {
        return _relationship;
    }

    public void set_relationship(String _relationship) {
        this._relationship = _relationship;
    }

    public String get_email() {
        return _email;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public EmergencyContact() {
    }

    public EmergencyContact(String _email, String _firstName, String _surname, String _phone, String _relationship){


        this._email = _email;
        this._firstName = _firstName;
        this._surname = _surname;
        this._phone = _phone;
        this._relationship = _relationship;

    }
}
