package com.rtu.uberv.divinote;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.rtu.uberv.divinote.database.DiviNoteContract;
import com.rtu.uberv.divinote.database.DiviNoteDAO;
import com.rtu.uberv.divinote.models.Note;

import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.rtu.uberv.divinote.EditNoteActivity.KEY_NOTE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    // constants
    private static final String TAG_LICENSE_DIALOG = "TAG_LICENSE_DIALOG";

    // views
    private RecyclerView mNotesRecyclerView;

    private DiviNoteDAO mNoteDatabaseDAO;
    private CompositeSubscription subscriptions = new CompositeSubscription();
    private List<Note> mNotes;
    private List<Note> mFilteredNotes;
    private NotesFilter mNotesFilter = NotesFilter.All;
    NoteRecyclerAdapter adapter;
    private boolean isSearching = false;
    private List<Note> mFoundNotes;
    private SearchView mSearchView;
    private String searchText;
    private SearchView.OnQueryTextListener mListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            mSearchView.clearFocus();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            searchText = newText.trim().toLowerCase();
            isSearching = searchText.length() > 0;
            mFoundNotes = new ArrayList<>();
            for (Note note : mFilteredNotes) {
                if (note.getTitle().toLowerCase().contains(searchText)
                        || note.getContent().toLowerCase().contains(searchText)) {
                    mFoundNotes.add(note);
                }
            }
            adapter.setData(mFoundNotes);
            adapter.notifyDataSetChanged();
            return true;
        }
    };

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize views:
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mNotesRecyclerView = (RecyclerView) findViewById(R.id.notes_recycler_view);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // start edit activity without Note object
                startEditActivity(null);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //startEditActivity(note);

        DiviNoteDAO dao = DiviNoteDAO.getInstance(this);
        mNotes = dao.fetchAllNotes();

        // test content provider
//        ContentResolver cr = getContentResolver();
//        Cursor cursor = cr.query(DiviNoteContract.NoteTable.CONTENT_URI, null, null, null, null);
//        while (cursor.moveToNext()) {
//            Note note = DiviNoteDAO.extractNoteFromCursor(cursor);
//            Timber.d(note.toString());
//        }

        adapter = new NoteRecyclerAdapter(mNotes, this);
        adapter.setOnNoteDoneClickListener(new NoteRecyclerAdapter.OnNoteDoneClickListener() {
            @Override
            public void onNoteDoneClick(int position, View v) {
                Note note;
                if (isSearching) {
                    note = mFoundNotes.get(position);
                } else {
                    note = mFilteredNotes.get(position);
                }
                note.setCompleted(!note.isCompleted());
                DiviNoteDAO.getInstance(MainActivity.this).updateNoteById(note.getId(), note);
                updateData();
                if (!isSearching) {
                    updateUi();
                } else {
                    mListener.onQueryTextChange(searchText);
                }
            }
        });
        adapter.setOnNoteDeleteClickListener(new NoteRecyclerAdapter.OnNoteDeleteClickListener() {
            @Override
            public void onNoteDeleteClick(int position, View v) {
                Note note;
                if (isSearching) {
                    note = mFoundNotes.get(position);
                } else {
                    note = mFilteredNotes.get(position);
                }
                note.setCompleted(!note.isCompleted());
                DiviNoteDAO.getInstance(MainActivity.this).deleteNoteById(note.getId());
                updateData();
                if (!isSearching) {
                    updateUi();
                } else {
                    // refresh search
                    mListener.onQueryTextChange(searchText);
                }
            }
        });
        adapter.setOnItemClickListener(new NoteRecyclerAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                // Edit note
                Note note;
                if (isSearching) {
                    note = mFoundNotes.get(position);
                } else {
                    note = mFilteredNotes.get(position);
                }
                startEditActivity(note);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mNotesRecyclerView.setLayoutManager(layoutManager);
        mNotesRecyclerView.setAdapter(adapter);
    }

    private void updateUi() {
        switch (mNotesFilter) {
            case All:
                getSupportActionBar().setTitle("All");
                break;
            case Incomplete:
                getSupportActionBar().setTitle("Incomplete");
                break;
            case Complete:
                getSupportActionBar().setTitle("Complete");
                break;
            case Reminders:
                getSupportActionBar().setTitle("Reminders");
                break;
        }
        adapter.setData(mFilteredNotes);
        adapter.notifyDataSetChanged();
    }

    private void updateData() {
        mNotes = DiviNoteDAO.getInstance(this).fetchAllNotes();
        mFilteredNotes = new ArrayList<>();
        for (Note note : mNotes) {
            if (filterNote(note)) {
                mFilteredNotes.add(note);
            }
        }
    }

    private boolean filterNote(Note note) {
        switch (mNotesFilter) {
            case All:
                return true;
            case Incomplete:
                return note.isCompleted() == false;
            case Complete:
                return note.isCompleted() == true;
            case Reminders:
                return note.getRemindAt() > 0;
            default:
                return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateData();
        updateUi();
    }

    // pas null for new note or existing to edit
    private void startEditActivity(Note note) {
        Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
        intent.putExtra(KEY_NOTE, note);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(mListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_license) {
            showLicense();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notes_all) {
            mNotesFilter = NotesFilter.All;
            updateData();
            if (!isSearching) {
                updateUi();
            }
        } else if (id == R.id.nav_notes_incomplete) {
            mNotesFilter = NotesFilter.Incomplete;
            updateData();
            if (!isSearching) {
                updateUi();
            }
        } else if (id == R.id.nav_notes_complete) {
            mNotesFilter = NotesFilter.Complete;
            updateData();
            if (!isSearching) {
                updateUi();
            }
        } else if (id == R.id.nav_notes_reminders) {
            mNotesFilter = NotesFilter.Reminders;
            updateData();
            if (!isSearching) {
                updateUi();
            }
        }

        updateData();
        if (!isSearching) {
            updateUi();
        } else {
            mListener.onQueryTextChange(searchText);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLicense() {
        MessageDialogFragment licenseDialog = MessageDialogFragment.newInstance(getResources().getString(R.string.license));
        licenseDialog.show(getSupportFragmentManager(), TAG_LICENSE_DIALOG);
    }

    private enum NotesFilter {
        All, Complete, Incomplete, Reminders
    }
}
