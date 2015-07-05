package com.laxo.lofaif.favirand;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class EditContact extends Activity {

    int contactId;
    String oldContactName;
    int oldContactNumCalls;
    int oldContactNumMessages;
    EditText editContactName;
    EditText editContactPhone;
    EditText editContactEmail;
    MyDBHandler dbHandler;
    private Typeface Thin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        //get Edit Texts
        editContactName = (EditText) findViewById(R.id.editContactName);
        editContactPhone = (EditText) findViewById(R.id.editContactPhone);
        editContactEmail = (EditText) findViewById(R.id.editContactEmail);

        //loading the fonts and apply them
        Thin = Typeface.createFromAsset(getAssets(), "fonts/MyriadSetPro-Thin.ttf");
        TextView contactsText = (TextView) findViewById(R.id.editContactText);
        contactsText.setTypeface(Thin);

        //getting the user
        Contact contactToEdit = (Contact) getIntent().getSerializableExtra("contactToEdit");

        //set old contact name
        oldContactName = contactToEdit.getName().toString();
        oldContactNumCalls = contactToEdit.getNum_calls();
        oldContactNumMessages = contactToEdit.getNum_messages();

        //setting the fields
        contactId = contactToEdit.get_id();
        editContactName.setText(contactToEdit.getName().toString());
        editContactPhone.setText("0"+contactToEdit.getNumber());
        editContactEmail.setText(contactToEdit.getEmail().toString());

        //we are letting the main class taking care of init
        dbHandler = new MyDBHandler(this, null, null, 1);
    }

    //Edit contact clicked
    public void editContactClicked(View v) {

        String name = editContactName.getText().toString();
        long number = Long.parseLong(editContactPhone.getText().toString());
        String email = editContactEmail.getText().toString();

        if(name.matches("") || editContactPhone.getText().toString().matches("")) {
            Toast.makeText(getApplicationContext(), "empty field !",
                    Toast.LENGTH_LONG).show();
        }

        else {
            //edit contact , we need to pass
            Contact contact = new Contact(name,number,email,0,0,0);

            dbHandler.editContact(oldContactName, contact);

            //go to main activity after editing the contact
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }


    public void GoToMainActivity(View v) {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

}
