package com.laxo.lofaif.favirand;

/*
  for displaying the items in the list
*/

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

//array adapter for contacts
public class CustomAdapter extends ArrayAdapter<Contact> {

    /* declarations for animations */
    HashMap<Contact, Integer> mIdMap = new HashMap<Contact, Integer>();
    View.OnTouchListener mTouchListener;
    //true = contacts , false = emails !
    boolean contactOrEmailList;

    public CustomAdapter(Context context, List<Contact> contacts ,View.OnTouchListener listener, boolean b) {
        super(context, R.layout.custom_contact_row, contacts);
        contactOrEmailList = b;
        mTouchListener = listener;
        for (int i = 0; i < contacts.size(); ++i) {
            mIdMap.put(contacts.get(i), i);
        }
        /* ********************** */

    }

    @Override
    public long getItemId(int position) {
        Contact item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
    /**********************************************/


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //house keeping
        // context = background information
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_contact_row, parent, false);

        // for animation
        if (customView != convertView) {
            //add animator
            customView.setOnTouchListener(mTouchListener);
        }

        //get references
        Contact singleContactItem = getItem(position);
        TextView name = (TextView) customView.findViewById(R.id.contactName);
        TextView contactConnections = (TextView) customView.findViewById(R.id.contactConnections);
        TextView extra = (TextView) customView.findViewById(R.id.contactExtra);

        //change text dynamically
        name.setText(singleContactItem.getName());

        if(contactOrEmailList == true){
            int connections = singleContactItem.getNum_calls()+singleContactItem.getNum_messages();
            extra.setText("0"+singleContactItem.getNumber());
            contactConnections.setText("(" + connections + ")");
        }
        else if (contactOrEmailList == false) {
            extra.setText(singleContactItem.getEmail());
            contactConnections.setText("(" + singleContactItem.getNum_emails() + ")");
        }

        return customView;

    }


    @Override
    public void remove(Contact object) {
        super.remove(object);
    }

}
