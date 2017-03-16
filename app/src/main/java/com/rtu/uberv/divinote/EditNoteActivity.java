package com.rtu.uberv.divinote;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.rtu.uberv.divinote.fragments.DatePickerFragment;
import com.rtu.uberv.divinote.fragments.TimePickerFragment;
import com.rtu.uberv.divinote.models.Note;

import java.util.Calendar;

public class EditNoteActivity extends AppCompatActivity
implements DatePickerFragment.OnDatePickedListener{
    // TODO activity flags

    public static final String LOG_TAG=EditNoteActivity.class.getSimpleName();
    public static final String KEY_NOTE="KEY_NOTE";
    public static final String KEY_SAVED_NOTE="KEY_SAVED_NOTE";

    // Views
    ImageButton pickDateIb;
    // Fields
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        pickDateIb = (ImageButton) findViewById(R.id.pickDateIb);
        pickDateIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickDate();
            }
        });

        // TODO different actions depending on intent arguments
        String toolbarTitle=null;
        if(savedInstanceState==null){
            // no saved note => try to get passed with an intent
            note = getIntent().getParcelableExtra(KEY_NOTE);
            if(note!=null){
                toolbarTitle="Edit note";
            }
        }else{
            // getIntent() persists the Note object we are passed, however new note is not saved that way...
            // as well as edits are not saved either
            note=savedInstanceState.getParcelable(KEY_SAVED_NOTE);
            if(note!=null){
                // TODO handle if we are editing or creating a new note
                toolbarTitle="Create/Edit a note";
            }
        }
        if(note==null){
            // no Note saved nor we did not receive any to edit
            // Create a new Note
            toolbarTitle="Create a note";
            note = new Note();
            long createdAtMillis = System.currentTimeMillis();
            note.setCreatedAt(createdAtMillis);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(createdAtMillis);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
        }

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(toolbarTitle);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
    }

    private void onPickDate() {
        DatePickerFragment newFragment = new DatePickerFragment();
        // TODO implement
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_SAVED_NOTE,note);
        super.onSaveInstanceState(outState);
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onDatePicked(int year, int month, int day) {
        Toast.makeText(this, "date picked", Toast.LENGTH_SHORT).show();
    }
}
