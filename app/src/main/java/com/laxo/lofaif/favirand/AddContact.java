package com.laxo.lofaif.favirand;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.LongSparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

public class AddContact extends Activity {

    EditText contactName;
    EditText contactPhone;
    EditText contactEmail;
    ProgressBar progressBar;
    MyDBHandler dbHandler;
    private Typeface Thin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        //get relevant view
        contactName = (EditText) findViewById(R.id.addContactName);
        contactPhone = (EditText) findViewById(R.id.addContactPhone);
        contactEmail = (EditText) findViewById(R.id.addContactEmail);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //loading the fonts and apply them
        Thin = Typeface.createFromAsset(getAssets(), "fonts/MyriadSetPro-Thin.ttf");
        TextView contactsText = (TextView) findViewById(R.id.newContactText);
        contactsText.setTypeface(Thin);

        dbHandler = new MyDBHandler(this, null, null, 1);
    }


    public void addContactClicked(View v) {

        String name = contactName.getText().toString();

        String phone = contactPhone.getText().toString();
        phone = phone.replace("+","");
        phone = phone.replace("-","");
        phone = phone.replace(" ","");

        String email = contactEmail.getText().toString();
        email = email.replace(" ","");

        if(name.matches("") || phone.matches("")) {
            Toast.makeText(getApplicationContext(), "empty field !",
                    Toast.LENGTH_LONG).show();
        }

        else {
            Contact contact = new Contact(name, Integer.parseInt(phone), email, 0, 0, 0);

            dbHandler.addContact(contact);

            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

    private static final String[] PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Email.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Email.DATA
    };

    public void ImportAllContacts(View v) {

        progressBar.setVisibility(View.VISIBLE);

        Thread temp = new Thread(){

            @Override
            public void run(){
                //Do everything you need
                List<AddressBookContact> list = new LinkedList<AddressBookContact>();
                LongSparseArray<AddressBookContact> array = new LongSparseArray<AddressBookContact>();
                long start = System.currentTimeMillis();

                String[] projection = {
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.Data.CONTACT_ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Contactables.DATA,
                        ContactsContract.CommonDataKinds.Contactables.TYPE,
                };
                String selection = ContactsContract.Data.MIMETYPE + " in (?, ?)";
                String[] selectionArgs = {
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                };
                String sortOrder = ContactsContract.Contacts.SORT_KEY_ALTERNATIVE;

                Uri uri = ContactsContract.Data.CONTENT_URI;

                Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

                final int mimeTypeIdx = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);
                final int idIdx = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);
                final int nameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int dataIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DATA);
                final int typeIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.TYPE);

                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idIdx);
                    AddressBookContact addressBookContact = array.get(id);
                    if (addressBookContact == null) {
                        addressBookContact = new AddressBookContact(id, cursor.getString(nameIdx), getResources());
                        array.put(id, addressBookContact);
                        list.add(addressBookContact);
                    }
                    int type = cursor.getInt(typeIdx);
                    String data = cursor.getString(dataIdx);
                    String mimeType = cursor.getString(mimeTypeIdx);
                    if (mimeType.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                        addressBookContact.addEmail(type, data);
                    } else {
                        addressBookContact.addPhone(type, data);
                    }
                }
                long ms = System.currentTimeMillis() - start;
                cursor.close();


                LongSparseArray<String> phones;
                LongSparseArray<String> emails;

                for (AddressBookContact addressBookContact : list) {
                    String phone = "";
                    String email = "";
                    String name = addressBookContact.getName();

                    phones = addressBookContact.getPhones();
                    emails = addressBookContact.getEmails();

                    if(phones != null) {
                        //get first phone
                        phone = phones.valueAt(0);
                        phone = phone.replace("+212","");
                        phone = phone.replace("-","");
                        phone = phone.replace(" ","");
                    }

                    if(emails != null) {
                        //get first email
                        email = emails.valueAt(0);
                        email = email.replace(" ","");
                    }

                    if(name != "" && phone != "") {

                        //here we will import users that have at least a name and a phone number
                        Contact contactToAdd = new Contact(name , Long.parseLong(phone) , email,0,0,0);

                        dbHandler.addContact(contactToAdd);

                    }
                }

                Intent i = new Intent(AddContact.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();

            }

        };
        temp.start();

    }


    public void GoToMainActivity(View v) {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

}
