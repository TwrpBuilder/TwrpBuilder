package com.github.TwrpBuilder.app;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.filelister.FileListerDialog;
import com.github.TwrpBuilder.filelister.OnFileSelectedListener;
import com.github.TwrpBuilder.util.SharedP;

import java.io.File;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by androidlover5842 on 10.2.2018.
 */

public class FlasherActivity extends AppCompatActivity {
    private String Recoverypath;
    private EditText editText;
    private Button button;
    private ProgressBar flasherPB;
    private boolean flashing;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flasher);
        Toolbar toolbar=findViewById(R.id.action_bar_tool);
        toolbar.setTitle(R.string.flash);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Recoverypath=preferences.getString("recoveryPath","");
        flasherPB=findViewById(R.id.pb_flasher);

        editText=findViewById(R.id.ed_select_recovery);
        button=findViewById(R.id.bt_flash);
        final FileListerDialog fileListerDialog = FileListerDialog.createFileListerDialog(this);

        fileListerDialog.setOnFileSelectedListener(new OnFileSelectedListener() {
            @Override
            public void onFileSelected(File file, String path) {
                editText.setText(file.getAbsolutePath());
            }
        });

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fileListerDialog.show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText()==null || editText.getText().equals(""))
                {
                    Snackbar.make(getCurrentFocus(),"Please select recovery",Snackbar.LENGTH_SHORT).show();
                }
                else
                {
                new FlashAsync().execute(editText.getText().toString());
                editText.setEnabled(false);
                button.setEnabled(false);
                flasherPB.setVisibility(View.VISIBLE);
                Snackbar.make(getCurrentFocus(),"Flashing ...",Snackbar.LENGTH_INDEFINITE).show();
                }

            }
        });

    }

    public class FlashAsync extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... voids) {
            System.out.println("From "+ voids[0] + " to "+ Recoverypath);
            flashing=true;
            Shell.SU.run("dd if="+voids[0]+" of="+Recoverypath);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Snackbar.make(getCurrentFocus(),"Flashed", BaseTransientBottomBar.LENGTH_SHORT).show();
            editText.setEnabled(true);
            button.setEnabled(true);
            flasherPB.setVisibility(View.GONE);
            flashing=false;
        }
    }

    @Override
    public void onBackPressed() {
        if (flashing==false) {
            super.onBackPressed();
        }
        else
        {
        Toast.makeText(getBaseContext(),"You can't exit until flashing is finished",Toast.LENGTH_SHORT).show();
        }
    }
}
