package com.zenbarrier.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences;
    ArrayList<String> notes;
    ArrayAdapter<String> adapter;
    static final int REQUEST_CODE_NOTE = 1;
    static final int REQUEST_CODE_EDIT = 2;
    int editPostition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                intent.putExtra("requestCode", REQUEST_CODE_NOTE);
                startActivityForResult(intent, REQUEST_CODE_NOTE);
            }
        });

        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        Set<String> notesSet = preferences.getStringSet("notes", null);
        if(notesSet == null){
            notes = new ArrayList<>();
        }
        else{
            notes = new ArrayList<>(notesSet);
        }

        ListView notesList = (ListView)findViewById(R.id.notesList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1 ,notes);
        notesList.setAdapter(adapter);
        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                editPostition = position;
                intent.putExtra("edit", notes.get(position));
                intent.putExtra("requestCode", REQUEST_CODE_EDIT);
                startActivityForResult(intent, REQUEST_CODE_EDIT);
            }
        });
        notesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.i("long click", position+"");
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete this note?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notes.remove(position);
                                adapter.notifyDataSetChanged();
                                preferences.edit().putStringSet("notes",new HashSet<String>(notes)).apply();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setMessage(notes.get(position))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .create().show();
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_NOTE){
            if(resultCode == RESULT_OK){
                String result = data.getStringExtra("note");
                if(result.length() > 0){
                    notes.add(result);
                    adapter.notifyDataSetChanged();
                    preferences.edit().putStringSet("notes",new HashSet<String>(notes)).apply();
                }
            }
        }
        if(requestCode == REQUEST_CODE_EDIT){
            String result = data.getStringExtra("note");
            if(resultCode == RESULT_OK){
                if(result.length() > 0){
                    notes.set(editPostition, result);
                    adapter.notifyDataSetChanged();
                    preferences.edit().putStringSet("notes",new HashSet<String>(notes)).apply();
                }
                else{
                    notes.remove(editPostition);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
