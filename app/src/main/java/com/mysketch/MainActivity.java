package com.mysketch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.benjamin.git.MySketch.R;

import java.util.ArrayList;


/***
 * The MainActivity class contains the main options for the user when using MySketch.
 * The user has two options; adding a new project or loading an existing one.
 *
 * Both options takes the user to the SketchActivity b
 *
 *
 *
 */


public class MainActivity extends AppCompatActivity {

    static final String PROJECT_NAME_KEY = "currentProject";

    private static final String SAVE_DIALOG_STATE_KEY = "mDialogState";

    private int mDialogState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button newProject = (Button) findViewById(R.id.btn_new);
        assert newProject != null;
        newProject.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                makeNewProject();
            }
        });

        final Button loadProject = (Button) findViewById(R.id.btn_load);
        assert loadProject != null;
        loadProject.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
    }

    private void makeNewProject() {

        final EditText input = new EditText(getApplicationContext());
        input.setTextColor(Color.BLACK);
        input.setText("");

        new AlertDialog.Builder(this)
            .setTitle(getResources().getString(R.string.new_project_title))
            .setMessage(" ") //padding
            .setView(input)
            .setPositiveButton(R.string.default_enter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String title = input.getEditableText().toString();
                    if(!DataManager.newProject(title)){
                        Toast.makeText(getApplicationContext(),"\""+title+"\" "+getResources().getString(R.string.project_name_unavailable),Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                        makeNewProject();
                        return;
                    }
                    Intent loadNew = new Intent(MainActivity.this, SketchActivity.class);
                    loadNew.putExtra(PROJECT_NAME_KEY, title);
                    startActivity(loadNew);
                    Toast.makeText(getApplicationContext(),"\""+title+"\" "+getResources().getString(R.string.project_created),Toast.LENGTH_SHORT).show();

                    mDialogState = 0;
                }})
            .setNegativeButton(R.string.default_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    mDialogState = 0;
                }})
            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    mDialogState = 0;
                }})
            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    mDialogState = 0;
                }
            })
            .show();

        mDialogState = 1;
    }

    private void showFileChooser() {
        final ArrayList<String> items = DataManager.getAllNamesOfProjects();

        if(items != null && items.size() > 0) {

            final ArrayAdapter<String> arrayAdapterItems = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, items);

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.load_project_title))
                .setAdapter(arrayAdapterItems, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int position) {
                        String title = items.get(position);
                        Intent load = new Intent(MainActivity.this, SketchActivity.class);
                        load.putExtra(PROJECT_NAME_KEY, title);
                        startActivity(load);
                        Toast.makeText(getApplicationContext(), "\"" + title + "\" " + getResources().getString(R.string.project_loaded), Toast.LENGTH_SHORT).show();
                        mDialogState = 0;
                    }
                })
                .setPositiveButton(R.string.default_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mDialogState = 0;
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                        mDialogState = 0;
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        mDialogState = 0;
                    }
                });
            final AlertDialog alertDialog = alertBuilder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    public void onShow(DialogInterface dialog) {
                        final ListView lv = alertDialog.getListView();
                        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                                new AlertDialog.Builder(MainActivity.this)
                                    .setTitle(getResources().getString(R.string.delete_project_title))
                                    .setMessage(getResources().getString(R.string.delete_project_sub_title)+" \""+items.get(position)+"\"?")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            DataManager.deleteProject(items.get(position));
                                            Toast.makeText(getApplicationContext(), "\"" + items.get(position) + "\" " + getResources().getString(R.string.project_deleted), Toast.LENGTH_SHORT).show();
                                            items.remove(position);
                                            arrayAdapterItems.notifyDataSetChanged();
                                            if(items.size() == 0){
                                                alertDialog.dismiss();
                                            }
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null)
                                    .show();
                                return true;
                            }
                        });
                    }
                });
            alertDialog.show();

            mDialogState = 2;
        }
        else{
            Toast.makeText(getApplicationContext(), R.string.no_projects_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mDialogState = savedInstanceState.getInt(SAVE_DIALOG_STATE_KEY);
        switch (mDialogState){
            case 1: makeNewProject(); break;
            case 2: showFileChooser(); break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVE_DIALOG_STATE_KEY, mDialogState);
        super.onSaveInstanceState(outState);
    }
}
