package com.rtu.uberv.divinote;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.rtu.uberv.divinote.database.DiviNoteDatabaseDAO;
import com.rtu.uberv.divinote.database.DiviNoteDatabaseHelper;
import com.rtu.uberv.divinote.models.Note;

import java.util.List;

import static com.rtu.uberv.divinote.EditNoteActivity.KEY_NOTE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    // constants
    private static final String TAG_LICENSE_DIALOG = "TAG_LICENSE_DIALOG";

    // views

    // members variables
    private DiviNoteDatabaseDAO mNoteDatabaseDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize views:
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        Note note = new Note()
                .setCompleted(false)
                .setContent("Lorem ipsum porem")
                .setTitle("Lorem!")
                .setCreatedAt(System.currentTimeMillis());
        //startEditActivity(note);

        mNoteDatabaseDAO=DiviNoteDatabaseDAO.getInstance();
        mNoteDatabaseDAO.addNote(note,this);

        List<Note> notesFromDb=mNoteDatabaseDAO.getAllNotes(this);
        for(Note n:notesFromDb){
            Log.d(LOG_TAG,n.toString());
        }
    }

    // pas null for new note or existing to edit
    private void startEditActivity(Note note){
        Intent intent = new Intent(MainActivity.this,EditNoteActivity.class);
        intent.putExtra(KEY_NOTE,note);
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLicense() {
        MessageDialogFragment licenseDialog = MessageDialogFragment.newInstance(getResources().getString(R.string.license));
        licenseDialog.show(getSupportFragmentManager(), TAG_LICENSE_DIALOG);
    }
}
