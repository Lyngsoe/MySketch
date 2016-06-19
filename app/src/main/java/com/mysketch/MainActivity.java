package com.mysketch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.benjamin.git.MySketch.R;


public class MainActivity extends AppCompatActivity {

    static final String PROJECT_NAME_KEY = "currentProject";

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
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("New Project");
        alertBuilder.setMessage("Enter Title of New Project");

        final EditText input = new EditText(getApplicationContext());
        input.setTextColor(Color.BLACK);
        input.setText("");

        alertBuilder.setView(input);

        alertBuilder.setPositiveButton("ENTER", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String title = input.getEditableText().toString();
                if(!DataManager.newProject(title)){
                    Toast.makeText(getApplicationContext(),"The Title \""+title+"\" Is Already In Use",Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                    makeNewProject();
                    return;
                }
                Intent loadNew = new Intent(MainActivity.this, SketchActivity.class);
                loadNew.putExtra(PROJECT_NAME_KEY, title);
                startActivity(loadNew);
                Toast.makeText(getApplicationContext(),"\""+title+"\" Created.",Toast.LENGTH_SHORT).show();
            }
        });
        alertBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    private void showFileChooser() {
        final String items[] = DataManager.getAllNamesOfProjects();

        if(items != null && items.length > 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Project to Load.\n.../"+DataManager.DIR);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int position) {
                    String title = items[position];
                    Intent load = new Intent(MainActivity.this, SketchActivity.class);
                    load.putExtra(PROJECT_NAME_KEY, title);
                    startActivity(load);
                    Toast.makeText(getApplicationContext(),"\""+title+"\" Loaded.",Toast.LENGTH_SHORT).show();
                }
            });
            final AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
            {
                @Override
                public void onShow(DialogInterface dialog)
                {
                    final ListView lv = alertDialog.getListView();
                    lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                        @Override
                        public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                            DataManager.deleteProject(items[position]);
                            alertDialog.dismiss();
                            showFileChooser();
                            return true;
                        }
                    });
                }
            });
            alertDialog.show();
        }
        else{
            Toast.makeText(getApplicationContext(), "No Project(s) Found", Toast.LENGTH_SHORT).show();
        }
    }

}
