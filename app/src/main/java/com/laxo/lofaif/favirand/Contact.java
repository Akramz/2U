package com.laxo.lofaif.favirand;

import java.io.Serializable;

//implements to pass instances from this class in intents

public class Contact implements Serializable {

    private int _id;
    private String name;
    private long number;
    private String email;
    private int num_calls;
    private int num_messages;
    private int num_emails;

    public Contact(){}

    public Contact(String name, long number, String email, int num_calls , int num_messages , int num_emails) {
        this.name = name;
        this.number = number;
        this.email = email;
        this.num_calls = num_calls;
        this.num_messages = num_messages;
        this.num_emails = num_emails;
    }

    /* SETTERS */

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public void setNum_calls(int num_calls) {
        this.num_calls = num_calls;
    }

    public void setNum_messages(int num_messages) {
        this.num_messages = num_messages;
    }

    public void setNum_emails(int num_emails) {
        this.num_emails = num_emails;
    }

    /* GETTERS */

    public int get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public long getNumber() {
        return number;
    }

    public String getEmail() {
        return email;
    }

    public int getNum_emails() {
        return num_emails;
    }

    public int getNum_messages() {
        return num_messages;
    }

    public int getNum_calls() {
        return num_calls;
    }
}
