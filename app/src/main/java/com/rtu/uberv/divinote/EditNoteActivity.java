package com.rtu.uberv.divinote;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.rtu.uberv.divinote.fragments.DatePickerFragment;
import com.rtu.uberv.divinote.fragments.TimePickerFragment;
import com.rtu.uberv.divinote.models.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.OnClick;

public class EditNoteActivity extends AppCompatActivity
        implements DatePickerFragment.OnDatePickedListener , TimePickerFragment.OnTimePickedListener{
    // TODO activity flags

    public static final String LOG_TAG = EditNoteActivity.class.getSimpleName();
    public static final String KEY_NOTE = "KEY_NOTE";
    public static final String KEY_SAVED_NOTE = "KEY_SAVED_NOTE";
    public static final long MIN_REMIND_TIME_DIFF=1000*60*15; // 15 minutes

    // Views
    private ImageButton pickDateIb;
    private EditText mTitleEt;
    private EditText mContentEt;
    private TextView mRemindAtTv;
    private Button mSaveBtn;
    private View.OnClickListener mSaveBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // capture note state from views
            Note tmpNote = new Note();
            tmpNote.setTitle(mTitleEt.getText().toString());
            tmpNote.setContent(mContentEt.getText().toString());
            tmpNote.setUpdatedAt(System.currentTimeMillis());
            // Todo validate
            if(verifyNote(tmpNote)){
                note=tmpNote;
                // save to db
                // notify user
                // move to main activity/result
                Toast.makeText(EditNoteActivity.this, "Note has been saved!", Toast.LENGTH_SHORT).show();
            }else{
                // Notify user about errors
                Toast.makeText(EditNoteActivity.this, "Please fix errors", Toast.LENGTH_SHORT).show();
            }
        }
    };
    // Fields
    private Note note;
    private int year, month,day,hour,minute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        // TODO refactor view id names
        mSaveBtn = (Button) findViewById(R.id.edit_note_save_btn);
        mSaveBtn.setOnClickListener(mSaveBtnClickListener);
        mContentEt= (EditText) findViewById(R.id.noteContentEt);
        mTitleEt= (EditText) findViewById(R.id.noteTitleEt);
        mRemindAtTv= (TextView) findViewById(R.id.edit_note_remind_at_tv);

        pickDateIb = (ImageButton) findViewById(R.id.pickDateIb);
        pickDateIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO disable прошедшее время/past
                onPickDate();
            }
        });

        // TODO different actions depending on intent arguments
        String toolbarTitle = null;
        if (savedInstanceState == null) {
            // no saved note => try to get passed with an intent
            note = getIntent().getParcelableExtra(KEY_NOTE);
            if (note != null) {
                toolbarTitle = "Edit note";
            }
        } else {
            // getIntent() persists the Note object we are passed, however new note is not saved that way...
            // as well as edits are not saved either
            note = savedInstanceState.getParcelable(KEY_SAVED_NOTE);
            if (note != null) {
                // TODO handle if we are editing or creating a new note
                toolbarTitle = "Create/Edit a note";
            }
        }
        if (note == null) {
            // no Note saved nor we did not receive any to edit
            // Create a new Note
            toolbarTitle = "Create a note";
            note = new Note();
            long createdAtMillis = System.currentTimeMillis();
            note.setCreatedAt(createdAtMillis);
        }

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(toolbarTitle);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
    }

    private boolean verifyNote(Note note){
        boolean hasErrors=false;
        View focusedView=null;

        // clear previous errors
        mContentEt.setError(null);
        mRemindAtTv.setError(null);
        mTitleEt.setError(null);

        // TODO better validation rules
        if(note.getContent()==null || note.getContent().isEmpty()){
            hasErrors=true;
            focusedView=mContentEt;
            mContentEt.setError("Note content can't be empty!");
        }
        if(note.getRemindAt()>-1 && note.getRemindAt()<System.currentTimeMillis()+MIN_REMIND_TIME_DIFF){
            hasErrors=true;
            focusedView=mRemindAtTv;
            // TODO use another textview for error
            mRemindAtTv.setError("Cannot set a reminder for time less than 15 minutes");
        }
        if(note.getTitle()==null || note.getTitle().isEmpty()){
            hasErrors=true;
            focusedView=mTitleEt;
            mTitleEt.setError("Note title can't be empty!");
        }

        if(focusedView!=null){
            focusedView.requestFocus();
        }

        return !hasErrors;
    }

    private void onPickDate() {
        DatePickerFragment newFragment = new DatePickerFragment();
        // check if we have previous remind-at value and use this as default then
        if(note.getRemindAt()!=-1){
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(note.getRemindAt());
            newFragment.setDefaultDate(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
        }
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_SAVED_NOTE, note);
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
        // TODO calcule current time +15min+5min as default
        // TODO or use note's time
        this.year=year;
        this.month=month;
        this.day=day;

        TimePickerFragment timeFragment = new TimePickerFragment();
        if(note.getRemindAt()!=-1){
            Calendar c = Calendar.getInstance();
            if(note.getRemindAt()<System.currentTimeMillis()+MIN_REMIND_TIME_DIFF){
                // TODO check if less than 15 min and add missing till 15 + 5
                c.setTimeInMillis(System.currentTimeMillis()+MIN_REMIND_TIME_DIFF+5*60*1000);
            }else{
                c.setTimeInMillis(note.getRemindAt());
            }
            timeFragment.setDefaultTime(c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE));
        }

        timeFragment.show(getSupportFragmentManager(),"timePicker");
    }

    @Override
    public void onTimePicked(int hourOfDay, int minute) {
        this.hour=hourOfDay;
        this.minute=minute;
        // calculate millis
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(year,month,day,hour,minute);
        Log.d(LOG_TAG,c.getTimeInMillis()+"");
        note.setRemindAt(c.getTimeInMillis());
        // update ui
        DateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy hh:mm");
        mRemindAtTv.setText(dateFormat.format(c.getTime()));
        // TODO add ability ro remove remindAt field
    }
}
