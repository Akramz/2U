package com.laxo.lofaif.favirand;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ListView;
import android.view.MotionEvent;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity
        implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    //to work with the database
    MyDBHandler dbHandler;
    List<Contact> contacts;
    List<Contact> searchResults;

    /* for animation  */
    boolean mSwiping = false;
    boolean mItemPressed = false;
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();
    ListView ListView;
    CustomAdapter adapter;
    private SearchView searchView;
    private ImageView centerButton;
    private Typeface Thin;

    private static final int SWIPE_DURATION = 250;
    private static final int MOVE_DURATION = 150;

    /* ************* */

    //start the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //we are letting the main class taking care of init
        dbHandler = new MyDBHandler(this, null, null, 1);

        //loading the fonts and apply them
        Thin = Typeface.createFromAsset(getAssets(), "fonts/MyriadSetPro-Thin.ttf");
        TextView contactsText = (TextView) findViewById(R.id.contactsText);
        contactsText.setTypeface(Thin);

        //set SearchView
        searchView = (SearchView) findViewById(R.id.searchInput);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);

        //get contacts
        contacts = new ArrayList<Contact>();
        contacts = dbHandler.getContacts();

        //covert array to list items
        adapter = new CustomAdapter(this, contacts, mTouchListener, true);
        ListView = (ListView) findViewById(R.id.ContactsListView);
        ListView.setAdapter(adapter);

        //set a listener on click search
        centerButton = (ImageView) findViewById(R.id.emailsButton);
        searchView.setOnSearchClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        centerButton.setVisibility(View.INVISIBLE);
                    }
                }
        );

    }

    //go to the add contact activity
    public void GoToAddActivity(View view) {
        Intent i = new Intent(this, AddContact.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    //go to emails list activity
    public void GoToEmailsActivity(View v) {
        Intent i = new Intent(this, EmailsList.class);
        startActivity(i);
    }

   /** to search */

    @Override
    public boolean onClose() {
        centerButton = (ImageView) findViewById(R.id.emailsButton);
        centerButton.setVisibility(View.VISIBLE);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //we search in db here !
        String textToSearch = newText;

        searchResults = new ArrayList<Contact>();
        searchResults = dbHandler.searchByName(textToSearch);

        //covert array to list items
        adapter = new CustomAdapter(this, searchResults, mTouchListener, true);
        ListView = (ListView) findViewById(R.id.ContactsListView);
        ListView.setAdapter(adapter);

        return true;
    }





    /* for animation , listener */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        float mDownX;
        private int mSwipeSlop = -1;
        long start_time = 0 ;
        boolean message_luncher = false;
        boolean swipe_to_left = false;
        boolean swipe_to_right = false;

        @Override
        public boolean onTouch(final View v, MotionEvent event) {

            //to not set it multiple times as we press down
            if(start_time == 0 ) {
                start_time = System.nanoTime();
                message_luncher = false;
            }

            if (mSwipeSlop < 0) {
                mSwipeSlop = ViewConfiguration.get(MainActivity.this).
                        getScaledTouchSlop();
            }

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    if (mItemPressed) {
                        // Multi-item swipes not handled
                        return false;
                    }
                    mItemPressed = true;
                    mDownX = event.getX();
                    start_time = 0 ;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1);
                    v.setTranslationX(0);
                    mItemPressed = false;
                    v.setBackgroundColor(Color.WHITE);
                    start_time = 0 ;
                    break;
                case MotionEvent.ACTION_MOVE:
                {
                    v.setBackgroundColor(Color.parseColor("#6BB9F0"));
                    float x = event.getX() + v.getTranslationX();
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);
                    if (!mSwiping) {
                        if (deltaXAbs > mSwipeSlop) {
                            //we are def swiping here !
                            mSwiping = true;
                            ListView.requestDisallowInterceptTouchEvent(true);
                        }

                        /* NOT SWIPING BUT PRESSING DOWN */
                        //we send a message here !
                        if(message_luncher == false) {
                            if ((double) (System.nanoTime() - start_time) / 1000000000 > 0.2) {
                                //send a message
                                int p = ListView.getPositionForView(v);
                                long numberToSendMessage = adapter.getItem(p).getNumber();
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:0" + numberToSendMessage)));
                                String nameToUpdate = adapter.getItem(p).getName();
                                dbHandler.incrementNumCalls(nameToUpdate);
                                message_luncher = true;
                                break;
                            }
                        }


                    }

                    if (mSwiping) {

                        /* WE DEAL HERE WITH SWIPING */

                        if(deltaX > 0) {
                            //swiping to remove
                            v.setBackgroundColor(Color.parseColor("#E74C3C"));
                        }
                        else if (deltaX < 0) {
                            //swiping to edit
                            v.setBackgroundColor(Color.parseColor("#87D37C"));
                        }
                        v.setTranslationX((x - mDownX));
                        v.setAlpha(1 - deltaXAbs / v.getWidth());
                    }
                }
                break;
                case MotionEvent.ACTION_UP:
                {
                    // User let go - figure out whether to animate the view out, or back into place
                    if (mSwiping) {
                        float x = event.getX() + v.getTranslationX();
                        float deltaX = x - mDownX;
                        float deltaXAbs = Math.abs(deltaX);
                        float fractionCovered;
                        float endX;
                        float endAlpha;
                        final boolean remove;

                        if (deltaXAbs > v.getWidth() / 2) {
                            // Greater than a quarter of the width - animate it out
                            fractionCovered = deltaXAbs / v.getWidth();
                            endX = deltaX < 0 ? -v.getWidth() : v.getWidth();

                            if( endX < 0 ) {
                                swipe_to_left = true;
                                swipe_to_right = false;
                            }

                            else if( endX > 0 ) {
                                swipe_to_right = true;
                                swipe_to_left = false;
                            }

                            endAlpha = 0;
                            remove = true;
                        } else {
                            // Not far enough - animate it back
                            fractionCovered = 1 - (deltaXAbs / v.getWidth());
                            endX = 0;
                            endAlpha = 1;
                            remove = false;
                        }

                        // Animate position and alpha of swiped item
                        long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                        ListView.setEnabled(false);
                        v.animate().setDuration(duration).
                                alpha(endAlpha).translationX(endX).
                                withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Restore animated values
                                        v.setAlpha(1);
                                        v.setTranslationX(0);
                                        if (remove) {
                                            animateRemoval(ListView, v , swipe_to_right , swipe_to_left);

                                        } else {
                                            mSwiping = false;
                                            ListView.setEnabled(true);
                                        }
                                    }
                                });
                    } else {

                        //we call here!
                        if((double)(System.nanoTime() - start_time)/1000000000 <= 0.2) {
                            //call
                            int p = ListView.getPositionForView(v);
                            long numberToCall = adapter.getItem(p).getNumber();
                            String nameToUpdate = adapter.getItem(p).getName();
                            dbHandler.incrementNumCalls(nameToUpdate);
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:0"+numberToCall));
                            startActivity(callIntent);
                        }
                        v.setBackgroundColor(Color.WHITE);
                        start_time = 0 ;
                    }
                }
                mItemPressed = false;
                start_time = 0 ;
                v.setBackgroundColor(Color.WHITE);
                break;
                default:
                    return false;
            }

            return true;
        }
    };



    private void animateRemoval(final ListView listview, View viewToRemove , boolean swipe_to_right , boolean swipe_to_left) {
        int firstVisiblePosition = listview.getFirstVisiblePosition();
        for (int i = 0; i < listview.getChildCount(); ++i) {
            View child = listview.getChildAt(i);
            if (child != viewToRemove) {
                int position = firstVisiblePosition + i;
                long itemId = adapter.getItemId(position);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }

        int position = ListView.getPositionForView(viewToRemove);

        /* HERE I REMOVE the contact from the adapter */

        if(swipe_to_right == true) {
            //remove contact
            //delete from the database first
            String nameToRemove = adapter.getItem(position).getName();
            dbHandler.deleteContactByName(nameToRemove);

            // Delete the item from the adapter
            adapter.remove(adapter.getItem(position));
        }

        if(swipe_to_left == true) {
            //edit contact
            Contact contactToEdit = adapter.getItem(position);

            // Delete the item from the adapter
            adapter.remove(adapter.getItem(position));
            // lunch editor here !

            Intent i = new Intent(this, EditContact.class);
            i.putExtra("contactToEdit", contactToEdit);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }

        /* ******************************************* */

        final ViewTreeObserver observer = listview.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = listview.getFirstVisiblePosition();
                for (int i = 0; i < listview.getChildCount(); ++i) {
                    final View child = listview.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = adapter.getItemId(position);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();
                    if (startTop != null) {
                        if (startTop != top) {
                            int delta = startTop - top;
                            child.setTranslationY(delta);
                            child.animate().setDuration(MOVE_DURATION).translationY(0);
                            if (firstAnimation) {
                                child.animate().withEndAction(new Runnable() {
                                    public void run() {
                                        mSwiping = false;
                                        ListView.setEnabled(true);
                                    }
                                });
                                firstAnimation = false;
                            }
                        }
                    } else {
                        int childHeight = child.getHeight() + listview.getDividerHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        child.animate().setDuration(MOVE_DURATION).translationY(0);
                        if (firstAnimation) {
                            child.animate().withEndAction(new Runnable() {
                                public void run() {
                                    mSwiping = false;
                                    ListView.setEnabled(true);
                                }
                            });
                            firstAnimation = false;
                        }
                    }
                }
                mItemIdTopMap.clear();
                return true;
            }
        });
    }


        /* ******************************************************************* */


    //call a contact
    public void callContact() {

    }

    //send message to contact
    public void sendMessageToContact() {

    }

}
