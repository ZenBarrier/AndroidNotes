package com.zenbarrier.notes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class NoteActivity extends AppCompatActivity {
    EditText editTextNote;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        editTextNote = (EditText) findViewById(R.id.editTextNote);

        Intent parentIntent = getIntent();
        int requestCode = parentIntent.getIntExtra("requestCode",0);
        switch (requestCode){
            case MainActivity.REQUEST_CODE_NOTE:
                String draft = preferences.getString("draft", "");
                editTextNote.setText(draft);
                break;
            case MainActivity.REQUEST_CODE_EDIT:
                preferences.edit().remove("draft").commit();
                String editNote = parentIntent.getStringExtra("edit");
                editTextNote.setText(editNote);
                break;
            default:
                finish();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNoteAndFinish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onBackPressed() {
        saveDraftNote();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            saveNoteAndFinish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        saveDraftNote();
        super.onPause();
    }

    private void saveDraftNote() {
        SharedPreferences preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        preferences.edit().putString("draft",editTextNote.getText().toString()).apply();
    }

    private void saveNoteAndFinish() {
        Intent intent = new Intent();
        intent.putExtra("note", editTextNote.getText().toString());
        editTextNote.setText("");
        setResult(RESULT_OK, intent);
        finish();
    }
}
