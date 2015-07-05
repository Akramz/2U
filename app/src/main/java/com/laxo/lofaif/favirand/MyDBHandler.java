package com.laxo.lofaif.favirand;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

public class MyDBHandler extends SQLiteOpenHelper {

    //responsible for everything related to the database !

    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "contactDB.db";
    public static final String TABLE_CONTACTS = "contacts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CONTACT_NAME = "name";
    public static final String COLUMN_CONTACT_NUMBER = "number";
    public static final String COLUMN_CONTACT_EMAIL = "email";
    public static final String COLUMN_CONTACT_NUM_CALLS = "num_calls";
    public static final String COLUMN_CONTACT_NUM_MESSAGES = "num_messages";
    public static final String COLUMN_CONTACT_NUM_EMAILS = "num_emails";


    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        //house keeping stuff
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //first time we run this , this will execute
        String query = "CREATE TABLE " + TABLE_CONTACTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CONTACT_NAME + " TEXT UNIQUE , " +
                COLUMN_CONTACT_NUMBER + " INTEGER UNIQUE , " +
                COLUMN_CONTACT_EMAIL + " TEXT ," +
                COLUMN_CONTACT_NUM_CALLS + " INTEGER DEFAULT 0 ," +
                COLUMN_CONTACT_NUM_MESSAGES + " INTEGER DEFAULT 0 ," +
                COLUMN_CONTACT_NUM_EMAILS + " INTEGER DEFAULT 0 " +
                ");";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS + ";");
        onCreate(db);
    }


    // add a new row to the database
    public void addContact(Contact contact){
        //set a bunch of different values and inserting them in a row
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTACT_NAME , contact.getName());
        values.put(COLUMN_CONTACT_NUMBER , contact.getNumber());
        values.put(COLUMN_CONTACT_EMAIL , contact.getEmail());
        values.put(COLUMN_CONTACT_NUM_CALLS , 0);
        values.put(COLUMN_CONTACT_NUM_MESSAGES , 0);
        values.put(COLUMN_CONTACT_NUM_EMAILS , 0);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }

    // update a contact row
    public void editContact(String oldContactName , Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_CONTACT_NAME , contact.getName());
        args.put(COLUMN_CONTACT_NUMBER , contact.getNumber());
        args.put(COLUMN_CONTACT_EMAIL, contact.getEmail());

        db.update(TABLE_CONTACTS, args, COLUMN_CONTACT_NAME + "=\"" + oldContactName + "\";" , null);
    }

    //increment the num_calls
    public void incrementNumCalls(String contactName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_CONTACTS + " SET " + COLUMN_CONTACT_NUM_CALLS + "=" + COLUMN_CONTACT_NUM_CALLS + "+1" + " WHERE " + COLUMN_CONTACT_NAME + "=?";
        db.execSQL(query,new String[] { String.valueOf(contactName) } );
    }

    //increment the num_messages
    public void incrementNumMessages(String contactName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_CONTACTS + " SET " + COLUMN_CONTACT_NUM_MESSAGES + "=" + COLUMN_CONTACT_NUM_MESSAGES + "+1" + " WHERE " + COLUMN_CONTACT_NAME + "=?";
        db.execSQL(query,new String[] { String.valueOf(contactName) } );
    }

    //increment the num_messages
    public void incrementNumEmails(String contactName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_CONTACTS + " SET " + COLUMN_CONTACT_NUM_EMAILS + "=" + COLUMN_CONTACT_NUM_EMAILS + "+1" + " WHERE " + COLUMN_CONTACT_NAME + "=?";
        db.execSQL(query,new String[] { String.valueOf(contactName) } );
    }

    //delete a contact from the database
    public void deleteContactByName(String name){
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_CONTACT_NAME + "=\"" + name + "\";";
        db.execSQL(query);
        db.close();
    }

    public List<Contact> getContacts() {

        //the list we are gonna return !
        List<Contact> contacts = new ArrayList<Contact>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT *,(num_calls+num_messages) AS connections FROM " + TABLE_CONTACTS + " ORDER BY connections DESC ;";

        //Cursor point to a location in your results
        Cursor c = db.rawQuery(query , null);

        //move to first row
        c.moveToFirst();

        while(!c.isAfterLast()) {
            if(c.getString(c.getColumnIndex("name")) != null) {

                Contact contact = new Contact(
                        c.getString(c.getColumnIndex("name")),
                        Long.parseLong(c.getString(c.getColumnIndex("number"))),
                        c.getString(c.getColumnIndex("email")),
                        Integer.parseInt(c.getString(c.getColumnIndex("num_calls"))),
                        Integer.parseInt(c.getString(c.getColumnIndex("num_messages"))),
                        Integer.parseInt(c.getString(c.getColumnIndex("num_emails")))
                );
                contacts.add(contact);
            }
            c.moveToNext();
        }
        db.close();
        return contacts;
    }

    //get contacts with emails
    public List<Contact> getContactsWithEmails() {

        //the list we are gonna return !
        List<Contact> contacts = new ArrayList<Contact>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT *,(num_emails) AS connections FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_CONTACT_EMAIL + " != '' ORDER BY connections DESC ;";

        //Cursor point to a location in your results
        Cursor c = db.rawQuery(query , null);

        //move to first row
        c.moveToFirst();

        while(!c.isAfterLast()) {
            if(c.getString(c.getColumnIndex("name")) != null) {

                Contact contact = new Contact(
                        c.getString(c.getColumnIndex("name")),
                        Long.parseLong(c.getString(c.getColumnIndex("number"))),
                        c.getString(c.getColumnIndex("email")),
                        Integer.parseInt(c.getString(c.getColumnIndex("num_calls"))),
                        Integer.parseInt(c.getString(c.getColumnIndex("num_messages"))),
                        Integer.parseInt(c.getString(c.getColumnIndex("num_emails")))
                );
                contacts.add(contact);
            }
            c.moveToNext();
        }
        db.close();
        return contacts;
    }


    public List<Contact> searchByName(String partOfName) {

        //the list we are gonna return !
        List<Contact> contacts = new ArrayList<Contact>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT *,(num_calls+num_messages) AS connections FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_CONTACT_NAME + " LIKE '" + partOfName + "%' ORDER BY connections DESC ;";

        //Cursor point to a location in your results
        Cursor c = db.rawQuery(query , null);

        //move to first row
        c.moveToFirst();

        while(!c.isAfterLast()) {
            if(c.getString(c.getColumnIndex("name")) != null) {

                Contact contact = new Contact(
                        c.getString(c.getColumnIndex("name")),
                        Long.parseLong(c.getString(c.getColumnIndex("number"))),
                        c.getString(c.getColumnIndex("email")),
                        Integer.parseInt(c.getString(c.getColumnIndex("num_calls"))),
                        Integer.parseInt(c.getString(c.getColumnIndex("num_messages"))),
                        Integer.parseInt(c.getString(c.getColumnIndex("num_emails")))

                );
                contacts.add(contact);
            }
            c.moveToNext();
        }
        db.close();
        return contacts;
    }


    public List<Contact> searchByNameWithEmail(String partOfName) {

        //the list we are gonna return !
        List<Contact> contacts = new ArrayList<Contact>();

        SQLiteDatabase db = getWritableDatabase();
        String query =
                "SELECT *,(num_emails) AS connections FROM " + TABLE_CONTACTS + " WHERE "
                        + COLUMN_CONTACT_NAME + " LIKE '" + partOfName + "%' AND "
                        + COLUMN_CONTACT_EMAIL + " != '' "
                        + " ORDER BY connections DESC ; ";

        //Cursor point to a location in your results
        Cursor c = db.rawQuery(query , null);

        //move to first row
        c.moveToFirst();

        while(!c.isAfterLast()) {
            if(c.getString(c.getColumnIndex("name")) != null) {

                Contact contact = new Contact(
                        c.getString(c.getColumnIndex("name")),
                        Long.parseLong(c.getString(c.getColumnIndex("number"))),
                        c.getString(c.getColumnIndex("email")),
                        Integer.parseInt(c.getString(c.getColumnIndex("num_calls"))),
                        Integer.parseInt(c.getString(c.getColumnIndex("num_messages"))),
                        Integer.parseInt(c.getString(c.getColumnIndex("num_emails")))
                );
                contacts.add(contact);
            }
            c.moveToNext();
        }
        db.close();
        return contacts;
    }

}
